package cc.perlink.controller;

import cc.perlink.pojo.dto.user.*;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.EmailService;
import cc.perlink.service.UserService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 用户接口控制类
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private EmailService emailService;

    // 注册
    @PostMapping("register")
    public Result<Object> register(@RequestBody @Validated RegisterRequest register) {
        return userService.register(register);
    }

    // 登录
    @PostMapping("login")
    public Result<Object> login(@RequestBody @Validated LoginRequest login) {
        return userService.login(login);
    }

    // 退出登录
    @GetMapping("logout")
    public Result<Object> logout() {
        return userService.logout();
    }

    // 重置密码
    @SneakyThrows
    @PostMapping("rePassword")
    public Result<Object> rePassword(@RequestBody @Validated RePasswordRequest rePasswordRequest) {
        userService.rePassword(rePasswordRequest);
        // 重置成功发送验证码
        return emailService.sendEmail("rePasswordSuccess", rePasswordRequest.getEmail(), "密码已重置");

    }

    // 修改密码
    @PutMapping("changePassword")
    public Result<Object> changePassword(@RequestBody @Validated ChangePasswordRequest changePassword) {
        return userService.changePassword(changePassword);
    }

    // 修改用户信息
    @PutMapping("changeUserInfo")
    public Result<Object> changeUserInfo(@RequestBody @Validated ChangeUserInfoRequest changeUserInfo) {
        return userService.changeUserInfo(changeUserInfo);
    }

    // 重置用户SecretKey
    @GetMapping("resetSecretKey")
    public Result<Object> resetSecretKey() {
        return userService.resetSecretKey();
    }

    // 获取用户AccessKey和SecretKey
    @GetMapping("getUserKeys")
    public Result<Object> getUserKeys() {
        return userService.getUserKeys();
    }

    // 判断用户的AccessKey是否合法
    @PostMapping("checkAccessKey")
    public Result<Object> checkAccessKey(@RequestParam String accessKey) {
        return userService.checkAccessKey(accessKey);
    }

    // 检查用户AccessKey和SecretKey是否正确
    @PostMapping("checkKey")
    public Result<Object> checkKey(@RequestParam String accessKey, @RequestParam String secretKey) {
        return userService.checkKey(accessKey, secretKey);
    }

    // 使用密钥登陆
    @PostMapping("keyLogin")
    public Result<Object> keyLogin(@RequestParam String accessKey, @RequestParam String secretKey) {
        return userService.keyLogin(accessKey, secretKey);
    }

}
