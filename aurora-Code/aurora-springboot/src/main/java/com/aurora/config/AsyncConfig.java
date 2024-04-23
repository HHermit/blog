package com.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author 33477
 * 开启异步控制管理，并对异步线程池进行相关配置
 * 跟yml配置中的hikari不一样，hikari是用来控制与数据库的连接的
 */
@EnableAsync // 启用异步注解
@Configuration // 表示这是一个配置类
public class AsyncConfig {

    /**
     * 创建并配置一个 TaskExecutor bean，用于异步任务的执行。
     *
     * @return 返回配置好的 ThreadPoolTaskExecutor 实例。
     */
    @Bean
    public TaskExecutor taskExecutor() {
        //新建一个用于管理异步控制的线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 配置线程池的基本属性
        executor.setCorePoolSize(10); // 核心线程数
        executor.setMaxPoolSize(20); // 最大线程数
        executor.setQueueCapacity(20); // 队列容量
        executor.setKeepAliveSeconds(60); // 线程的空闲时间

        // 配置线程名称前缀
        executor.setThreadNamePrefix("async-task-thread-");

        return executor;
    }
}
