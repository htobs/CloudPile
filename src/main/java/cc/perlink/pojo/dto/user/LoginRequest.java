package cc.perlink.pojo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Data
public class LoginRequest {
    @Email
    @NotEmpty(message = "邮箱为空")
    private String email;
    @NotEmpty(message = "密码为空")
    private String password;
}
