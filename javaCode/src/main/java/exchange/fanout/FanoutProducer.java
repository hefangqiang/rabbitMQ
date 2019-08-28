package exchange.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 不处理路由键，广播模式。所有绑定到该交换机的队列都会收到  生产者发送的消息
 * @author: Mr.He
 * @date: 2019-08-19 19:50
 **/
public class FanoutProducer {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_fanout_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机.发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建 [fanout] 类型交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        // 定义日志级别   作为路由key使用
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            String msg = "Hello Fanout,这是["+logLevel+"]消息"+i;
            // 发布消息到 test_fanout_logs交换机
            channel.basicPublish(EXCHANGE_NAME, logLevel, null, msg.getBytes());
            System.out.println("发送消息："+msg);
        }
        channel.close();
        connection.close();
    }
}
