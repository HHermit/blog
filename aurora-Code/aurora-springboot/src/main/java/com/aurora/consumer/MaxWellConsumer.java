package com.aurora.consumer;

import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.MaxwellDataDTO;
import com.aurora.entity.Article;
import com.aurora.mapper.ElasticsearchMapper;
import com.aurora.util.BeanCopyUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.aurora.constant.RabbitMQConstant.MAXWELL_QUEUE;

@Component
@RabbitListener(queues = MAXWELL_QUEUE)
public class MaxWellConsumer {

    @Autowired
    private ElasticsearchMapper elasticsearchMapper;

    /**
     * @RabbitHandler 是一个注解，用于标记该方法是处理 RabbitMQ 消息的处理器。
     * 函数 processMessage 是一个消息处理器，用于处理接收到的消息。
     * 参数 message 是接收到的消息内容，类型为字符串。
     * 在函数体中，可以对消息进行相应的处理逻辑。
     * @param data
     */
    @RabbitHandler
    public void process(byte[] data) {
        // 将接收到的字节数组消息转换为MaxwellDataDTO对象
        MaxwellDataDTO maxwellDataDTO = JSON.parseObject(new String(data), MaxwellDataDTO.class);
        // 将MaxwellDataDTO中的数据转换为Article对象
        Article article = JSON.parseObject(JSON.toJSONString(maxwellDataDTO.getData()), Article.class);
        // 根据消息类型执行相应的操作
        switch (maxwellDataDTO.getType()) {
            case "insert":
            case "update":
                // 将Article对象保存到Elasticsearch中 先转化为ArticleSearchDTO对象
                elasticsearchMapper.save(BeanCopyUtil.copyObject(article, ArticleSearchDTO.class));
                break;
            case "delete":
                // 根据ID删除Elasticsearch中的记录
                elasticsearchMapper.deleteById(article.getId());
                break;
            default:
                // 对于未知类型的消息，不做任何处理
                break;
        }
    }
}