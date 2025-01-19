package cc.perlink.pojo.po.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 邮箱验证码缓存数据，存储到redis的实体类
 * @Author: htobs
 * @Date: 2024/11/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmailCodeRedisPo extends BaseRedisPo {
    private String code;    // 验证码
    private String email;    // 邮箱
    private String codeUid;
    private String createdAt; // 验证码创建时间
    private final Integer exp = 30; // 验证码过期时间

    @Override
    public String getRedisKey() {
        return "emailCodes:" + email;
    }

    @Override
    protected void populateRedisValueMap(Map<String, Object> redisValueMap) {
        redisValueMap.put("code", this.code);
        redisValueMap.put("email", this.email);
        redisValueMap.put("codeUid", this.codeUid);
        redisValueMap.put("createdAt", this.createdAt);
    }

    public static EmailCodeRedisPo fromRedis(String json) {
        return fromRedis(json, EmailCodeRedisPo.class);
    }

    /**
     * 获取验证码有效时间单位
     *
     * @return 单位为分钟
     */
    public TimeUnit getTimeUnit() {
        return TimeUnit.MINUTES;
    }
}
