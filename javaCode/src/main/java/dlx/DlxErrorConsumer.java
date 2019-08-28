package dlx;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description:  消费者 消费死信交换器中的消息(消息重新绑定的路由键为dlx.error)
 * @author: Mr.He
 * @date: 2019-08-28 20:46
 **/
public class DlxErrorConsumer {
    // 定义死信交换器
    private final static String DLX_EXCHANGE_NAME = "test_dlx";
    // 定义队列
    private final static String DLX_QUEUE_NAME = "test_dlx_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机，队列，在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 2.创建队列
        channel.queueDeclare(DLX_QUEUE_NAME,false,false,false,null);
        // 3.队列绑定到交换机，设置路由键
        channel.queueBind(DLX_QUEUE_NAME,DLX_EXCHANGE_NAME,"dlx.error");

        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("DlxErrorConsumer获得["+envelope.getRoutingKey()+"]消息："+new String(body));
            }
        };
        // 消费者正式开始在指定队列上消费消息
        channel.basicConsume(DLX_QUEUE_NAME,true,consumer);
    }


}
