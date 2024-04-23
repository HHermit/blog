package com.aurora.handler;

import com.alibaba.fastjson.JSON;
import com.aurora.constant.CommonConstant;
import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.model.dto.UserInfoDTO;
import com.aurora.entity.UserAuth;
import com.aurora.mapper.UserAuthMapper;
import com.aurora.service.TokenService;
import com.aurora.util.BeanCopyUtil;
import com.aurora.util.UserUtil;
import com.aurora.model.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    /**
     *  自动注入UserAuthMapper，用于操作用户认证信息
     */
    @Autowired
    private UserAuthMapper userAuthMapper;

    /**
     * 自动注入TokenService，用于生成用户认证token
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 用户登录成功后的处理逻辑。
     *
     * @param request HttpServletRequest对象，代表客户端的请求
     * @param response HttpServletResponse对象，用于向客户端发送响应
     * @param authentication 认证信息，包含登录成功的用户详情
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 复制当前登录用户的信息到UserInfoDTO
        UserInfoDTO userLoginDTO = BeanCopyUtil.copyObject(UserUtil.getUserDetailsDTO(), UserInfoDTO.class);
        // 检查认证信息是否非空，为登录成功的用户生成并设置token
        if (Objects.nonNull(authentication)) {
            //调用getPrincipal()方法可以获取到认证的主体信息，即用户详情。
            UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();
            String token = tokenService.createToken(userDetailsDTO);
            userLoginDTO.setToken(token);
        }
        // 设置响应内容类型为JSON，将登录成功后的用户信息（包含token）返回给客户端
        response.setContentType(CommonConstant.APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(ResultVO.ok(userLoginDTO)));
        // 异步更新用户信息，包括最后登录时间、IP地址等
        updateUserInfo();
    }

    /**
     * 异步更新用户信息。
     * 该方法会更新用户的最后登录时间、IP地址等信息。
     */
    @Async
    public void updateUserInfo() {
        // 构建用户认证信息对象，并根据当前登录用户的信息进行设置
        UserAuth userAuth = UserAuth.builder()
                .id(UserUtil.getUserDetailsDTO().getId())
                .ipAddress(UserUtil.getUserDetailsDTO().getIpAddress())
                .ipSource(UserUtil.getUserDetailsDTO().getIpSource())
                .lastLoginTime(UserUtil.getUserDetailsDTO().getLastLoginTime())
                .build();
        // 通过UserAuthMapper异步更新用户认证信息
        userAuthMapper.updateById(userAuth);
    }
}

