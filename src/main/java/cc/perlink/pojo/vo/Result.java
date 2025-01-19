package cc.perlink.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 返回前端数据
 * @Author: htobs
 * @Date: 2024/10/5
 */
@NoArgsConstructor  // 无参构造方法
@AllArgsConstructor // 全参构造方法
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    /**
     * 返回操作成响应结果（带响应数据）
     *
     * @param data 响应数据
     * @param <E>  泛型
     * @return {}
     */
    public static <E> Result<E> success(E data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 自定义成功信息和数据
     * @param message
     * @param data
     * @return
     * @param <E>
     */
    public static <E> Result<E> success(String message, E data) {
        return new Result<>(200, message, data);
    }

    /**
     * 返回操作成功响应结果（不带响应数据）
     *
     * @return {}
     */
    public static <E> Result<E> success() {
        return new Result<E>(200, "操作成功", null);
    }

    /**
     * 返回操作失败响应结果
     *
     * @param message 错误信息
     * @return {}
     */
    public static <E>Result<E> error(String message) {
        return new Result<E>(500, message, null);
    }

}
