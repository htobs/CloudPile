package cc.perlink.controller;

import cc.perlink.pojo.vo.Result;
import cc.perlink.service.KeyService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Key接口控制类
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Validated
@RestController
@RequestMapping("/api/key")
public class KeyController {
    @Resource
    private KeyService keyService;

    // 新增key
    @GetMapping("create")
    public Result<Object> create(@RequestParam
                                 @NotNull(message = "容量不能为空")
                                 @NotEmpty(message = "容量不能为空") Integer capacity) {
        return keyService.createKey(capacity);
    }

    // 使用key
    @GetMapping("use")
    public Result<Object> use(@RequestParam
                              @NotNull(message = "Key不能为空")
                              @NotEmpty(message = "Key不能为空") String key,
                              @RequestParam
                              @NotNull(message = "存储桶uuid不能为空")
                              @NotEmpty(message = "存储桶uuid不能为空") String bucketUid) {
        return keyService.useKey(key, bucketUid);
    }

    // 删除key
    @DeleteMapping("delete")
    public Result<Object> delete(@RequestParam
                                 @NotNull(message = "Key不能为空")
                                 @NotEmpty(message = "Key不能为空") String key) {
        return keyService.deleteKey(key);
    }


    // 查询是否使用（查询是否使用前会查询是否有效）
    @GetMapping("check")
    public Result<Object> check(@RequestParam
                                @NotNull(message = "Key不能为空")
                                @NotEmpty(message = "Key不能为空") String key) {
        return keyService.checkKey(key);
    }

}
