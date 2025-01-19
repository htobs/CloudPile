package cc.perlink.pojo.dto.user;

import lombok.Data;

/**
 * @Description: 修改用户信息实体类
 * @Author: htobs
 * @Date: 2024/11/15
 */
@Data
public class ChangeUserInfoRequest {
    private String nickName; // 用户昵称
    private String country; // 用户所在国家
    private String province; // 用户所在省
    private String language; // 用户使用语言
    private String avatarUrl; // 用户头像
}
