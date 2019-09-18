package queueproperties;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @description: 队列属性设置
 * @author: Mr.He
 * @date: 2019-09-17 21:05
 **/
public class ConsumerQueueProperties {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test__exchange";
    // 定义队列
    private final static String QUEUE_NAME = "test__queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机，队列，在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建direct类型的持久化交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT,true);

        // 定义队列参数
        Map<String,Object> arguments  = new HashMap<String,Object>();
        arguments.put("x-max-length",1000); // 设置队列最大消息数为1000
        arguments.put("x-message-ttl",1000*10); // 设置队列中消息的过期时间为10秒
        // 2.创建队列(加入属性)
        channel.queueDeclare(QUEUE_NAME,true,false,false,arguments);
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
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }

}
