package msgproperties.replyto;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 消费者接收消息，并发送应答到生产者
 * @author: Mr.He
 * @date: 2019-08-31 13:55
 **/
public class ReplyConsumer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_direct_logs";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_info";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        final Channel channel = connection.createChannel();

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
                System.out.println("ReplyConsumer获得消息："+new String(body));
                // 定义响应的消息属性
                AMQP.BasicProperties respProperties = new  AMQP.BasicProperties.Builder()
                            .replyTo(properties.getReplyTo()) // 设置应答消息发送的队列
                            .correlationId(properties.getMessageId()) //设置关联消息的id，即对哪个消息id做出响应
                            .build();
                // 发送响应信息到生产者
                String respMsg = "我已经收到消息id=["+properties.getMessageId()+"]的消息";
                System.out.println("消费者发送应答："+respMsg);
                // 绑定默认交换器
                // 默认交换器隐式地绑定到每个队列，其路由键等于队列名称
                channel.basicPublish("",properties.getReplyTo(),respProperties,respMsg.getBytes());
            }
        };
        // 消费者正式开始在指定队列上消费消息
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }

}
