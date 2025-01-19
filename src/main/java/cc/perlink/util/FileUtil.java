package cc.perlink.util;

import cc.perlink.config.FileUploadConfig;
import cc.perlink.enums.exception.FileExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.pojo.po.mysql.Bucket;
import cc.perlink.pojo.po.mysql.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Getter
@Component
public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private static FileUploadConfig fileUploadConfig;

    @Autowired
    public void FileStorageService(FileUploadConfig fileUploadConfig) {
        FileUtil.fileUploadConfig = fileUploadConfig;
    }

    private static final Map<String, String> FILE_TYPE_MAP = new HashMap<>();


    static {
        // 图片类型
        FILE_TYPE_MAP.put("jpg", "image");
        FILE_TYPE_MAP.put("jpeg", "image");
        FILE_TYPE_MAP.put("png", "image");
        FILE_TYPE_MAP.put("gif", "image");
        FILE_TYPE_MAP.put("bmp", "image");
        FILE_TYPE_MAP.put("svg", "image");
        FILE_TYPE_MAP.put("ico", "image");

        // 音频类型
        FILE_TYPE_MAP.put("mp3", "audio");
        FILE_TYPE_MAP.put("wav", "audio");
        FILE_TYPE_MAP.put("aac", "audio");
        FILE_TYPE_MAP.put("flac", "audio");

        // 视频类型
        FILE_TYPE_MAP.put("mp4", "video");
        FILE_TYPE_MAP.put("avi", "video");
        FILE_TYPE_MAP.put("mkv", "video");
        FILE_TYPE_MAP.put("mov", "video");
        FILE_TYPE_MAP.put("wmv", "video");

        // 默认文件类型
        FILE_TYPE_MAP.put("", "file");
    }

    /**
     * 根据文件名获取文件类型
     *
     * @param fileName 文件名
     * @return 返回image、audio、video、file其中任意一项
     */
    public static String getFileType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "file";
        }
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return FILE_TYPE_MAP.getOrDefault(extension, "file");
    }


    /**
     * 获取文件详细信息
     *
     * @param file   上传的文件
     * @param bucket 存储桶
     * @return map集合的信息
     * @throws IOException IO异常
     */
    public static Map<String, Object> getFileInfo(MultipartFile file, Bucket bucket) throws IOException {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Map<String, Object> fileInfoMap = new HashMap<>();
        String tmpFileName = file.getOriginalFilename();
        // 文件后缀
        String[] parts = tmpFileName.split("\\.");
        String lastPart = parts[parts.length - 1];
        // 文件uuid
        String fileUid = UUIDManagerUtil.generateFileUUID(bucket.getBucketUid(), tmpFileName);
        // 文件名
        String fileName = fileUid + "." + lastPart;
        // 文件类型
        String fileType = getFileType(tmpFileName);
        // 上传者ID
        Integer userId = (Integer) claims.get("id");
        // 文件大小（单位KB）
        double fileSize = file.getSize() / 1024.0;
        // 文件上传路径
        String separator = File.separator;
        String filePath = fileUploadConfig.getUploadPath() + separator + userId + separator + bucket.getBucketUid() + separator + fileName;
        String fileUploadPath = fileUploadConfig.getUploadPath() + separator + userId + separator + bucket.getBucketUid();
        // 文件的公共访问URL
        String publicUrl = bucket.getBucketUid() + separator + fileName;


        // 获取桶总空间
        BigDecimal bucketCapacity = bucket.getCapacity();
        // 获取桶已使用空间
        BigDecimal bucketUseCapacity = bucket.getUseCapacity();
        // 桶剩余空间+上传文件后的大小
        BigDecimal bucketPredictCapacity = bucketUseCapacity.add(BigDecimal.valueOf(fileSize));
        // 判断桶剩余空间是否满足上传
        boolean uploadCondition;
        if (bucketPredictCapacity.compareTo(bucketCapacity) <= 0) {
            // 空间足够
            uploadCondition = true;
        } else {
            // 空间不足
            uploadCondition = false;
        }

        fileInfoMap.put("fileName", fileName); // 文件名称
        fileInfoMap.put("fileUid", fileUid); // 文件uuid
        fileInfoMap.put("publicUrl", publicUrl); // 文件公共访问url
        fileInfoMap.put("fileType", fileType); // 文件类型
        fileInfoMap.put("userId", userId);  // 用户ID
        fileInfoMap.put("filePath", filePath);  // 文件路径
        fileInfoMap.put("fileUploadPath", fileUploadPath); // 文件上传路径
        fileInfoMap.put("fileSize", fileSize);  // 文件大小
        fileInfoMap.put("bucketUid", bucket.getBucketUid());
        fileInfoMap.put("uploadCondition", uploadCondition); // 文件上传条件，布尔类型
        fileInfoMap.put("bucketCapacity", bucketCapacity);  // 存储桶总空间
        fileInfoMap.put("bucketUseCapacity", bucketUseCapacity); // 存储桶已使用空间
        fileInfoMap.put("bucketPredictCapacity", bucketPredictCapacity); // 上传该文件后存储桶已使用空间
        fileInfoMap.put("fileUploadTime", LocalDateTime.now()); // 文件上传时间
        return fileInfoMap;
    }


    /**
     * 检查上传的文件夹是否存在，不存在则创建。
     *
     * @param filePath 文件夹路径
     * @return 是否成功创建目录
     */
    public boolean createDirectory(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path); // 创建所有不存在的父目录
                return true;
            }
        } catch (AccessDeniedException e) {
            log.error(FileExceptionEnum.NO_PERMISSION.getMessage());
            log.error(e.getMessage());
            throw new ExceptionMissing(FileExceptionEnum.NO_PERMISSION.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

}
