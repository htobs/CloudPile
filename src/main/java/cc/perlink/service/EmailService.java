package cc.perlink.service;

import cc.perlink.pojo.vo.Result;

import java.io.IOException;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/18
 */
public interface EmailService {
    // 发送普通邮件
    Result<Object> sendEmail(String template,String email,String subject) throws IOException;

    // 发送密码重置验证码邮件
    Result<Object> sendRePasswordCode(String template,String email,String subject) throws IOException;



}
