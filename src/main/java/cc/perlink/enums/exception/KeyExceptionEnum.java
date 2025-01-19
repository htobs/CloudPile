package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/16
 */
@Getter
public enum KeyExceptionEnum {
    CAPACITY_CANNOT_BE_ZERO_OR_LESS(4001, "设置容量不得低于或等于0"),
    KEY_ALREADY_IN_USE(4002, "Key已经被使用"),
    INVALID_KEY(4003, "无效的key");

    private final int code;
    private final String message;

    KeyExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
