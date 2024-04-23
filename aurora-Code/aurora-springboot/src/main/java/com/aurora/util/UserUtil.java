package com.aurora.util;

import com.aurora.model.dto.UserDetailsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *  @description: 用户获取Security当前认证用户信息
 *  
*/
@Component
public class UserUtil {

    /**
     * 获取当前用户详细信息的DTO（数据传输对象）。
     * 该方法通过SecurityContextHolder获取当前安全上下文的认证信息，然后从中提取用户详细信息的DTO。
     * getPrincipal: 获取用户身份信息，在未认证的情况下获取到的是用户名，在已认证的情况下获取到的是 UserDetails。
     * @return UserDetailsDTO 当前用户的详细信息DTO。
     */
    public static UserDetailsDTO getUserDetailsDTO() {
        return (UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取当前的认证信息。
     * 该方法用于获取当前安全上下文中的认证信息，这包括了用户的身份验证细节，比如用户名、权限等。
     *
     * @return Authentication 当前的认证信息。
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
