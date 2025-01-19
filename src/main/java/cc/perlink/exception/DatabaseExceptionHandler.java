package cc.perlink.exception;

import cc.perlink.enums.exception.CommonExceptionEnum;
import cc.perlink.enums.exception.UserExceptionEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: htobs
 * @Date: 2024/11/13
 */
public class DatabaseExceptionHandler {
    /**
     * 用户注册时的错误
     *
     * @param e
     */
    public static void UserInsertExceptionHanlder(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("Duplicate entry")) {
                // 检查是否是邮箱重复的异常
                if (errorMessage.contains("for key 'users.email_UNIQUE'")) {
                    throw new ExceptionMissing(UserExceptionEnum.EMAIL_ALREADY_REGISTERED.getMessage());
                }
                // 可以在这里添加其他Duplicate entry的处理逻辑
            } else if (errorMessage.contains("cannot be null")) {
                // 提取字段名
                String columnName = extractColumnName(errorMessage);
                throw new ExceptionMissing(columnName + CommonExceptionEnum.MISSING_PARAMETERS.getMessage());
            }
            // 可以在这里添加其他异常类型的处理逻辑
        }
        // 如果不是特定的异常，可以返回通用的错误信息
        throw new ExceptionMissing(CommonExceptionEnum.SOMETHING_WENT_WRONG.getMessage());
    }


    /**
     * 提取字段名的辅助方法
     *
     * @param errorMessage 报错信息
     * @return
     */
    private static String extractColumnName(String errorMessage) {
        // 异常信息示例："Column 'email' cannot be null"
        // 使用正则表达式提取字段名
        Pattern pattern = Pattern.compile("Column '(.*?)' cannot be null");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "未知字段";
    }
}
