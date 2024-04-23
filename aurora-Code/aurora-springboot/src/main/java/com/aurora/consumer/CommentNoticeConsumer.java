package com.aurora.consumer;

import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.EmailDTO;
import com.aurora.util.EmailUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import static com.aurora.constant.RabbitMQConstant.EMAIL_QUEUE;

/**
 *  @description: 监听Rabbit中的邮件队列中的消息，用来发送 评论通知邮件
 *  
*/
@Component
@RabbitListener(queues = EMAIL_QUEUE)
public class CommentNoticeConsumer {

    @Autowired
    private EmailUtil emailUtil;

    /**
    * @Description: @RabbitHandler 标记处理消息的方法，与上边的Listener对应
    * @Param: [data：Rabbit中的队列中的消息]
    * @return: void
    */
    @RabbitHandler 
    public void process(byte[] data) {
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        emailUtil.sendHtmlMail(emailDTO);
    }

}
