package com.hfq.rabbitmq.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @description： 生产者失败通知
 *                1.实现此方法在basicPublish失败时回调(消息无法成功路由到队列) 相当于 ReturnListener的功能
 *                2.在发布消息时设置mandatory等于true
 *                3.监听消息是否有相匹配的队列，没有时ReturnCallback将执行returnedMessage方法，消息将返给发送者
 * @author： Mr.He
 * @date： 2019-01-09 17:02
 **/
@Component
public class MyReturnCallBack implements RabbitTemplate.ReturnCallback {

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息["+new String(message.getBody())+"]路由失败,replyText:"+replyText+"  exchange:"+ exchange
        +"   routingKey:"+routingKey);
    }
}
