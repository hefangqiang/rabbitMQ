package msgreliability.queuetoconsumer.confirm;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 消费方手动回复，通知mq 消息成功消费
 *              服务器端一直没有收到消费者的ack信号，并且消费此消息的消费者已经断开连接，
 *              则服务器端会安排该消息重新进入队列，等待投递给下一个消费者（也可能还是原来的那个消费者）。
 * @author: Mr.He
 * @date: 2019-08-26 21:49
 **/
public class ConsumerHandReply {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_consumer_handReply";
    // 定义队列
    private final static String QUEUE_NAME = "test_queue_consumerHandReply";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        final Channel channel = connection.createChannel();

        /* 声明交换机，队列，在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 1.创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 2.创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, true, null);
        // 3.队列绑定到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "info");
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"error");
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"warning");

        // 定义消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try{
                   // int a = 1/0;
                    System.out.println("ConsumerHandReply获得消息：" + new String(body));
                    // 手动确认消息
                    // param：消息标签，是否批量确认(可一次确认多条)
                    channel.basicAck(envelope.getDeliveryTag(),false);
                    System.out.println("确认消息："+envelope.getDeliveryTag());
                }catch (Exception e){
                    e.getMessage();
                    // 一次可拒绝消息多条消息
                    // param: 消息标签，是否批量确认，被拒绝的消息是否重回队列(消息重回队列会到队列的头部，错误消息一直被拒绝，
                    // 可能造成死循环,最好不要重回队列)
                    channel.basicNack(envelope.getDeliveryTag(),false,false);
                    // 一次拒绝一条消息
                    //channel.basicReject(envelope.getDeliveryTag(),false);
                    System.out.println("拒绝消息："+envelope.getDeliveryTag());
                }
            }
        };
        // 消费者正式开始在指定队列上消费消息 异步推送
        channel.basicConsume(QUEUE_NAME, false, consumer);


        // 主动获取
//        while(true){
//            GetResponse getResponse =
//                    channel.basicGet(QUEUE_NAME, true);
//            if(null != getResponse){
//                System.out.println("received["
//                        +getResponse.getEnvelope().getRoutingKey()+"]"
//                        +new String(getResponse.getBody()));
//            }
//        }



    }

}
