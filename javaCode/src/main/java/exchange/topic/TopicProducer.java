package exchange.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 【topic 类型交换机】
 *                生产者发送消息到交换机，交换机跟据队列匹配的routeKey分配消息
 * @author: Mr.He
 * @date: 2019-08-20 20:40
 **/
public class TopicProducer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机.发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建 [fanout] 类型交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // 定义日志级别
        String[] logsLevel = {"error","info","warning"};
        // 定义机器码
        String[] diffs = {"A","B","C"};
        // 组装topic路由键，并发送消息
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            for (int j = 0; j < 3; j++) {
                String diff = diffs[j&3];
                // 组装routeKey 比如log.info.A
                String routeKey = "log."+logLevel+"."+diff;
                String msg = "Hello topic,这是["+routeKey+"]消息"+(i+j);
                // 发布消息到 test_topic_logs交换机
                channel.basicPublish(EXCHANGE_NAME, routeKey, null, msg.getBytes());
                System.out.println("发送消息："+msg);
            }

        }
        channel.close();
        connection.close();
    }
}
