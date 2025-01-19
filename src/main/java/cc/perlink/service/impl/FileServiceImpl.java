package cc.perlink.service.impl;

import cc.perlink.enums.exception.BucketExceptionEnum;
import cc.perlink.enums.exception.FileExceptionEnum;
import cc.perlink.enums.exception.UserExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.mapper.BucketMapper;
import cc.perlink.mapper.FileMapper;
import cc.perlink.mapper.UserMapper;
import cc.perlink.pojo.po.mysql.Bucket;
import cc.perlink.pojo.po.mysql.User;
import cc.perlink.pojo.po.redis.FileRedisPo;
import cc.perlink.pojo.vo.PageFiles;
import cc.perlink.pojo.vo.PageFilesResponse;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.FileService;
import cc.perlink.util.FileUtil;
import cc.perlink.util.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Component
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    @Resource
    private FileMapper fileMapper;

    @Resource
    private BucketMapper bucketMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, FileRedisPo> redisTemplate;


    private final String utf8 = "UTF-8";


    /**
     * 上传文件
     * 用户登录的状态下
     *
     * @param file      上传的文件
     * @param bucketUid 文件的uuid
     * @return
     */
    @Override
    public Result<Object> upload(MultipartFile file, String bucketUid) throws IOException {
        QueryWrapper<Bucket> queryBucketWrapper = new QueryWrapper<Bucket>().eq("bucket_uid", bucketUid);
        Bucket bucket = bucketMapper.selectOne(queryBucketWrapper);
        if (bucket == null) {
            throw new ExceptionMissing(BucketExceptionEnum.BUCKET_NOT_EXIST.getMessage());
        }
        FileUtil util = new FileUtil();
        Map<String, Object> fileInfo = FileUtil.getFileInfo(file, bucket);
        // 查看桶内存是否足够
        if (!(boolean) fileInfo.get("uploadCondition")) { // 空间不足
            throw new ExceptionMissing(BucketExceptionEnum.BUCKET_SPACE_INSUFFICIENT.getMessage());
        }
        // 创建文件夹
        util.createDirectory(fileInfo.get("fileUploadPath").toString());
        // 上传文件
        file.transferTo(new File(fileInfo.get("filePath").toString()));
        // 更新桶内存
        BigDecimal bucketPredictCapacity = (BigDecimal) fileInfo.get("bucketPredictCapacity");
        updateBucketCapacity(bucketUid, bucketPredictCapacity);
        // redis缓存
        setRedis(fileInfo);
        log.info("{}上传成功", fileInfo.get("fileName").toString());
        return Result.success(fileInfo.get("publicUrl").toString());
    }


    /**
     * 删除文件
     * 用户登录的状态下
     *
     * @param bucketUid 存储桶uuid
     * @param fileUid  文件uuid
     */
    @Override
    public Result<Object> delete(String bucketUid, String fileUid) {
        // 查找文件
        FileRedisPo fileRedisPo = new FileRedisPo();
        fileRedisPo.setFileUid(fileUid);
        fileRedisPo.setBucketUid(bucketUid);
        FileRedisPo fileRedis = getFileRedis(bucketUid, fileUid);
        if (fileRedis == null) {
            throw new ExceptionMissing(FileExceptionEnum.FILE_NOT_EXIST.getMessage());
        }
        String queryFileRedisData = stringRedisTemplate.opsForValue().get(fileRedis.getRedisKey());
        FileRedisPo fileRedisPo1 = FileRedisPo.fromRedis(queryFileRedisData);
        // 更新存储桶
        QueryWrapper<Bucket> queryBucketWrapper = new QueryWrapper<Bucket>().eq("bucket_uid", bucketUid);
        Bucket bucket = bucketMapper.selectOne(queryBucketWrapper);
        BigDecimal capacity = bucket.getUseCapacity().subtract(fileRedisPo1.getFileSize());
        bucket.setUseCapacity(capacity);
        UpdateWrapper<Bucket> updateBucketWrapper = new UpdateWrapper<Bucket>().eq("bucket_uid", bucketUid);
        bucketMapper.update(bucket, updateBucketWrapper);
        // 删除redis
        stringRedisTemplate.delete(fileRedisPo.getRedisKey());
        // 删除本地
        String localPath = fileRedis.getLocalPath();
        File file = new File(localPath); // 替换为你的文件路径
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new ExceptionMissing(FileExceptionEnum.DELETE_FAILED.getMessage());
            }
        } else {
            throw new ExceptionMissing(FileExceptionEnum.FILE_NOT_EXIST.getMessage());
        }
        return Result.success();
    }

    /**
     * 预览文件
     *
     * @param bucketUid 存储桶uuid
     * @param fileUid   文件uuid
     */
    @Override
    public void preview(String bucketUid, String fileUid, HttpServletResponse response) throws IOException {
        // 查询是否允许访问
        QueryWrapper<Bucket> queryBucketWrapper = new QueryWrapper<Bucket>().eq("bucket_uid", bucketUid);
        Bucket bucket = bucketMapper.selectOne(queryBucketWrapper);
        if (bucket == null) {
            throw new ExceptionMissing(BucketExceptionEnum.BUCKET_NOT_EXIST.getMessage());
        }
        if (!bucket.getStatus()) {
            throw new ExceptionMissing(BucketExceptionEnum.BUCKET_ACCESS_DENIED.getMessage());
        }

        // 获取文件名
        FileRedisPo fileRedis = getFileRedis(bucketUid, fileUid);
        String filename = fileRedis.getFilename();
        // 获取文件路径
        String localPath = fileRedis.getLocalPath();
        File file = new File(localPath);
        if (!file.exists()) {
            throw new ExceptionMissing(FileExceptionEnum.FILE_NOT_EXIST.getMessage());
        }

        // 设置响应头
        response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(filename, utf8) + "\"");
        response.setContentType("application/octet-stream");

        // 读取文件并写入响应输出流
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            log.error(FileExceptionEnum.DOWNLOAD_FAILED.getMessage() + e.getMessage(), e);
            throw new ExceptionMissing(FileExceptionEnum.DOWNLOAD_FAILED.getMessage());
        } finally {
            // 确保输出流被刷新并关闭
            response.flushBuffer();
        }
    }


    /**
     * 获取当前用户下的所有文件
     * （0.0.2版本计划）
     *
     * @param bucketUid 存储桶uuid
     * @return
     * @throws IOException
     */
    @Override
    public Result<Object> findUserAllFile(String bucketUid, int page, int size) throws IOException {
        PageFiles pageFiles = new PageFiles();
        pageFiles.setBucketUid(bucketUid);
        pageFiles.setFileUid("*");
        String pattern = pageFiles.getRedisKey();

        // 使用 scan 命令进行分页查询
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
        Cursor<byte[]> cursor = stringRedisTemplate.executeWithStickyConnection(
                connection -> connection.scan(options));

        List<PageFilesResponse> pageFilesResponseList = new ArrayList<>();
        try {
            if (cursor != null) {
                while (cursor.hasNext()) {
                    String key = new String(cursor.next());
                    String value = stringRedisTemplate.opsForValue().get(key);
                    PageFiles pageFile = PageFiles.fromRedis(value);

                    // 将 PageFiles 映射为 PageFilesResponse，并排除 redisKey 和 redisValue
                    PageFilesResponse response = new PageFilesResponse();
                    response.setFileType(pageFile.getFileType());
                    response.setFileSize(pageFile.getFileSize());
                    response.setBucketUid(pageFile.getBucketUid());
                    response.setFileUid(pageFile.getFileUid());
                    response.setStatus(pageFile.getStatus());
                    response.setCreatedAt(pageFile.getCreatedAt());

                    pageFilesResponseList.add(response);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // 计算总页数
        long total = Objects.requireNonNull(stringRedisTemplate.keys(pattern)).size();
        int totalPage = (int) Math.ceil((double) total / size);

        // 分页逻辑
        int fromIndex = (page - 1) * size;
        // 处理 fromIndex 超出总数据的情况
        if (fromIndex >= pageFilesResponseList.size()) {
            return Result.success(new ArrayList<>());  // 返回空列表，表示没有数据
        }
        int toIndex = Math.min(fromIndex + size, pageFilesResponseList.size());

        List<PageFilesResponse> pagedPageFiles = pageFilesResponseList.subList(fromIndex, toIndex);

        return Result.success(pagedPageFiles);
    }


    // 将文件信息缓存到redis
    private void setRedis(Map<String, Object> fileInfo) {
        BigDecimal fileSize = new BigDecimal(fileInfo.get("fileSize").toString());
        FileRedisPo fileRedisPo = new FileRedisPo();
        fileRedisPo.setLocalPath(fileInfo.get("filePath").toString());
        fileRedisPo.setFilename(fileInfo.get("fileName").toString());
        fileRedisPo.setFileType(fileInfo.get("fileType").toString());
        fileRedisPo.setBucketUid(fileInfo.get("bucketUid").toString());
        fileRedisPo.setFileUid(fileInfo.get("fileUid").toString());
        fileRedisPo.setPublicUrl(fileInfo.get("publicUrl").toString());
        fileRedisPo.setFileSize(fileSize);
        fileRedisPo.setStatus(true);
        fileRedisPo.setCreatedAt(fileInfo.get("fileUploadTime").toString());
        stringRedisTemplate.opsForValue().set(fileRedisPo.getRedisKey(), fileRedisPo.getRedisValue());
    }

    // 获取redis当中的文件信息
    private FileRedisPo getFileRedis(String bucketUid, String fileUid) {
        FileRedisPo fileRedisPo = new FileRedisPo();
        fileRedisPo.setBucketUid(bucketUid);
        fileRedisPo.setFileUid(fileUid);
        String string = stringRedisTemplate.opsForValue().get(fileRedisPo.getRedisKey());
        return FileRedisPo.fromRedis(string);
    }

    /**
     * 更新存储桶内存
     *
     * @param bucketUid 存储桶uuid
     * @param capacity  上传文件后的内存
     */
    private void updateBucketCapacity(String bucketUid, BigDecimal capacity) {
        Bucket bucket = new Bucket();
        bucket.setUseCapacity(capacity);
        UpdateWrapper<Bucket> updateWrapper = new UpdateWrapper<Bucket>().eq("bucket_uid", bucketUid);
        bucketMapper.update(bucket, updateWrapper);
    }

    /**
     * 用户传入accessKey和secretKey检查是否有效，无效抛出异常
     *
     * @param accessKey 访问密钥
     * @param secretKey 私密密钥
     * @return 返回包含用户数据的User对象
     */
    private User checkUserKey(String accessKey, String secretKey) {
        QueryWrapper<User> queryUserWrapper = new QueryWrapper<User>().eq("access_key", accessKey).eq("secret_key", secretKey);
        User user = userMapper.selectOne(queryUserWrapper);
        if (user == null) {
            throw new ExceptionMissing(UserExceptionEnum.SECRET_KEY_INCORRECT.getMessage());
        }
        return user;

    }

}
