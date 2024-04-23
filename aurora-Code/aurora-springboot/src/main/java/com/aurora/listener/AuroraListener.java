package com.aurora.listener;

import com.aurora.entity.ExceptionLog;
import com.aurora.entity.OperationLog;
import com.aurora.event.ExceptionLogEvent;
import com.aurora.event.OperationLogEvent;
import com.aurora.mapper.ExceptionLogMapper;
import com.aurora.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *  @description: 设置监听器，监听对应的事件，之后进行处理。主要是对操作日志和异常日志的保存。
 *
*/
@Component
public class AuroraListener {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    /**
    * @Description: 采用@Async注解，表示监听到之后使用异步线程处理这个事件。
     *              使用@EventListener(OperationLogEvent.class)，表示这个处理方法负责监听OperationLogEvent事件
     *              监听到之后，将封装好的一个OperationLog对象保存到数据库。
    * @Param: [operationLogEvent]
    * @return: void
    */
    @Async
    @EventListener(OperationLogEvent.class)
    public void saveOperationLog(OperationLogEvent operationLogEvent) {
        operationLogMapper.insert((OperationLog) operationLogEvent.getSource());
    }

    @Async
    @EventListener(ExceptionLogEvent.class)
    public void saveExceptionLog(ExceptionLogEvent exceptionLogEvent) {
        exceptionLogMapper.insert((ExceptionLog) exceptionLogEvent.getSource());
    }

}
