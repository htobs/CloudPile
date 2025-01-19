package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * 通用异常枚举
 */
@Getter
public enum CommonExceptionEnum {

    SOMETHING_WENT_WRONG(1001, "发生错误"),
    MISSING_PARAMETERS(1002, "缺少参数"),
    IP_BANNED(1003, "IP已被封禁"),
    NO_PERMISSION(1004,"没有权限");


    private final int code;
    private final String message;

    CommonExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}