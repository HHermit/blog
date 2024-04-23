package com.aurora.exception;

/**
 * 根据需要指定异常的消息、错误码和异常原因。
 * 这个异常类通常用于在业务逻辑中抛出异常，
 * 并且可以根据具体业务情况设置异常的错误码以及相关的描述信息
 */
public class TaskException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Code code;

    public TaskException(String msg, Code code) {
        this(msg, code, null);
    }

    public TaskException(String msg, Code code, Exception exception) {
        super(msg, exception);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public enum Code {
        TASK_EXISTS, NO_TASK_EXISTS, TASK_ALREADY_STARTED, UNKNOWN, CONFIG_ERROR, TASK_NODE_NOT_AVAILABLE
    }
}
