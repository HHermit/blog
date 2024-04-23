package com.aurora.service;

import com.aurora.model.dto.UserDetailsDTO;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {

    /**
     * 为登录用户创建token。
     */
    String createToken(UserDetailsDTO userDetailsDTO);

    /**
     * 创建指定用户的令牌。
     */
    String createToken(String subject);

    /**
     * 刷新用户令牌。
     */
    void refreshToken(UserDetailsDTO userDetailsDTO);

    /**
     * 更新用户令牌。
     */
    void renewToken(UserDetailsDTO userDetailsDTO);

    /**
     * 解析令牌并返回其声明。
     */
    Claims parseToken(String token);

    /**
     * 从HTTP请求中获取用户详情数据传输对象。
     */
    UserDetailsDTO getUserDetailDTO(HttpServletRequest request);

    /**
     * 删除登录用户。
     */
    void delLoginUser(Integer userId);

}
