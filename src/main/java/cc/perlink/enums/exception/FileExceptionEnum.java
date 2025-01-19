package cc.perlink.enums.exception;

import lombok.Getter;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/17
 */
@Getter
public enum FileExceptionEnum {
    UPLOAD_FAILED(5001, "上传失败"),
    NO_PERMISSION(5002, "当前操作系统没有权限（增删）"),
    FILE_NOT_EXIST(5003, "文件不存在"),
    DOWNLOAD_FAILED(5004,"下载失败"),
    DELETE_FAILED(5005, "删除失败");

    private final int code;
    private final String message;

    FileExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
