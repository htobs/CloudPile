package cc.perlink.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description: Key实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
@TableName("`keys`")
public class Key {
    @TableId(type = IdType.AUTO)
    private Integer id; // Key的唯一标识，自增主键
    private String keyCode;            // key的代码
    private BigDecimal capacity;      // key绑定的容量
    private Integer userId;         // 被使用的用户ID
    private String bucketUid;      // 被使用存储桶uuid
    private Boolean used;          // 是否被使用
    private LocalDateTime createdAt;        // 创建时间
    private LocalDateTime updatedAt;        // 最后使用时间

}