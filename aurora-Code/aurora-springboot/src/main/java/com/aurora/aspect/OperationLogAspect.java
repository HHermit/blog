package com.aurora.aspect;

import com.alibaba.fastjson.JSON;
import com.aurora.annotation.OptLog;
import com.aurora.entity.OperationLog;
import com.aurora.event.OperationLogEvent;
import com.aurora.util.IpUtil;
import com.aurora.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

/**
 *  @description:
 *  在类上上增加@Aspect 注解 声明这是一个切面处理类,
 *
 *  
*/
@Aspect
@Component
public class OperationLogAspect {

    /**
     *  事件发布是由ApplicationContext对象管控的，我们发布事件前需要注入ApplicationContext对象调用publishEvent方法完成事件发布
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
    * @Description: 使用@Pointcut 注解定义切点，标记方法。
     * 该方法的作用就是，声明一个名为operationLogPointCut切点，而这个切点拦截所有带有 @OptLog 注解的方法。
     * 在 Advice 中通过这个方法名就可以复用这个 Pointcut。
    * @Param: []
    * @return: void
    */
    @Pointcut("@annotation(com.aurora.annotation.OptLog)")
    public void operationLogPointCut() {
    }

    /**
    * @Description:  saveOperationLog 方法就是切面
     *               AfterReturning表示在目标方法正常返回后才执行的通知。表示操作成功才记录日志。
     *               value属性：指定该方法在哪些切点上执行，
     *               returning属性：keys 是切面方法的参数，表示目标方法返回值将被绑定到 keys 参数上。
     *                             目标方法就是指的就是 operationLogPointCut() 切点所匹配到的那些带有 @OptLog 注解的方法。
    * @Param: [joinPoint, keys]
     *        joinPoint：包含了切点的信息，如被代理对象、代理方法、方法参数等。
    * @return: void
    */
    @AfterReturning(value = "operationLogPointCut()", returning = "keys")
    @SuppressWarnings("unchecked")
    public void saveOperationLog(JoinPoint joinPoint, Object keys) {
        //获取request对象，方便查看请求中的信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);

        //定义操作日志实体，作为发布事件的内容，后序通过监听器保存到数据库中
        OperationLog operationLog = new OperationLog();

        //从切点中获取方法签名，进而获取方法对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //获取方法所属的类的Api注解，也就是Controller层的API注解。eg：@Api(tags = "文章模块")
        Api api = (Api) signature.getDeclaringType().getAnnotation(Api.class);

        //获取方法上的ApiOperation注解 eg：@ApiOperation(value = "获取所有文章")
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);

        //获取方法上的OptLog注解，表示这个方法发生了什么操作
        OptLog optLog = method.getAnnotation(OptLog.class);

        operationLog.setOptModule(api.tags()[0]);
        operationLog.setOptType(optLog.optType());
        operationLog.setOptDesc(apiOperation.value());

        //joinPoint.getTarget()：获取目标对象，即被拦截方法所在的对象实例
        String className = joinPoint.getTarget().getClass().getName();

        String methodName = method.getName();
        methodName = className + "." + methodName;

        //设置请求的类型和操作方法的全限定名
        operationLog.setRequestMethod(Objects.requireNonNull(request).getMethod());
        operationLog.setOptMethod(methodName);

        //获取切点所在方法上的参数
        if (joinPoint.getArgs().length > 0) {
            if (joinPoint.getArgs()[0] instanceof MultipartFile) {
                operationLog.setRequestParam("file");
            } else {
                operationLog.setRequestParam(JSON.toJSONString(joinPoint.getArgs()));
            }
        }
        operationLog.setResponseData(JSON.toJSONString(keys));
        operationLog.setUserId(UserUtil.getUserDetailsDTO().getId());
        operationLog.setNickname(UserUtil.getUserDetailsDTO().getNickname());
        String ipAddress = IpUtil.getIpAddress(request);
        operationLog.setIpAddress(ipAddress);
        operationLog.setIpSource(IpUtil.getIpSource(ipAddress));
        operationLog.setOptUri(request.getRequestURI());

        //将封装好的OperationLog对象，作为事件参数，发布事件到上下文中，由监听器获取
        applicationContext.publishEvent(new OperationLogEvent(operationLog));
    }

}
