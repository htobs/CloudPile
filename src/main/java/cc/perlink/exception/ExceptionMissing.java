package cc.perlink.exception;

/**
 * @Description: 异常信息
 * @Author: htobs
 * @Date: 2024/11/3
 */
public class ExceptionMissing extends RuntimeException {

    public ExceptionMissing(String message) {
        super(message);
    }

    public ExceptionMissing(String message, Throwable cause) {
        super(message, cause);
    }
}

