package cc.perlink.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 存储桶实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
@TableName("`buckets`")
public class Bucket {
    @TableId(type = IdType.AUTO)
    private Integer id; // Bucket的唯一标识，自增主键
    private String name; // Bucket的名字
    private String bucketUid; // Bucket的UUID
    private Boolean status; // Bucket的状态，1表示启用，0表示禁用
    private LocalDateTime createdAt; // Bucket的创建时间
    private LocalDateTime updatedAt; // Bucket的最后一次更新时间
    private Integer userId; // 与用户表的用户ID关联的外键
    private BigDecimal capacity; // Bucket的容量，默认20GB，单位KB
    private BigDecimal useCapacity; // Bucket已使用的容量，0KB，单位KB

}