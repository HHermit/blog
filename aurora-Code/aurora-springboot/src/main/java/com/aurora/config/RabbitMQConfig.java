package com.aurora.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.aurora.constant.RabbitMQConstant.*;

/**
 * 配置一些队列和交换机并进行绑定
 * 注意：所有的交换机都是Fanout类型，对于发布的消息，以广播的形式转发给绑定的所有队列
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 创建一个名为MAXWELL_QUEUE的队列，该队列是持久化的。
     * @return 返回创建的队列实例。
     */
    @Bean
    public Queue articleQueue() {
        return new Queue(MAXWELL_QUEUE, true);
    }

    /**
     * 创建一个名为MAXWELL_EXCHANGE的Fanout类型交换器，该交换器是持久化的。
     * @return 返回创建的交换器实例。
     */
    @Bean
    public FanoutExchange maxWellExchange() {
        return new FanoutExchange(MAXWELL_EXCHANGE, true, false);
    }

    /**
     * 将articleQueue队列绑定到maxWellExchange交换器上。
     * @return 返回创建的绑定实例。
     */
    @Bean
    public Binding bindingArticleDirect() {
        return BindingBuilder.bind(articleQueue()).to(maxWellExchange());
    }

    /**
     * 创建一个名为EMAIL_QUEUE的队列，该队列是持久化的。
     * @return 返回创建的队列实例。
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    /**
     * 创建一个名为EMAIL_EXCHANGE的Fanout类型交换器，该交换器是持久化的。
     * @return 返回创建的交换器实例。
     */
    @Bean
    public FanoutExchange emailExchange() {
        return new FanoutExchange(EMAIL_EXCHANGE, true, false);
    }

    /**
     * 将emailQueue队列绑定到emailExchange交换器上。
     * @return 返回创建的绑定实例。
     */
    @Bean
    public Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

    /**
     * 创建一个名为SUBSCRIBE_QUEUE的队列，该队列是持久化的。
     * @return 返回创建的队列实例。
     */
    @Bean
    public Queue subscribeQueue() {
        return new Queue(SUBSCRIBE_QUEUE, true);
    }

    /**
     * 创建一个名为SUBSCRIBE_EXCHANGE的Fanout类型交换器，该交换器是持久化的。
     * @return 返回创建的交换器实例。
     */
    @Bean
    public FanoutExchange subscribeExchange() {
        return new FanoutExchange(SUBSCRIBE_EXCHANGE, true, false);
    }

    /**
     * 将subscribeQueue队列绑定到subscribeExchange交换器上。
     * @return 返回创建的绑定实例。
     */
    @Bean
    public Binding bindingSubscribeDirect() {
        return BindingBuilder.bind(subscribeQueue()).to(subscribeExchange());
    }


}
