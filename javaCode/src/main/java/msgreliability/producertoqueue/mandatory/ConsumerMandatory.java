package msgreliability.producertoqueue.mandatory;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;

/**
 * @description: 消费方
 * @author: Mr.He
 * @date: 2019-08-21 21:26
 **/
public class ConsumerMandatory {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_producerMandatory";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_mandatory";

    public static void main(String[] args) throws Exception {

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
//        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"error"); // 测试用
//        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"warning");

        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Consumer获得消息："+new String(body));
            }
        };
        // 消费者正式开始在指定队列上消费消息
        channel.basicConsume(QUEUE_NAME,true,consumer);

    }
}
