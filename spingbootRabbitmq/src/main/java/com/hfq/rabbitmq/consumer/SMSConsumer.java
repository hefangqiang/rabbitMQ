package com.hfq.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @description: 接收sms消息 手动签收
 * @author: Mr.He
 * @date: 2019-09-02 21:55
 **/
@Component
public class SMSConsumer implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            System.out.println("收到sms信息:" + new String(message.getBody(), "utf-8"));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("手动签收成功");
        } catch (Exception e) {
            System.out.println("sms信息接收失败！！！e："+e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }
    }
}
