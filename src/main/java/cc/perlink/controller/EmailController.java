package cc.perlink.controller;

import cc.perlink.pojo.vo.Result;
import cc.perlink.service.EmailService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/19
 */
@Validated
@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Resource
    private EmailService emailService;

    // 发送验证码
    @SneakyThrows
    @GetMapping("sendCode")
    public Result<Object> sendCode(@RequestParam
                                   @NotNull(message = "未知模板")
                                   @NotEmpty(message = "未知模板") String template,
                                   @RequestParam
                                   @NotNull(message = "邮箱为空")
                                   @NotEmpty(message = "邮箱为空") String email
    ) {
        return emailService.sendRePasswordCode(template, email, "账户验证码");
    }

    // 发送邮件
    @SneakyThrows
    @GetMapping("sendEmail")
    public Result<Object> sendEmail(@RequestParam
                                    @NotNull(message = "未知模板")
                                    @NotEmpty(message = "未知模板") String template,
                                    @RequestParam
                                    @NotNull(message = "邮箱为空")
                                    @NotEmpty(message = "邮箱为空") String email,
                                    @RequestParam
                                    @NotNull(message = "主题为空")
                                    @NotEmpty(message = "主题为空") String subject
    ) {
        return emailService.sendEmail(template, email, subject);
    }

}
