package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
@Getter
public enum UserExceptionEnum {
    REGISTER_FAIL(2001, "注册失败"),
    LOGIN_FAIL(2002, "登录失败"),
    CHANGE_PASSWORD_FAIL(2003, "修改密码失败"),
    UPDATE_FAIL(2004, "更新失败"),
    GET_USER_UUID_FAIL(2005, "获取用户uuid失败"),
    EMAIL_ALREADY_REGISTERED(2006, "邮箱已被注册"),
    PASSWORD_LENGTH_INVALID(2007, "密码长度不合格"),
    EMAIL_INVALID(2008, "邮箱不合格"),
    USER_DISABLED(2009, "用户已被禁用"),
    PASSWORDS_DO_NOT_MATCH(2010, "两次密码不一致"),
    EMAIL_OR_PASSWORD_INCORRECT(2011, "邮箱或密码错误"),
    PASSWORD_INCORRECT(2012, "密码错误"),
    USER_NOT_EXIST(2013, "用户不存在"),
    USER_INFO_UPDATE_FAIL(2014, "用户信息更新失败"),
    RESET_USER_KEYS_FALL(2015,"更新密钥失败"),
    SECRET_KEY_INCORRECT(2016, "密钥不正确"),
    USER_NOT_LOGGED_IN(2017,"用户未登录");

    private final int code;
    private final String message;

    UserExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
