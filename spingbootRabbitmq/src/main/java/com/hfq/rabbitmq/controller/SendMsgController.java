package com.hfq.rabbitmq.controller;

import com.hfq.rabbitmq.producer.MyConfirmCallBack;
import com.hfq.rabbitmq.producer.MyReturnCallBack;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 发送消息
 * @author: Mr.He
 * @date: 2019-09-02 21:43
 **/
@RestController
public class SendMsgController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private MyConfirmCallBack myConfirmCallBack;

    @Resource
    private MyReturnCallBack myReturnCallBack;


    @RequestMapping("/sendEmailMessage/{message}")
    public String sendEmailMessage(@PathVariable String message){
//        CorrelationData correlationData = new CorrelationData();
//        correlationData.setId();
        System.out.println("发送Email消息：" + message);
        rabbitTemplate.setMandatory(true); // 开启失败通知
        rabbitTemplate.setConfirmCallback(myConfirmCallBack); // 消息确认
        rabbitTemplate.setReturnCallback(myReturnCallBack); // 失败通知

        // 发送消息
        rabbitTemplate.convertAndSend("springboot_exchange","springboot.email",message);
        return "email消息发送成功！！";
    }

    @RequestMapping("/sendSmsMessage/{message}")
    public String sendSmsMessage(@PathVariable String message){
        System.out.println("发送SMS消息：" + message);
        rabbitTemplate.setMandatory(true); // 开启失败通知
        rabbitTemplate.setConfirmCallback(myConfirmCallBack); // 消息确认
        rabbitTemplate.setReturnCallback(myReturnCallBack); // 失败通知
        // 发送消息
        rabbitTemplate.convertAndSend("springboot_exchange","springboot.sms",message);
        return "sms消息发送成功！！";
    }
}
