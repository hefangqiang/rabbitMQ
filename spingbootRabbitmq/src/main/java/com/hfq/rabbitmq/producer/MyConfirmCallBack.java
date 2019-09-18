package com.hfq.rabbitmq.producer;


import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @description： 生产者消息确认
 *                1.在basicPublish后，回调该方法 相当于ConfirmListener
 *                2.ack=true 说明发送到broker成功  false为发送失败（网络闪断）
 *                3.监听消息是否成功发送到broker
 * @author： Mr.He
 * @date： 2019-01-09 17:01
 **/
@Component
public class MyConfirmCallBack implements RabbitTemplate.ConfirmCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("消息发送到rabbitMQ---成功");
        } else {
            System.out.println("消息发送到rabbitMQ---失败,原因："+cause);
        }
    }
}
