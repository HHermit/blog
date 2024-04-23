package com.aurora.handler;

import com.aurora.model.vo.ResultVO;
import com.aurora.enums.StatusCodeEnum;
import com.aurora.exception.BizException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;


/**
 * 全局异常处理类
 * 用于处理RESTful API异常。
 * 通过这样的全局异常处理器，可以统一处理控制器层抛出的异常，并返回自定义的错误消息给客户端，提高了系统的容错性和用户体验。
 * Log4j2 进行日志记录，
 * RestControllerAdvice  统一处理控制器层抛出的异常，并将异常信息以 JSON 格式返回给客户端。
 */
@Log4j2
@RestControllerAdvice
public class ControllerAdviceHandler {

    /**
     * 处理业务异常（BizException）。
     * 当控制器层抛出 BizException 异常时，Spring 将会调用这个方法来处理异常，并返回一个自定义的错误消息。
     */
    @ExceptionHandler(value = BizException.class)
    public ResultVO<?> errorHandler(BizException e) {
        return ResultVO.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常（MethodArgumentNotValidException）。
     * 当客户端提交的参数不满足验证条件时，即验证失败时，触发此异常处理。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<?> errorHandler(MethodArgumentNotValidException e) {
        return ResultVO.fail(StatusCodeEnum.VALID_ERROR.getCode(),
                Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * 处理未被捕获的通用异常。
     * 这是异常处理的最后屏障，用于处理所有其他未被特定异常处理器处理的异常。
     */
    @ExceptionHandler(value = Exception.class)
    public ResultVO<?> errorHandler(Exception e) {
        e.printStackTrace();
        return ResultVO.fail(StatusCodeEnum.SYSTEM_ERROR.getCode(), StatusCodeEnum.SYSTEM_ERROR.getDesc());
    }

}

