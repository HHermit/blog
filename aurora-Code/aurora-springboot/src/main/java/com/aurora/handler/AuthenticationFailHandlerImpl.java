package com.aurora.handler;

import com.alibaba.fastjson.JSON;
import com.aurora.constant.CommonConstant;
import com.aurora.model.vo.ResultVO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理
 */
@Component
public class AuthenticationFailHandlerImpl implements AuthenticationFailureHandler {
    /**
     * 处理认证失败的逻辑。
     * 当用户登录失败时（例如，用户名或密码错误），此方法将被调用。
     *
     * @param httpServletRequest 当前的HTTP请求对象。
     * @param httpServletResponse 当前的HTTP响应对象。
     * @param e 触发认证失败的异常对象。
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        // 设置响应内容类型为JSON
        httpServletResponse.setContentType(CommonConstant.APPLICATION_JSON);
        // 将认证失败的信息封装为JSON格式并写入响应体
        httpServletResponse.getWriter().write(JSON.toJSONString(ResultVO.fail(e.getMessage())));
    }

}

