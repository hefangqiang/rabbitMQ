package exchange.direct;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 一个queue，有多个消费者
 *               消息在消费者之间的轮询发送(多个消费者平摊一个队列中的消息)
 * @author: Mr.He
 * @date: 2019-08-19 21:22
 **/
public class MultiConsumerOneQueue1 {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_direct_logs";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_multiconsumer";

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
        // 3.队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < logsLevel.length; i++) {
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,logsLevel[i]);
        }

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("MultiConsumerOneQueue1获得消息："+new String(body));
            }
        };
        channel.basicConsume(QUEUE_NAME,true,consumer);

    }
}
