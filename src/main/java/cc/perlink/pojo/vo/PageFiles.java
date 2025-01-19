package cc.perlink.pojo.vo;

import cc.perlink.pojo.po.redis.BaseRedisPo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageFiles extends BaseRedisPo {

    private String fileType;
    private BigDecimal fileSize;
    private String bucketUid;
    private String fileUid;
    private Date expireTime;
    private Boolean status;
    private String createdAt;

    @Override
    public String getRedisKey() {
        return "files:" + this.bucketUid + ":" + this.fileUid;
    }

    @Override
    protected void populateRedisValueMap(Map<String, Object> redisValueMap) {
        redisValueMap.put("fileType", this.fileType);
        redisValueMap.put("fileSize", this.fileSize);
        redisValueMap.put("bucketUid", this.bucketUid);
        redisValueMap.put("fileUid", this.fileUid);
        redisValueMap.put("expireTime", this.expireTime);
        redisValueMap.put("status", this.status);
        redisValueMap.put("createdAt", this.createdAt);
    }

    public static PageFiles fromRedis(String json) {
        return fromRedis(json, PageFiles.class);
    }
}