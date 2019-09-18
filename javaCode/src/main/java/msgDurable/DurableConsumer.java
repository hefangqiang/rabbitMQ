package msgDurable;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 消费者  创建持久化交换机和持久化队列 并接收消息
 * @author: Mr.He
 * @date: 2019-08-31 13:30
 **/
public class DurableConsumer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_durable_exchange";
    // 定义队列
    private final static String QUEUE_NAME = "test_durable_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机，队列，在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建direct类型的持久化交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT,true);
        // 2.创建持久化队列,关闭队列自动删除
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        // 3.队列绑定到交换机
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"info");

        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("DurableConsumer获得消息："+new String(body));
            }
        };
        //不消费消息，重启rabbit服务，可以看到交换器和队列以及消息还存在
        // 消费者正式开始在指定队列上消费消息
        //channel.basicConsume(QUEUE_NAME,true,consumer);
    }

}
