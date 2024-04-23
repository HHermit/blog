package com.aurora.aspect;

import com.alibaba.fastjson.JSON;
import com.aurora.entity.ExceptionLog;
import com.aurora.event.ExceptionLogEvent;
import com.aurora.util.ExceptionUtil;
import com.aurora.util.IpUtil;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class ExceptionLogAspect {

    @Autowired
    private ApplicationContext applicationContext;

    /**
    * @Description: 设置切点，匹配 com.aurora.controller 包及其子包下的所有类中的所有方法。
     * execution：这是一个切点函数，用于匹配方法的执行。
     * 第一个*：表示返回类型，* 表示任意返回类型。
     * com.aurora.controller..：表示要匹配的方法所在的包路径。.. 表示匹配该包及其子包
     * 第二个*：表示类名，* 表示匹配所有类。
     * *(..)：表示方法名及其参数。* 表示匹配所有方法名，(..) 表示匹配任意参数列表。
    */
    @Pointcut("execution(* com.aurora.controller..*.*(..))")
    public void exceptionLogPointcut() {
    }

    /**
    * @Description: AfterThrowing：表示这个切面方法会在切点匹配的目标方法抛出异常后执行。
    * @Param: [joinPoint, e]joinPoint参数封装切点的相关信息，e参数表示目标方法抛出的异常。
    * @return: void
    */
    @AfterThrowing(value = "exceptionLogPointcut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Exception e) {
        //获取request对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);

        //定义异常日志实体
        ExceptionLog exceptionLog = new ExceptionLog();

        //joinPoint.getSignature()：获取方法签名，签名信息包含了连接点的各种信息，主要用于识别连接点所在的类、方法等。
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);

        //往异常实体中增添数据
        exceptionLog.setOptUri(Objects.requireNonNull(request).getRequestURI());

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        methodName = className + "." + methodName;

        exceptionLog.setOptMethod(methodName);
        exceptionLog.setRequestMethod(Objects.requireNonNull(request).getMethod());
        if (joinPoint.getArgs().length > 0) {
            if (joinPoint.getArgs()[0] instanceof MultipartFile) {
                exceptionLog.setRequestParam("file");
            } else {
                exceptionLog.setRequestParam(JSON.toJSONString(joinPoint.getArgs()));
            }
        }

        if (Objects.nonNull(apiOperation)) {
            exceptionLog.setOptDesc(apiOperation.value());
        } else {
            exceptionLog.setOptDesc("");
        }

        exceptionLog.setExceptionInfo(ExceptionUtil.getTrace(e));
        String ipAddress = IpUtil.getIpAddress(request);
        exceptionLog.setIpAddress(ipAddress);
        exceptionLog.setIpSource(IpUtil.getIpSource(ipAddress));

        //将封装好的exceptionLog对象，作为事件参数，发布事件到上下文中，由监听器获取
        applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));
    }

}
