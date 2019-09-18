package com.consumer;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @description： 接收消息
 * @author： Mr.He
 * @date： 2019-01-09 19:06
 **/
@Component
public class RecvMessage implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            System.out.println("收到信息:" + new String(message.getBody(), "utf-8"));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("手动签收成功");
        } catch (Exception e) {
            System.out.println("信息接收失败！！！message："+e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }
    }

}
