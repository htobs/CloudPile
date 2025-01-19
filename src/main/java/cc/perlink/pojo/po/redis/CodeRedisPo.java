package cc.perlink.pojo.po.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 验证码缓存数据，存储到redis的实体类
 * @Author: htobs
 * @Date: 2024/11/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CodeRedisPo extends BaseRedisPo {
    private String code;
    private String codeUid;
    private String base64;
    private String createdAt; // 验证码创建时间
    private final Integer exp = 10; // 验证码过期时间


    @Override
    public String getRedisKey() {
        return "codes:" + codeUid;
    }

    @Override
    protected void populateRedisValueMap(Map<String, Object> redisValueMap) {
        redisValueMap.put("code", this.code);
        redisValueMap.put("codeUid", this.codeUid);
        redisValueMap.put("base64", this.base64);
        redisValueMap.put("createdAt", this.createdAt);
    }

    public static CodeRedisPo fromRedis(String json) {
        return fromRedis(json, CodeRedisPo.class);
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
