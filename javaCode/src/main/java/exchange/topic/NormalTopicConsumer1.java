package exchange.topic;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 普通消费者
 *               获取test_queue_logA队列的消息
 *               (队列绑定交换机使用 【log.*.A】 作为匹配路由键，队列可以收到生产者发送的路由键位为
 *                log.info.A,log.error.A,log.warning.A 的消息)
 * @author: Mr.He
 * @date: 2019-08-20 20:58
 **/
public class NormalTopicConsumer1 {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_topic_logs";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_logA";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机，队列在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建 [fanout] 类型交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 2.创建队列
        channel.queueDeclare(QUEUE_NAME,false,false,true,null);
        // 3.队列绑定到交换机
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"log.*.A");

        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("NormalTopicConsumer1 [log.*.A] 获得消息："+new String(body));
            }
        };
        // 消费者正式开始在指定队列上消费消息
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }
}
