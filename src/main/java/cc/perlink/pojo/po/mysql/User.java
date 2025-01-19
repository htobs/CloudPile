package cc.perlink.pojo.po.mysql;

import cc.perlink.enums.UserPermission;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 用户实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
@TableName("`users`")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;             // 用户的唯一标识，自增主键
    private String email; // 用户邮箱，唯一
    private String password; // 用户密码
    private String nickName; // 用户昵称
    private BigDecimal amount; // 用户金额，单位元
    private String country; // 用户所在国家
    private String province; // 用户所在省
    private String language; // 用户使用语言
    private String avatarUrl; // 用户头像
    private LocalDateTime createdAt; // 用户注册时间
    private LocalDateTime updatedAt; // 用户最后一次更新时间
    private String permission; // 用户权限，admin 表示管理员，user 表示普通用户，banned 表示被封禁的用户
    private Integer bucketCount; // 用户创建的桶数，默认为1
    private String accessKey; // 用户的AccessKey
    private String secretKey; // 用户的SecretKey
}