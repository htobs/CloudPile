package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * 邮件相关异常枚举
 */
@Getter
public enum EmailExceptionEnum {

    EMAIL_SEND_FAILURE(7001, "邮件发送失败"),
    TEMPLATE_ERROR(7002, "模板错误"),
    EMAIL_ADDRESS_ERROR(7003, "邮箱错误"),
    EMAIL_CONFIG_ERROR(7004, "邮箱配置错误");

    private final int code;
    private final String message;

    EmailExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}