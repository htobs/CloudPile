package cc.perlink.pojo.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Description: 修改用户密码实体类
 * @Author: htobs
 * @Date: 2024/11/15
 */
@Data
public class ChangePasswordRequest {
    @NotEmpty(message = "旧密码为空")
    private String oldPassword;
    @NotEmpty(message = "新密码为空")
    private String newPassword;
    @NotEmpty(message = "第二次密码为空")
    public String rePassword;

}
