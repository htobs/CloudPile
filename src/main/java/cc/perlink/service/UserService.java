package cc.perlink.service;

import cc.perlink.pojo.dto.user.*;
import cc.perlink.pojo.vo.Result;

import java.io.IOException;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/12
 */
public interface UserService {
    // 注册
    Result<Object> register(RegisterRequest userDto);

    // 登录
    Result<Object> login(LoginRequest userDto);

    // 重置用户密码（忘记密码）
    Result<Object> rePassword(RePasswordRequest userDto) throws IOException;

    // 退出登录
    Result<Object> logout();

    // 修改密码
    Result<Object> changePassword(ChangePasswordRequest userDto);

    // 修改用户信息
    Result<Object> changeUserInfo(ChangeUserInfoRequest userDto);

    // 重置用户SecretKey
    Result<Object> resetSecretKey();

    // 获取用户AccessKey和SecretKey
    Result<Object> getUserKeys();

    // 判断用户的AccessKey是否合法
    Result<Object> checkAccessKey(String accessKey);

    // 检查用户AccessKey和SecretKey是否正确
    Result<Object> checkKey(String accessKey, String secretKey);

    // 使用AccessKey和SecretKey进行用户登录
    Result<Object> keyLogin(String accessKey, String secretKey);
}
