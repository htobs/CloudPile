package cc.perlink.pojo.po.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @Description: 用户登录时的token数据，存储到redis的实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TokenRedisPo extends BaseRedisPo {

    private String token;
    private String email;

    @Override
    public String getRedisKey() {
        return "tokens:" + email;
    }

    @Override
    protected void populateRedisValueMap(Map<String, Object> redisValueMap) {
        redisValueMap.put("token", this.token);
        redisValueMap.put("email", this.email);
    }

    public static TokenRedisPo fromRedis(String json) {
        return BaseRedisPo.fromRedis(json, TokenRedisPo.class);
    }

}
