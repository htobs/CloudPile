package cc.perlink.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 用户充值记录实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
@TableName("`recharge_records`")
public class RechargeRecord {
    @TableId(type = IdType.AUTO)
    private Integer id; // 充值记录的唯一标识，自增主键
    private Integer userId; // 与用户表的用户ID关联的外键
    private BigDecimal amount; // 充值金额，单位是元
    private LocalDateTime rechargeTime; // 充值时间
    private String paymentMethod; // 支付方式，如支付宝、微信、信用卡等
    private Boolean status; // 充值状态，1表示成功，0表示失败
    private String description; // 充值描述，比如充值活动或优惠信息
}