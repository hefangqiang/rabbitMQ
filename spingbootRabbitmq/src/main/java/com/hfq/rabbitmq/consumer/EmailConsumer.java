package com.hfq.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @description: 接收email消息 自动签收
 * @author: Mr.He
 * @date: 2019-09-02 21:55
 **/
@RabbitListener(queues = "springboot_queue_email")
public class EmailConsumer {

    @RabbitHandler
    private void getEmailMessage(String message) {
        System.out.println("接收到email消息："+message);
    }
}
