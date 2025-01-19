package cc.perlink.service.impl;

import cc.perlink.enums.UserPermission;
import cc.perlink.enums.exception.CodeExceptionEnum;
import cc.perlink.enums.exception.UserExceptionEnum;
import cc.perlink.exception.DatabaseExceptionHandler;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.mapper.UserMapper;
import cc.perlink.pojo.dto.user.*;
import cc.perlink.pojo.po.mysql.User;
import cc.perlink.pojo.po.redis.CodeRedisPo;
import cc.perlink.pojo.po.redis.EmailCodeRedisPo;
import cc.perlink.pojo.po.redis.TokenRedisPo;
import cc.perlink.pojo.po.redis.UserKeyRedisPo;
import cc.perlink.pojo.vo.Result;
import cc.perlink.service.UserService;
import cc.perlink.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cc.perlink.enums.exception.UserExceptionEnum.*;


/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/12
 */
@Component
public class UserServiceImpl implements UserService {
    final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;

    /**
     * 注册用户
     *
     * @param registerRequest 用户注册时需要传入的参数
     * @return 注册成功
     */
    @Override
    public Result<Object> register(RegisterRequest registerRequest) {
        // 判断两次密码是否准确
        if (!registerRequest.getPassword().equals(registerRequest.getRePassword())) {
            throw new ExceptionMissing(PASSWORDS_DO_NOT_MATCH.getMessage());
        }
        // 加密密码
        registerRequest.setPassword(Md5Util.getMD5String(registerRequest.getPassword()));
        User user = new User();
        BeanUtils.copyProperties(registerRequest, user);
        // 生成 key
        Map<String, String> keyMap = UUIDManagerUtil.generateUserKeys(user.getEmail());
        user.setAccessKey(keyMap.get("accessKey"));
        user.setSecretKey(keyMap.get("secretKey"));
        // 增加其他属性
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPermission(UserPermission.USER.getPermission());
        // 插入数据库
        try {
            // 插入数据库
            userMapper.insert(user);
        } catch (Exception e) {
            DatabaseExceptionHandler.UserInsertExceptionHanlder(e);
        }
        // 将用户数据缓存到redis
        log.info("{}注册成功", user.getEmail());
        return Result.success();

    }

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录的参数
     * @return 用户token
     */
    @Override
    public Result<Object> login(LoginRequest loginRequest) {
        // 加密密码
        String email = loginRequest.getEmail();
        String password = Md5Util.getMD5String(loginRequest.getPassword());
        // 数据库查询用户信息，根据email和password
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", email).eq("password", password);
        User user = userMapper.selectOne(query);
        if (user == null) {
            throw new ExceptionMissing(UserExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.getMessage());
        }
        // 检查是否被封禁
        if (user.getPermission().equals(UserPermission.BANNED.getPermission())) {
            throw new ExceptionMissing(UserExceptionEnum.USER_DISABLED.getMessage());
        }
        // 如果有数据，生成调用JwtUtil.genToken(Map对象数据)方法，生成token，Map对象里面的数据有id、email、country、province、language、avatarUrl、createdAt、bucketCount、nickName这几个字段
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("country", user.getCountry());
        claims.put("province", user.getProvince());
        claims.put("language", user.getLanguage());
        claims.put("avatarUrl", user.getAvatarUrl());
        claims.put("createdAt", Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        claims.put("bucketCount", user.getBucketCount());
        claims.put("nickName", user.getNickName());
        claims.put("permission", user.getPermission());
        ThreadLocalUtil.set(claims);
        String token = JwtUtil.genToken(claims);
        // 将Token缓存到redis
        TokenRedisPo tokenRedisPo = new TokenRedisPo();
        tokenRedisPo.setToken(token);
        tokenRedisPo.setEmail(user.getEmail());
        stringRedisTemplate.opsForValue().set(tokenRedisPo.getRedisKey(), tokenRedisPo.getRedisValue());
        return Result.success(token);
    }

    /**
     * 用户忘记密码重置密码
     *
     * @param userDto 重置密码的参数
     * @return 重置成功
     */
    @Override
    public Result<Object> rePassword(RePasswordRequest userDto) throws IOException {
        // 检查验证码是否正确
        EmailCodeRedisPo emailCodeRedisPo = new EmailCodeRedisPo();
        emailCodeRedisPo.setEmail(userDto.getEmail());
        String queryEmailCodeString = stringRedisTemplate.opsForValue().get(emailCodeRedisPo.getRedisKey());
        if (queryEmailCodeString == null) { // 用户不存在
            throw new ExceptionMissing(CodeExceptionEnum.CAPTCHA_ERROR.getMessage());
        }
        EmailCodeRedisPo emailCodeRedisData = EmailCodeRedisPo.fromRedis(queryEmailCodeString);
        if (!emailCodeRedisData.getCode().equals(userDto.getCode())) { // 验证码错误
            throw new ExceptionMissing(CodeExceptionEnum.CAPTCHA_ERROR.getMessage());
        }
        // 验证码正确，更新数据库
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>().eq("email", userDto.getEmail());
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setPassword(Md5Util.getMD5String(userDto.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user, updateWrapper);
        // 删除验证码
        stringRedisTemplate.delete(emailCodeRedisPo.getRedisKey());
        return Result.success();
    }

    /**
     * 退出登录
     *
     * @return
     */
    @Override
    public Result<Object> logout() {
        TokenRedisPo tokenRedisPo = new TokenRedisPo();
        Map<String, Object> claims = ThreadLocalUtil.get();
        tokenRedisPo.setEmail(claims.get("email").toString());
        stringRedisTemplate.delete(tokenRedisPo.getRedisKey());
        return Result.success();
    }

    /**
     * 修改用户密码
     *
     * @param changePasswordRequest 修改用户密码的参数
     * @return 修改成功
     */
    @Override
    public Result<Object> changePassword(ChangePasswordRequest changePasswordRequest) {
        // 判断两次密码是否一致
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getRePassword())) {
            throw new ExceptionMissing(PASSWORDS_DO_NOT_MATCH.getMessage());
        }

        // 获得旧密码，查询数据库判断旧密码是否相同
        String oldPassword = Md5Util.getMD5String(changePasswordRequest.getOldPassword());
        QueryWrapper<User> query = new QueryWrapper<>();
        Map<String, Object> claims = ThreadLocalUtil.get();
        String email = (String) claims.get("email");
        query.eq("email", email).eq("password", oldPassword);
        User user = userMapper.selectOne(query);
        if (user == null) { // 用户不存在
            throw new ExceptionMissing(UserExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.getMessage());
        }
        // 修改密码
        BeanUtils.copyProperties(changePasswordRequest, user);
        user.setPassword(Md5Util.getMD5String(changePasswordRequest.getNewPassword()));
        userMapper.updateById(user);
        // 删除redis用户token和线程里的token
        TokenRedisPo userTokenRedisPo = new TokenRedisPo();
        userTokenRedisPo.setEmail(email);
        stringRedisTemplate.delete(userTokenRedisPo.getRedisKey());
        ThreadLocalUtil.remove();
        return Result.success();
    }

    /**
     * 修改用户信息
     *
     * @param changeUserInfoRequest 修改用户的参数
     * @return 修改成功
     */
    @Override
    public Result<Object> changeUserInfo(ChangeUserInfoRequest changeUserInfoRequest) {
        // 线程当中获取用户信息
        Map<String, Object> claims = ThreadLocalUtil.get();
        User user = new User();
        BeanUtils.copyProperties(changeUserInfoRequest, user);
        String email = claims.get("email").toString();
        user.setEmail(email);
        // 修改用户数据
        UpdateWrapper<User> update = new UpdateWrapper<>();
        update.eq("email", email);
        int updateCount = userMapper.update(user, update);
        if (updateCount < 0) {
            throw new ExceptionMissing(UserExceptionEnum.USER_INFO_UPDATE_FAIL.getMessage());
        }
        return Result.success();
    }

    /**
     * 重置SecretKey
     *
     * @return 重置成功
     */
    @Override
    public Result<Object> resetSecretKey() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String email = claims.get("email").toString();
        // 生成新的keys
        Map<String, String> keyMap = UUIDManagerUtil.generateUserKeys(email);
        String accessKey = keyMap.get("accessKey");
        String secretKey = keyMap.get("secretKey");
        // 修改数据库
        UpdateWrapper<User> update = new UpdateWrapper<>();
        update.eq("email", email);
        User user = new User();
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        int updateCount = userMapper.update(user, update);
        if (updateCount < 0) {  // 更新密钥失败
            throw new ExceptionMissing(UserExceptionEnum.RESET_USER_KEYS_FALL.getMessage());
        }
        // 删除Redis数据
        UserKeyRedisPo userKeyRedisPo = new UserKeyRedisPo();
        userKeyRedisPo.setEmail(email);
        userKeyRedisPo.setId(Integer.valueOf(claims.get("id").toString()));
        stringRedisTemplate.delete(userKeyRedisPo.getRedisKey());
        log.info("{}重置SecretKey", user.getEmail());
        return Result.success();
    }

    /**
     * 获取用户密钥
     *
     * @return 一个包含着accessKey和secretKey的Map对象
     */
    @Override
    public Result<Object> getUserKeys() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserKeyRedisPo userKeyRedisPo = new UserKeyRedisPo();
        userKeyRedisPo.setId(Integer.valueOf(claims.get("id").toString()));
        userKeyRedisPo.setEmail(claims.get("email").toString());
        // 查询Redis是否存在
        String queryRedisString = stringRedisTemplate.opsForValue().get(userKeyRedisPo.getRedisKey());
        if (queryRedisString == null) {
            // Redis不存在从数据库当中获取并缓存到Redis，
            QueryWrapper<User> query = new QueryWrapper<User>().eq("email", claims.get("email").toString());
            User queryData = userMapper.selectOne(query);
            userKeyRedisPo.setAccessKey(queryData.getAccessKey());
            userKeyRedisPo.setSecretKey(queryData.getSecretKey());
            // 缓存redis
            stringRedisTemplate.opsForValue().set(userKeyRedisPo.getRedisKey(), userKeyRedisPo.getRedisValue());
            // 返回并返回accessKey和secretKey
            Map<String, String> keysMap = new HashMap<>();
            keysMap.put("accessKey", queryData.getAccessKey());
            keysMap.put("secretKey", queryData.getSecretKey());
            return Result.success(keysMap);
        }
        // 如果Redis存在则就只返回accessKey
        UserKeyRedisPo queryRedisData = UserKeyRedisPo.fromRedis(queryRedisString);
        Map<String, String> keysMap = new HashMap<>();
        keysMap.put("accessKey", queryRedisData.getAccessKey());
        keysMap.put("secretKey", "********************************");
        return Result.success(keysMap);
    }


    /**
     * 检查AccessKey
     *
     * @param accessKey 访问密钥
     * @return 是否有效
     */
    @Override
    public Result<Object> checkAccessKey(String accessKey) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String email = claims.get("email").toString();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", email);
        User queryData = userMapper.selectOne(query);
        String access = queryData.getAccessKey();
        String secret = queryData.getSecretKey();
        if (!access.equals(accessKey)) {
            throw new ExceptionMissing(SECRET_KEY_INCORRECT.getMessage());
        }
        return Result.success("密钥有效", null);
    }

    /**
     * 检查AccessKey和SecretKey
     *
     * @param accessKey 访问密钥
     * @param secretKey 私密密钥
     * @return
     */
    @Override
    public Result<Object> checkKey(String accessKey, String secretKey) {
        QueryWrapper<User> query = new QueryWrapper<User>().eq("access_key", accessKey).eq("secret_key", secretKey);
        User queryData = userMapper.selectOne(query);
        if (queryData == null) {
            throw new ExceptionMissing(SECRET_KEY_INCORRECT.getMessage());
        }
        return Result.success("密钥有效", null);
    }

    /**
     * 使用accessKey和secretKey登录
     *
     * @param accessKey 访问密钥
     * @param secretKey 用户私钥
     * @return
     */
    @Override
    public Result<Object> keyLogin(String accessKey, String secretKey) {
        // 数据库查询用户信息，查询私钥和密钥
        System.out.println(accessKey + secretKey);
        QueryWrapper<User> query = new QueryWrapper<User>().eq("access_key", accessKey).eq("secret_key", secretKey);
        User user = userMapper.selectOne(query);
        System.out.println(user);
        if (user == null) {
            throw new ExceptionMissing(UserExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.getMessage());
        }
        // 检查是否被封禁
        if (user.getPermission().equals(UserPermission.BANNED.getPermission())) {
            throw new ExceptionMissing(UserExceptionEnum.USER_DISABLED.getMessage());
        }
        // 如果有数据，生成调用JwtUtil.genToken(Map对象数据)方法，生成token，Map对象里面的数据有id、email、country、province、language、avatarUrl、createdAt、bucketCount、nickName这几个字段
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("country", user.getCountry());
        claims.put("province", user.getProvince());
        claims.put("language", user.getLanguage());
        claims.put("avatarUrl", user.getAvatarUrl());
        claims.put("createdAt", Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        claims.put("bucketCount", user.getBucketCount());
        claims.put("nickName", user.getNickName());
        claims.put("permission", user.getPermission());
        ThreadLocalUtil.set(claims);
        String token = JwtUtil.genToken(claims);
        // 将Token缓存到redis
        TokenRedisPo tokenRedisPo = new TokenRedisPo();
        tokenRedisPo.setToken(token);
        tokenRedisPo.setEmail(user.getEmail());
        stringRedisTemplate.opsForValue().set(tokenRedisPo.getRedisKey(), tokenRedisPo.getRedisValue());
        return Result.success(token);
    }

}
