package cc.perlink.service.impl;

import cc.perlink.enums.exception.EmailExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.pojo.po.redis.CodeRedisPo;
import cc.perlink.pojo.po.redis.EmailCodeRedisPo;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.EmailService;
import cc.perlink.util.GenerateCodeUtil;
import cc.perlink.util.UUIDManagerUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@SpringBootTest
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Resource
    private JavaMailSenderImpl mailSender;

    @Resource
    private TemplateEngine templateEngine;  // 注入 Thymeleaf 的 TemplateEngine

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 通用发送邮件
     *
     * @param template 需要使用的邮件模板
     * @param email    收件人
     * @param subject  邮件主题
     * @return 发送成功
     * @throws IOException IO异常
     */
    @Override
    public Result<Object> sendEmail(String template, String email, String subject) throws IOException {
        // 发送邮件
        globalSend(template, email, subject, null);
        return Result.success();
    }

    /**
     * 发送重置密码的验证码
     *
     * @param template 需要使用的邮件模板
     * @param email    收件人
     * @param subject  邮件主题
     * @return 发送成功
     * @throws IOException IO异常
     */
    @Override
    public Result<Object> sendRePasswordCode(String template, String email, String subject) throws IOException {
        // 生成验证码
        Map<String, String> codeMap = GenerateCodeUtil.outputVerifyImageBase64(400, 200, 5);
        String codeUid = UUIDManagerUtil.generateCodeUUID(codeMap.get("code"));
        codeMap.put("codeUid", codeUid);
        // 缓存redis
        EmailCodeRedisPo emailCodeRedisPo = new EmailCodeRedisPo();
        emailCodeRedisPo.setCode(codeMap.get("code"));
        emailCodeRedisPo.setCodeUid(codeUid);
        emailCodeRedisPo.setEmail(email);
        emailCodeRedisPo.setCreatedAt(String.valueOf(LocalDateTime.now()));
        stringRedisTemplate.opsForValue().set(
                emailCodeRedisPo.getRedisKey(),
                emailCodeRedisPo.getRedisValue(),
                emailCodeRedisPo.getExp(),
                emailCodeRedisPo.getTimeUnit()
        );
        // 发送邮件
        HashMap<String, String> contentMap = new HashMap<>();
        contentMap.put("code", codeMap.get("code"));
        globalSend(template, email, subject, contentMap);
        return Result.success();
    }


    /**
     * 全局发送邮件模板
     *
     * @param template   需要使用的邮件模板
     * @param email      收件人
     * @param subject    邮件主题
     * @param contentMap 发送邮件时的模板参数（如果有）
     * @throws IOException IO异常
     */
    public void globalSend(String template, String email, String subject, Map<String, String> contentMap) throws IOException {
        try {
            String content = content(template, contentMap);
            send(content, email, subject);
        } catch (Exception e) { // 邮件发送失败
            log.error("邮件发送失败");
            log.error(e.getMessage());
            throw new ExceptionMissing(EmailExceptionEnum.EMAIL_SEND_FAILURE.getMessage());
        }
    }


    /**
     * 使用 Thymeleaf 渲染模板内容
     *
     * @param template 模板
     * @param model    包裹参数的Map集合
     * @return 渲染后的邮件内容
     */
    public String content(String template, Map<String, String> model) {
        // 创建 Thymeleaf 上下文
        Context context = new Context();

        // 遍历 Map 集合，动态设置模板变量
        if (model != null) {
            for (Map.Entry<String, String> entry : model.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }

        // 加载模板并渲染
        return templateEngine.process(template, context);  // 只传入模板名
    }

    /**
     * 发送邮件
     *
     * @param content 需要发送的邮件内容
     * @param email   收件人邮箱
     * @param subject 邮件主题
     * @return 返回发送成功的结果
     */
    public Result<Object> send(String content, String email, String subject) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(content, true);  // 设置邮件内容为 HTML 格式
            helper.setTo(email);
            helper.setFrom("cloudPile@163.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }

        return Result.success();
    }
}

