package cc.perlink.service;

import cc.perlink.pojo.dto.bucket.UpdateBucketRequest;
import cc.perlink.pojo.vo.Result;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
public interface BucketService {
    // 新增存储桶
    Result<Object> createBucket(String bucketName);

    // 删除存储桶
    Result<Object> deleteBucket(String bucketUid);

    // 修改存储桶信息
    Result<Object> updateBucket(UpdateBucketRequest bucketRequest);

    // 查询某一存储桶
    Result<Object> findBucket(String bucketUid);

    // 查询某一用户的所有存储桶
    Result<Object> findBuckets(Integer page, Integer size);

    // 增加存储桶容量
    Result<Object> augmentBucket(String bucketUid,String key);


    // 获取存储桶id
    Result<Object> findBucketId(String bucketUid);
}
