package com.hfq.rabbitmq.config;


import com.hfq.rabbitmq.consumer.SMSConsumer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @description: rabbitmq 配置类
 * @author: Mr.He
 * @date: 2019-09-02 21:19
 **/
@Configuration
public class RabbitMqConfig {

    @Resource
    public SMSConsumer smsConsumer;

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setEncoding("UTF-8");
        return rabbitTemplate;
    }

    // 定义队列 [springboot_queue_email]
    @Bean
    public Queue queueEmail(){
        return new Queue("springboot_queue_email",false,false,false);
    }

    // 定义队列 [springboot_queue_sms]
    @Bean
    public Queue queueSMS(){
        return new Queue("springboot_queue_sms",false,false,false);
    }

    // 定义topic类型交换器 [springboot_exchange]
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("springboot_exchange", false, false);
    }

    // 队列springboot.email 绑定到交换器springboot_exchange
    @Bean
    public Binding emailBindingExchange(){
        return BindingBuilder
                .bind(queueEmail())
                .to(topicExchange())
                .with("springboot.email");
    }

    // 队列springboot.sms 绑定到交换器springboot_exchange
    @Bean
    public Binding smsBindingExchange(){
        return BindingBuilder
                .bind(queueEmail())
                .to(topicExchange())
                .with("springboot.sms");
    }

    /* 消费者监听容器定义
       接收sms消息
       手动ack
       预取5条
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueueNames("springboot_queue_email");
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动签收
        simpleMessageListenerContainer.setPrefetchCount(5); // 预取模式 5条
        simpleMessageListenerContainer.setMessageListener(smsConsumer);
        return simpleMessageListenerContainer;
    }


}
