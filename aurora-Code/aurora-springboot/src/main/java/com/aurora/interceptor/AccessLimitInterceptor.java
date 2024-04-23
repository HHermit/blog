package com.aurora.interceptor;

import com.alibaba.fastjson.JSON;
import com.aurora.annotation.AccessLimit;
import com.aurora.model.vo.ResultVO;
import com.aurora.service.RedisService;
import com.aurora.util.IpUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.aurora.constant.CommonConstant.APPLICATION_JSON;

/**
 *  @description: 访问限制拦截器
 *  用于对每个接口的访问频率进行限制
*/
@Log4j2
@Component
@SuppressWarnings("all")
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;



    /**
    * @Description: preHandle在处理请求之前调用
    * @Param: [httpServletRequest, httpServletResponse, handler]
    * @return: boolean
    */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        //HandlerMethod 是 Spring MVC 中的一个类，用于封装处理请求的方法。也就是controller层的方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //通过反射获取处理器方法上的 AccessLimit 注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                long seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                String key = IpUtil.getIpAddress(httpServletRequest) + "-" + handlerMethod.getMethod().getName();
                try {
                    //对key进行自增（如果第一次访问就设置过期时间） q存储的是访问次数
                    long q = redisService.incrExpire(key, seconds);
                    if (q > maxCount) {
                        render(httpServletResponse, ResultVO.fail("请求过于频繁，" + seconds + "秒后再试"));
                        log.warn(key + "请求次数超过每" + seconds + "秒" + maxCount + "次");
                        return false;
                    }
                    return true;
                } catch (RedisConnectionFailureException e) {
                    log.warn("redis错误: " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    /**
    * @Description: 统一处理需要返回的result对象
    * @Param: [response, resultVO]
    * @return: void
    */
    private void render(HttpServletResponse response, ResultVO<?> resultVO) throws Exception {
        response.setContentType(APPLICATION_JSON);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(resultVO);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

}
