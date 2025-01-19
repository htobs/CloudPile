package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/18
 */
@Getter
public enum CodeExceptionEnum {
    CAPTCHA_ERROR(6001, "验证码错误");
    private final int code;
    private final String message;

    CodeExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
