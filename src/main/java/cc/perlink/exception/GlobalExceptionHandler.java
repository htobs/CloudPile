package cc.perlink.exception;

/**
 * @Description: 全局异常处理
 * @Author: htobs
 * @Date: 2024/10/5
 */

import cc.perlink.Application;
import cc.perlink.pojo.vo.Result;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    final Logger log = LoggerFactory.getLogger(Application.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        // 获取所有的错误信息，并构建自定义的错误消息
        String errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .distinct() // 去重
                .collect(Collectors.joining("; ")); // 使用分号连接错误信息

        // 如果没有错误信息，则使用默认错误信息
        if (errorMessage.isEmpty()) {
            errorMessage = "参数校验失败";
        }
        return Result.error(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> exception(Exception e) {
        e.printStackTrace();
        return Result.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");

    }


}
