package com.aurora.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 33477
 * 开启事务管理，同时给mabitsplus添加分页插件
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusConfig {

    /**
     * 创建并配置MybatisPlusInterceptor Bean。
     * 这个方法初始化一个MybatisPlusInterceptor实例，并为其添加一个PaginationInnerInterceptor，
     * 用于支持MySQL数据库的分页插件。
     *
     * @return MybatisPlusInterceptor 返回配置好的MybatisPlusInterceptor实例。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加MySQL分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


}