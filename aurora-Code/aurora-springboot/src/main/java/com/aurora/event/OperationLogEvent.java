package com.aurora.event;

import com.aurora.entity.OperationLog;
import org.springframework.context.ApplicationEvent;

/**
 *  @description:
 *  定义操作日志事件，将一个OperationLog对象传入事件，这个就是发生事件的对象，方便后序监听时获取这个对象，进而对这个对象进行处理。
 *  
*/
public class OperationLogEvent extends ApplicationEvent {

    public OperationLogEvent(OperationLog operationLog) {
        super(operationLog);
    }
}
