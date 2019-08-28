package msgreliability.queuetoconsumer.confirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.ConnectionUtil;

/**
 * @description: 生产者
 * @author: Mr.He
 * @date: 2019-08-26 21:50
 **/
public class ProducerHandReply {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_consumer_handReply";

    public static void main(String[] args) throws Exception {

        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();

        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 定义日志级别   作为路由key使用
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            String msg = "Hello , 这是["+logLevel+"]消息"+i;
            // 发布消息到 test_direct_logs 交换机
            channel.basicPublish(EXCHANGE_NAME, logLevel, null, msg.getBytes());
            System.out.println("发送消息："+msg);
        }
        channel.close();
        connection.close();

    }

}
