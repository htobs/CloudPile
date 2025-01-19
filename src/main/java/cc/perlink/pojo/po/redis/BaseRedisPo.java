package cc.perlink.pojo.po.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseRedisPo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 将ObjectMapper定义为静态的，确保整个应用中使用的是同一个实例
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 返回Redis的key
     *
     * @return Redis key
     */
    public abstract String getRedisKey();

    /**
     * 返回Redis的值（JSON格式字符串）
     *
     * @return Redis value
     */
    public String getRedisValue() {
        Map<String, Object> redisValueMap = new HashMap<>();
        this.populateRedisValueMap(redisValueMap);
        try {
            return objectMapper.writeValueAsString(redisValueMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用于填充Redis value的Map，子类需要实现这个方法
     *
     * @param redisValueMap Redis值的map
     */
    protected abstract void populateRedisValueMap(Map<String, Object> redisValueMap);

    /**
     * 从Redis中获取JSON并转换为实体类
     *
     * @param json JSON字符串
     * @param clazz 类型
     * @return 实体类
     */
    public static <T> T fromRedis(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);  // 使用类级别的ObjectMapper实例
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}