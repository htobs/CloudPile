package cc.perlink.service;

import cc.perlink.pojo.vo.Result;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
public interface KeyService {
    // 新增key
    Result<Object> createKey(Integer capacity);

    // 使用key
    Result<Object> useKey(String key,String bucketUid);

    // 删除key
    Result<Object> deleteKey(String key);

    // 查询是否使用（查询是否使用前会查询是否有效）
    Result<Object> checkKey(String key);
}
