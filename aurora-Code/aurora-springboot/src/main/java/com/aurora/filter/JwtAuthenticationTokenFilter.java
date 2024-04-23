package com.aurora.filter;


import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.service.TokenService;
import com.aurora.util.UserUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 继承自OncePerRequestFilter的过滤器会确保逻辑在整个请求/响应周期中只执行一次，
 * 请求转发不会第二次触发过滤器 ，而Filter会触发二次
 * 从而避免重复处理导致的问题。
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    /**
     *
     * JWT相关的功能
     */
    @Autowired
    public TokenService tokenService;

    /**
     * 认证失败处理器
     */
    @Autowired
    public AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 重写doFilterInternal方法，实现过滤器的具体逻辑。
     * 此方法主要负责在请求通过过滤器链之前，检查是否存在有效的用户令牌，并据此进行认证和令牌刷新操作。
     * 判断是否在请求头中存在JWT的Token,如果存在（ 用户已经登入过了 ）就从token的载荷中获取用户名,tokenService.getUserDetailDTO(request);
     * 然后进行认证,如果不存在( 该请求为登入请求 ),用户的信息从登入界面获取( 用户名,密码 )然后进行认证。
     *
     * @param request  HttpServletRequest对象，代表客户端的HTTP请求
     * @param response HttpServletResponse对象，用于向客户端发送HTTP响应
     * @param filterChain 过滤器链，包含当前过滤器之后的所有过滤器
     */
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // 从请求中获取用户详情DTO，但是新请求通过时 当前Security上下文中没有认证信息，则进行认证和令牌刷新
        UserDetailsDTO userDetailsDTO = tokenService.getUserDetailDTO(request);
        if (Objects.nonNull(userDetailsDTO) && Objects.isNull(UserUtil.getAuthentication())) {
            // 刷新令牌
            tokenService.renewToken(userDetailsDTO);
            // 创建新的认证令牌并设置到SecurityContext中，新请求再次认证
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsDTO, null, userDetailsDTO.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 继续处理请求
        filterChain.doFilter(request, response);
    }
}
