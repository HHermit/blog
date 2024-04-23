package com.aurora.exception;


import com.aurora.enums.StatusCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务逻辑中出现的异常情况。
 */
@Getter
@AllArgsConstructor
public class BizException extends RuntimeException {

    private Integer code = StatusCodeEnum.FAIL.getCode();

    private final String message;

    public BizException(String message) {
        this.message = message;
    }

    public BizException(StatusCodeEnum statusCodeEnum) {
        this.code = statusCodeEnum.getCode();
        this.message = statusCodeEnum.getDesc();
    }

}
