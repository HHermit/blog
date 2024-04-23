package com.aurora.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;

/**
 * @author 3347
 * 配置swagger文档
 * 通过 localhost:8080/doc.html 可以访问swagger2的页面
 */
@Configuration
@EnableSwagger2WebMvc //开启swagger2的网页支持
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 设置支持的协议
                .protocols(Collections.singleton("https"))
                // 设置文档的主机名
                .host("localhost本机部署")
                // 设置API的基本信息
                .apiInfo(apiInfo())
                // 返回一个ApiSelectorBuilder实例，用来控制那些接口暴露给Swagger来展现
                .select()
                // 指定扫描的包路径来定义要展示的API
                .apis(RequestHandlerSelectors.basePackage("com.aurora.controller"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(PathSelectors.any())
                // 创建Docket实例
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("aurora文档")
                .description("aurora")
                .contact(new Contact("啊哈14", "", "3347778009@qq.com"))
                // 设置服务条款网址
                .termsOfServiceUrl("localhost本机部署")
                .version("1.0")
                .build();
    }

}
