package com.aurora.event;

import com.aurora.entity.ExceptionLog;
import org.springframework.context.ApplicationEvent;

/**
 *  @description:
 *  继承 ApplicationEvent 类，可以创建自定义的事件，并在应用程序中进行传播和处理。
 *  本类就是定义了异常日志事件。
 *  传入ExceptionLog对象，方便后序对其进行操作。
 *
*/
public class ExceptionLogEvent extends ApplicationEvent {
    public ExceptionLogEvent(ExceptionLog exceptionLog) {
        super(exceptionLog);
    }
}
