package cn.boommanpro.gaiaworkflow.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 */
@Data
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     *
     */
    private Integer code;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 错误提示，用户可阅读
     *
     */
    private String msg;

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 200;
        result.data = data;
        result.msg = "";
        return result;
    }

    public static<T> CommonResult<T> error(Exception e) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 500;
        result.data = null;
        result.msg = e.getMessage();
        return result;
    }
}
