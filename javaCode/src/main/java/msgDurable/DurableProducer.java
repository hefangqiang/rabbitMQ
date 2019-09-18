package msgDurable;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import utils.ConnectionUtil;

/**
 * @description: 生产者 创建持久化交换机 并发布持久化消息
 * @author: Mr.He
 * @date: 2019-08-31 13:02
 **/
public class DurableProducer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_durable_exchange";

    public static void main(String[] args) throws Exception {

        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型的持久化交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT,true);

        // 定义日志级别   作为路由key使用
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            String msg = "Hello Direct,这是["+logLevel+"]消息"+i;
            // 发布消息到 test_direct_logs 交换机
            channel.basicPublish(EXCHANGE_NAME, logLevel, MessageProperties.PERSISTENT_BASIC, msg.getBytes());
            System.out.println("发送消息："+msg);
        }
        channel.close();
        connection.close();

    }
}
