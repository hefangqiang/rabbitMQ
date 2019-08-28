package exchange.direct;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 一个tcp连接，多个信道
 * @author: Mr.He
 * @date: 2019-08-19 21:43
 **/
public class MultiChannelConsumer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();

        Thread TWork;
        // 一个连接,5个信道
        for (int i = 0; i < 5; i++) {
            // 将连接作为参数，传递给每个线程
            TWork = new Thread(new ConsumerWork(connection));
            TWork.start();
        }
    }

    // Consumer工作线程
    static class ConsumerWork implements Runnable {
        final Connection connection; // 初始化连接

        public ConsumerWork(Connection connection) {
            this.connection = connection;
        }

        public void run() {
            try {
                // 创建信道
                final Channel channel = connection.createChannel();

                // 1.创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                // 2.创建队列 随机队列
                String queueName = channel.queueDeclare().getQueue().replace("amq.","test_queue.");
                //System.out.println(queueName);
                channel.queueDeclare(queueName,false,false,true,null);
                // 3.队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定
                String[] logsLevel = {"error","info","warning"};
                for (int i = 0; i < logsLevel.length; i++) {
                    channel.queueBind(queueName,EXCHANGE_NAME,logsLevel[i]);
                }

                Consumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        System.out.println("channel["+Thread.currentThread().getName()+"]获得消息："+new String(body));
                    }
                };
                channel.basicConsume(queueName,true,consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
