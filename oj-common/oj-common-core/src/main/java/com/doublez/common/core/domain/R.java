package com.doublez.common.core.domain;

import com.doublez.common.core.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class R<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> R<T> ok(T data) {
        return assembleResult(data, ResultCode.SUCCESS);
    }

    public static <T> R<T> ok() {
        return assembleResult(null, ResultCode.SUCCESS);
    }
    public static <T> R<T> fail() {
        return assembleResult(null, ResultCode.FAILED);
    }
    /**
     * 指定状态码的返回
     * @param resultCode
     * @return
     * @param <T>
     */
    public static <T> R<T> fail(ResultCode resultCode) {
        return assembleResult(null, resultCode);
    }
    private static <T> R<T> assembleResult(T data, ResultCode resultCode) {
        R<T> r = new R<T>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMsg());
        r.setData(data);
        return r;
    }
}
