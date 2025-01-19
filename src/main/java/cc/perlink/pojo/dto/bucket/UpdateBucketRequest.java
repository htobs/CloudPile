package cc.perlink.pojo.dto.bucket;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Data
public class UpdateBucketRequest {
    private String name; // Bucket的名字
    @NotEmpty(message = "桶uuid是必须的")
    private String bucketUid; // Bucket的UUID
    private Boolean status; // Bucket的状态，1表示启用，0表示禁用
}
