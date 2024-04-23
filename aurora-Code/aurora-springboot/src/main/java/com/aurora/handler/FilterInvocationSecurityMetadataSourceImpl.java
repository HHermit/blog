package com.aurora.handler;

import com.aurora.mapper.RoleMapper;
import com.aurora.model.dto.ResourceRoleDTO;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;


/**
 * 实现FilterInvocationSecurityMetadataSource接口的安全元数据源类。
 * 用于提供访问控制列表（ACL）信息，决定用户是否有权访问特定资源。
 */
@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {


    @Autowired
    private RoleMapper roleMapper;


    //存储对应资源访问权限允许的角色列表
    private static List<ResourceRoleDTO> resourceRoleList;

    /**
     * 从角色映射器加载资源角色列表。
     * 该方法在对象初始化后执行一次，用于填充资源角色列表。
     */
    @PostConstruct
    private void loadResourceRoleList() {
        resourceRoleList = roleMapper.listResourceRoles();
    }

    /**
     * 清除数据源。
     * 用于在需要的时候清空资源角色列表。
     */
    public void clearDataSource() {
        resourceRoleList = null;
    }

    /**
     * 根据提供的受保护对象的信息，其实就是 URI，获取该 URI 配置的所有角色
     * 肯定是获取请求中的 URI 来和 所有的 资源配置中的 Ant Pattern 进行匹配以获取对应的资源配置
     * object
     *
     * 也就是这一步 通过查询对应的url访问路径，看是否有对应的角色，再通过与当前角色比对，有则放行url
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 如果资源角色列表为空，则加载它
        if (CollectionUtils.isEmpty(resourceRoleList)) {
            this.loadResourceRoleList();
        }
        FilterInvocation fi = (FilterInvocation) object;
        String method = fi.getRequest().getMethod(); // 获取请求方法
        String url = fi.getRequest().getRequestURI(); // 获取请求URL

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        // 遍历资源角色列表，寻找匹配的URL和请求方法
        for (ResourceRoleDTO resourceRoleDTO : resourceRoleList) {
            if (antPathMatcher.match(resourceRoleDTO.getUrl(), url) && resourceRoleDTO.getRequestMethod().equals(method)) {
                List<String> roleList = resourceRoleDTO.getRoleList();
                if (CollectionUtils.isEmpty(roleList)) {
                    // 如果没有角色要求，返回disable
                    return SecurityConfig.createList("disable");
                }
                // 返回该请求对应可访问的角色列表，作为安全属性
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }
        // 如果没有找到匹配项，返回null
        return null;
    }

    /**
     * 就是获取全部角色
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * 对特定的安全对象是否提供 ConfigAttribute 支持
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}

