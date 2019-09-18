package msgproperties.replyto;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.UUID;

/**
 * @description: 生产者发送消息，并接收消费者应答
 * @author: Mr.He
 * @date: 2019-08-31 13:54
 **/
public class ReplyProducer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_direct_logs";

    public static void main(String[] args) throws Exception {

        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 创建应答队列
        final String replyQueueName = channel.queueDeclare().getQueue();
        for (int i = 0; i < 3; i++) {
            String logLevel = "info";
            String msg = "Hello consumer,这是消息"+i;
            // 生成消息id
            String msgId = UUID.randomUUID().toString();
            // 1.定义messageProperties
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                    .messageId(msgId) // 设置消息id
                    .replyTo(replyQueueName)   // 设置应答队列
                    .build();
            // 2.发布消息(带有消息属性)
            channel.basicPublish(EXCHANGE_NAME, logLevel, basicProperties, msg.getBytes());
            System.out.println("发送消息："+msg);
        }

        // 3.生产者接收消费者应答
        channel.basicConsume(replyQueueName,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("接收到消费者应答，消息id=["+properties.getMessageId()+"]," +
                        "routeKey=["+envelope.getRoutingKey()+"]  msgBody:"+new String(body));
            }
        });




//        channel.close();
//        connection.close();

    }
}
