package dlx;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @description: 消费者    拒绝消息，使得消息到死信交换器
 * @author: Mr.He
 * @date: 2019-08-28 20:46
 **/
public class MakeDlxConsumer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_direct_logs";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_info";

    // 定义死信交换器
    private final static String DLX_EXCHANGE_NAME = "test_dlx";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机，队列，在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 2.创建队列
        //channel.queueDeclare(QUEUE_NAME,false,false,true,null);
        // 3.队列绑定到交换机
        // channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"info");

        // 创建死信交换器
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 定义参数map， 加入死信交换器配置
        Map<String,Object> arguments  = new HashMap<String,Object>();
        arguments.put("x-dead-letter-exchange",DLX_EXCHANGE_NAME);

        // 2.创建队列，加入map参数，绑定死信交换器
        channel.queueDeclare(QUEUE_NAME,false,false,true,arguments);
        // 3.队列绑定到交换机
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"info");


        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("MakeDlxConsumer 拒绝消息："+new String(body));
                // 拒绝消息，并且不重回原队列。 此时消息进入到死信交换器 test_dlx
                channel.basicNack(envelope.getDeliveryTag(),false,false);
            }
        };
        // 消费者正式开始在指定队列上消费消息
        channel.basicConsume(QUEUE_NAME,false,consumer);
    }

}
