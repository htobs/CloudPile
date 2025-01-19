package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * 通用异常枚举
 */
@Getter
public enum BucketExceptionEnum {

    BUCKET_CREATION_LIMIT_EXCEEDED(3001, "已超过最大能建桶数"),
    BUCKET_SPACE_INSUFFICIENT(3002, "存储桶空间不足"),
    BUCKET_NOT_EXIST(3003, "存储桶不存在"),
    BUCKET_ACCESS_DENIED(3004, "存储桶不允许访问");


    private final int code;
    private final String message;

    BucketExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}