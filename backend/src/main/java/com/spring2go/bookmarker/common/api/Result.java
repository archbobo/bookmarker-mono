package com.spring2go.bookmarker.common.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应结果包装类
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Slf4j
public final class Result<T> {
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 错误码
     */
    private ResultCode code = ResultCode.SUCCESS;

    /**
     * 返回结果实体
     */
    private T data = null;

    public boolean isSuccess() {
        return code == ResultCode.SUCCESS;
    }

    private Result(ResultCode code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> fail(String msg) {
        log.debug("返回错误", "code", ResultCode.FAILURE, "msg", msg);
        return new Result<T>(ResultCode.FAILURE, msg, null);
    }

    public static <T> Result<T> fail(ResultCode code) {
        log.debug("返回错误", "code", code);
        return new Result<T>(code, code.getDesc(), null);
    }

    public static <T> Result<T> fail(ResultCode code, String msg) {
        log.debug("返回错误", "code", code, "msg", msg);
        return new Result<T>(code, msg, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultCode.SUCCESS, "", data);
    }

    public static <T> Result<T> success() {
        return new Result<T>(ResultCode.SUCCESS, "", null);
    }
}
