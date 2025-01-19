package cc.perlink.service.impl;

import cc.perlink.enums.exception.BucketExceptionEnum;
import cc.perlink.enums.exception.KeyExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.mapper.BucketMapper;
import cc.perlink.mapper.KeyMapper;
import cc.perlink.mapper.UserMapper;
import cc.perlink.pojo.dto.bucket.UpdateBucketRequest;
import cc.perlink.pojo.po.mysql.Bucket;
import cc.perlink.pojo.po.mysql.Key;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.BucketService;
import cc.perlink.util.ThreadLocalUtil;
import cc.perlink.util.UUIDManagerUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Component
public class BucketServiceImpl implements BucketService {
    final Logger log = LoggerFactory.getLogger(BucketServiceImpl.class);

    @Resource // 存储桶mapper
    private BucketMapper bucketMapper;

    @Resource // 用户mapper
    private UserMapper userMapper;

    @Resource
    private KeyMapper keyMapper;

    /**
     * 创建存储桶
     * @param bucketName 桶名称
     * @return 创建成功
     */
    @Override
    public Result<Object> createBucket(String bucketName) {
        // 获取用户ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        // 从查询结果中获取最大桶数限制和已创建桶数量
        Map<String, Object> result = bucketMapper.customBucketCountSql(userId);
        if (result != null) {
            Integer maxBuckets = (Integer) result.get("max_bucket");
            Long existingBucketsCount = (Long) (result.get("bucket_count"));
            if (existingBucketsCount != null && existingBucketsCount >= maxBuckets) {
                throw new ExceptionMissing(BucketExceptionEnum.BUCKET_CREATION_LIMIT_EXCEEDED.getMessage());
            }
        }

        // 生成桶信息
        String bucketUUID = UUIDManagerUtil.generateBucketUUID(); // 桶uuid
        BigDecimal mb = new BigDecimal("1024").multiply(new BigDecimal("1024")); // 生成的1024Mb，也就是1Gb
        BigDecimal gb = new BigDecimal("20").multiply(mb); // 20Gb
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);
        bucket.setCapacity(gb); // 设置桶容量为20GB
        bucket.setCreatedAt(LocalDateTime.now());
        bucket.setUpdatedAt(LocalDateTime.now());
        bucket.setStatus(false);
        bucket.setUserId(userId);
        bucket.setBucketUid(bucketUUID);
        // 插入数据库
        bucketMapper.insert(bucket);
        return Result.success();
    }

    /**
     * 删除存储桶
     * @param bucketUid 存储桶UUID
     * @return
     */
    @Override
    public Result<Object> deleteBucket(String bucketUid) {
        QueryWrapper<Bucket> wrapper = new QueryWrapper<>();
        wrapper.eq("bucket_uid", bucketUid);
        bucketMapper.delete(wrapper);
        return Result.success();
    }

    /**
     * 更新存储桶信息
     * @param bucketRequest 包含存储桶名称和状态还有存储与uuid
     * @return 更新成功
     */
    @Override
    public Result<Object> updateBucket(UpdateBucketRequest bucketRequest) {
        UpdateWrapper<Bucket> updateWrapper = new UpdateWrapper<Bucket>().eq("bucket_uid", bucketRequest.getBucketUid());
        Bucket bucket = new Bucket();
        BeanUtils.copyProperties(bucketRequest, bucket);
        bucketMapper.update(bucket,updateWrapper);
        return Result.success();
    }

    /**
     * 根据uuid获取某一存储桶信息
     * @param bucketUid 存储桶UUID
     * @return
     */
    @Override
    public Result<Object> findBucket(String bucketUid) {
        QueryWrapper<Bucket> wrapper = new QueryWrapper<>();
        wrapper.eq("bucket_uid", bucketUid);
        return Result.success(bucketMapper.selectOne(wrapper));
    }

    /**
     * 获取所有存储桶信息
     * @param page 页码
     * @param size 每一个数据，默认10条
     * @return 查询到的数据
     */
    @Override
    public Result<Object> findBuckets(Integer page, Integer size) {
        // 设置默认分页参数
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        // 创建分页对象
        Page<Bucket> pageParam = new Page<>(page, size);
        QueryWrapper<Bucket> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return Result.success(bucketMapper.selectPage(pageParam, wrapper));
    }

    /**
     * 为存储桶扩容
     * @param key key
     * @return 扩容成功
     */
    @Override
    public Result<Object> augmentBucket(String bucketUid,String key) {
        // 查询key是否已使用
        QueryWrapper<Key> queryKeyWrapper = new QueryWrapper<Key>().eq("key_code", key);
        Key queryKeyData = keyMapper.selectOne(queryKeyWrapper);
        if (queryKeyData == null) {
            throw new ExceptionMissing(KeyExceptionEnum.INVALID_KEY.getMessage());
        }
        if (queryKeyData.getBucketUid() != null) {
            throw new ExceptionMissing(KeyExceptionEnum.KEY_ALREADY_IN_USE.getMessage());
        }
        // 获取key的信息
        BigDecimal keyCapacity = queryKeyData.getCapacity();
        QueryWrapper<Bucket> queryBucketWrapper = new QueryWrapper<Bucket>().eq("bucket_uid", bucketUid);
        Bucket queryBucketData = bucketMapper.selectOne(queryBucketWrapper);
        BigDecimal bucketCapacity = queryBucketData.getCapacity();
        BigDecimal add = bucketCapacity.add(keyCapacity);
        queryBucketData.setCapacity(add);
        // 扩容
        UpdateWrapper<Bucket> updateBucketWrapper = new UpdateWrapper<Bucket>().eq("bucket_uid", bucketUid);
        bucketMapper.update(queryBucketData, updateBucketWrapper);
        // 修改key为使用
        queryKeyData.setUpdatedAt(LocalDateTime.now());
        queryKeyData.setUsed(true);
        queryKeyData.setUserId(queryBucketData.getUserId());
        queryKeyData.setBucketUid(queryBucketData.getBucketUid());
        UpdateWrapper<Key> updateKeyWrapper = new UpdateWrapper<Key>().eq("key_code", key);
        keyMapper.update(queryKeyData, updateKeyWrapper);
        return Result.success();
    }

    /**
     * 根据桶uuid获取存储桶id
     * @param bucketUid 存储桶UUID
     * @return
     */
    @Override
    public Result<Object> findBucketId(String bucketUid) {
        QueryWrapper<Bucket> wrapper = new QueryWrapper<>();
        wrapper.eq("bucket_uid", bucketUid);
        return Result.success(bucketMapper.selectOne(wrapper).getId());
    }

}
