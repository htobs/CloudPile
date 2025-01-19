package cc.perlink.pojo.po.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @Description: 用户私要和密钥存储Redis实体类
 * @Author: htobs
 * @Date: 2024/11/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserKeyRedisPo extends BaseRedisPo {
    private Integer id;
    private String email;
    private String accessKey; // 用户的AccessKey
    private String secretKey; // 用户的SecretKey

    @Override
    public String getRedisKey() {
        return "userKeys:" + id;
    }

    @Override
    protected void populateRedisValueMap(Map<String, Object> redisValueMap) {
        redisValueMap.put("id", this.id);
        redisValueMap.put("email", this.email);
        redisValueMap.put("accessKey", this.accessKey);
        redisValueMap.put("secretKey", this.secretKey);
    }

    public static UserKeyRedisPo fromRedis(String json) {
        return BaseRedisPo.fromRedis(json, UserKeyRedisPo.class);
    }
}
