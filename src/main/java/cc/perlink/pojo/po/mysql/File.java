package cc.perlink.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 文件实体类
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
@TableName("`files`")
public class File {
    @TableId(type = IdType.AUTO)
    private Integer id; // 文件的唯一标识，自增主键
    private Integer userId; // 与用户表的用户ID关联的外键
    private Integer bucketUid; // 与bucket表的bucket ID关联的外键
    private String name; // 文件名
    private String localPath; // 本地文件保存路径
    private String fileType; // 文件类型
    private LocalDateTime createdAt; // 文件的创建时间
    private LocalDateTime updatedAt; // 文件的最后一次更新时间
    private Boolean status; // 文件状态，1表示启用，0表示禁用
    private String fileUid; // 文件的UUID
    private String publicUrl; // 文件的公共访问URL
    private LocalDateTime expireTime; // 文件的公共访问URL过期时间
}