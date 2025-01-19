package cc.perlink.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 消费记录实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
@TableName("`transactions`")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Integer id; // 消费记录的唯一标识，自增主键
    private Integer userId; // 与用户表的用户ID关联的外键
    private BigDecimal amount; // 消费金额，单位是元
    private LocalDateTime transactionTime; // 消费时间
    private String description; // 消费描述，比如购买的服务或产品
}