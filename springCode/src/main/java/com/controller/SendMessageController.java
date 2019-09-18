package com.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @description: TODO
 * @author: Mr.He
 * @date: 2019-09-01 18:39
 **/
@Controller
public class SendMessageController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMsg/{message}")
    @ResponseBody
    public String sendMsg(@PathVariable String message){
        // 设置消息属性
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        // 发送消息
        rabbitTemplate.send("spring_topic_exchange","spring.email",
                new Message(message.getBytes(),messageProperties));
        return "发送成功!!";
    }
}
