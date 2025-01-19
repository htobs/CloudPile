package cc.perlink.service;

import cc.perlink.pojo.vo.Result;

import java.io.IOException;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/18
 */
public interface CodeService {
    // 生成验证码
    Result<Object> createCode() throws IOException;

    // 校验验证码
    Result<Object> checkCode(String code, String codeUid);
}
