package com.aurora.config;


import com.aurora.interceptor.PaginationInterceptor;
import com.aurora.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    //分页拦截器
    private PaginationInterceptor paginationInterceptor;

    @Autowired
    //访问限制拦截器
    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    /**
     * 配置跨域请求的处理
     */
    public void addCorsMappings(CorsRegistry registry) {
        //所有路径映射到跨域请求处理中
        registry.addMapping("/**")
                //允许跨域请求携带凭证
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paginationInterceptor);
        registry.addInterceptor(accessLimitInterceptor);
    }

}
