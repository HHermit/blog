package com.aurora.config;

import com.aurora.filter.JwtAuthenticationTokenFilter;
import com.aurora.handler.AccessDecisionManagerImpl;
import com.aurora.handler.FilterInvocationSecurityMetadataSourceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 处理认证过程中的异常
     */
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 处理授权过程中的异常
     */
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler; // 认证成功处理器

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler; // 认证失败处理器

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter; // JWT认证过滤器

    /**
     * 配置资源访问的安全元数据
     * @return 返回FilterInvocationSecurityMetadataSource实例
     */
    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new FilterInvocationSecurityMetadataSourceImpl();
    }

    /**
     * 配置访问决策管理器
     * @return 返回AccessDecisionManager实例
     */
    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new AccessDecisionManagerImpl();
    }

    /**
     * 配置认证管理器
     * @return 返回AuthenticationManager实例
     * @throws Exception 可能抛出的异常
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 设置密码编码器
     * @return 返回PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置HTTP安全设置
     * 这位没有配置cors跨域策略，因为在WebMvcConfig 已经统一进行配置了
     * @param http 安全配置对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置表单登录
        http.formLogin()
                .loginProcessingUrl("/users/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // 配置授权规则
        http.authorizeRequests()
                //Spring Security引入了 `ObjectPostProcessor` 的概念，它可用于修改或替换 Java Configuration创建的许多 `Object` 实例。
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        //设置如何获得资源访问的安全元数据
                        fsi.setSecurityMetadataSource(securityMetadataSource());
                        //设置访问决策管理器如何进行鉴权
                        fsi.setAccessDecisionManager(accessDecisionManager());
                        return fsi;
                    }
                })
                //.anyRequest().permitAll() 的作用是作为授权规则链中的最后一环，
                // 放到最后确保所有请求都被处理，并且允许所有用户对未被之前规则匹配到的请求进行访问。
                .anyRequest().permitAll()
                .and()
                // 关闭CSRF保护并配置异常处理
                .csrf().disable().exceptionHandling()
                // 配置认证失败处理器
                .authenticationEntryPoint(authenticationEntryPoint)
                // 配置访问权限异常处理器
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                // 配置会话管理策略为无状态
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 在UsernamePasswordAuthenticationFilter之前添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

}

