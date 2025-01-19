package cc.perlink.pojo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Description: 忘记密码重置验证码
 * @Author: htobs
 * @Date: 2024/11/19
 */
@Data
public class RePasswordRequest {
    @Email
    @NotEmpty(message = "邮箱为空")
    private String email;
    @NotEmpty(message = "密码为空")
    private String password;
    @NotEmpty(message = "验证码为空")
    private String code;
}
