package cc.perlink.controller;

import cc.perlink.pojo.dto.bucket.UpdateBucketRequest;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.BucketService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 存储桶接口控制类
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Validated
@RestController
@RequestMapping("/api/bucket")
public class BucketController {
    @Resource
    private BucketService bucketService;

    // 新增存储桶
    @PostMapping("create")
    public Result<Object> createBucket(@RequestParam
                                       @NotNull(message = "存储桶名称不能为空")
                                       @NotEmpty(message = "存储桶名称不能为空") String bucketName) {
        return bucketService.createBucket(bucketName);
    }

    // 删除存储桶
    @DeleteMapping("delete")
    public Result<Object> deleteBucket(@RequestParam
                                       @NotNull(message = "存储桶uuid不能为空")
                                       @NotEmpty(message = "存储桶uuid不能为空") String bucketUid) {
        return bucketService.deleteBucket(bucketUid);
    }

    // 修改存储桶信息
    @PutMapping("update")
    public Result<Object> updateBucket(@RequestBody UpdateBucketRequest bucket) {
        return bucketService.updateBucket(bucket);
    }

    // 查询某一存储桶
    @GetMapping("find")
    public Result<Object> findBucket(@RequestParam
                                     @NotNull(message = "存储桶uuid不能为空")
                                     @NotEmpty(message = "存储桶uuid不能为空") String bucketUid) {
        return bucketService.findBucket(bucketUid);
    }

    // 查询某一用户的所有存储桶
    @GetMapping("finds")
    public Result<Object> findBuckets(@RequestParam Integer page, @RequestParam Integer size) {
        return bucketService.findBuckets(page, size);
    }

    // 增加存储桶容量
    @GetMapping("augmentBucket")
    public Result<Object> augmentBucket(@RequestParam
                                        @NotNull(message = "存储桶uuid不能为空")
                                        @NotEmpty(message = "存储桶uuid不能为空") String bucketUid,
                                        @RequestParam
                                        @NotNull(message = "Key不能为空")
                                        @NotEmpty(message = "Key不能为空")
                                        String key) {
        return bucketService.augmentBucket(bucketUid, key);
    }

    // 获取存储桶id
    @GetMapping("findBucketId")
    public Result<Object> findBucketId(@RequestParam
                                       @NotNull(message = "存储桶uuid不能为空")
                                       @NotEmpty(message = "存储桶uuid不能为空") String bucketUid) {
        return bucketService.findBucketId(bucketUid);
    }


}
