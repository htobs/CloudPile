package cc.perlink.pojo.po.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
/**
 * @Description: 文件缓存数据，存储到redis的实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileRedisPo extends BaseRedisPo {

    private String localPath;
    private String filename;
    private String fileType;
    private BigDecimal fileSize;
    private String bucketUid;
    private String fileUid;
    private Date expireTime;
    private String publicUrl;
    private Boolean status;
    private String createdAt;

    @Override
    public String getRedisKey() {
        // 使用桶UUID和文件UUID来生成唯一的Redis键
        return "files:" + this.bucketUid + ":" + this.fileUid;
    }

    @Override
    protected void populateRedisValueMap(Map<String, Object> redisValueMap) {
        redisValueMap.put("localPath", this.localPath);
        redisValueMap.put("filename", this.filename);
        redisValueMap.put("fileType", this.fileType);
        redisValueMap.put("fileSize", this.fileSize);
        redisValueMap.put("bucketUid", this.bucketUid);
        redisValueMap.put("fileUid", this.fileUid);
        redisValueMap.put("expireTime", this.expireTime);
        redisValueMap.put("publicUrl", this.publicUrl);
        redisValueMap.put("status", this.status);
        redisValueMap.put("createdAt", this.createdAt);
    }

    public static FileRedisPo fromRedis(String json) {
        return fromRedis(json, FileRedisPo.class);
    }
}
