package com.aurora.handler;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccessDecisionManagerImpl implements AccessDecisionManager {
    /**
     * 决定当前用户是否有权限访问指定的资源。
     *
     * @param authentication 当前用户的认证信息，包含用户的权限集合：这个自定实现的UserDetails中的权限指向对应的角色信息：{admin}
     * @param o 表示正在被访问的资源对象。就是FilterInvocation对象，可以得到request等web资源。
     * @param collection 表示访问该资源所需的安全配置属性集合。也就是FilterInvocationSecurityMetadataSourceImpl中getAttributes
     *                   返回该请求对应可访问的角色列表。两者对比体现是否有权限，
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        // 将当前用户的权限转换为字符串列表，可以看UserDetills中的getAuthorities相比对
        List<String> permissionList = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // 检查用户是否拥有访问资源所需的任一权限
        for (ConfigAttribute item : collection) {
            if (permissionList.contains(item.getAttribute())) {
                return; // 用户拥有权限，结束方法
            }
        }
        // 用户没有足够的权限访问资源
        throw new AccessDeniedException("权限不足");
    }


    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
