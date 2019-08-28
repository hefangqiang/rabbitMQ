package qos;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 消费者从队列中获取指定数量的消息进行消费
 * @author: Mr.He
 * @date: 2019-08-27 20:38
 **/
public class QosConsumer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_qos";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_qos";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机，队列，在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 2.创建队列
        channel.queueDeclare(QUEUE_NAME,false,false,true,null);
        // 3.队列绑定到交换机
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"info");

        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("QosConsumer获得消息："+new String(body));
                //手动，单条消息确认
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };

        /*设置预取消息数量，需要设置为手动确认模式
          param: prefetchCount预取消息大小，global是否应用到channel，true为channel级别，false为consumer级别
          可同时设置channel和consumer级别的消息预取条数*/
        // 整个通道最多channel有50条未确认(unAck)消息，一个consumer最多10条未确认(unAck)消息*/
        // channel.basicQos(50,true);
        channel.basicQos(10,false);

        // 消费者正式开始在指定队列上消费消息，手动确认，单条确认
        //channel.basicConsume(QUEUE_NAME,false,consumer);
        // 批量确认
        channel.basicConsume(QUEUE_NAME,false,new BatchAckConsumer(channel));
    }

}
