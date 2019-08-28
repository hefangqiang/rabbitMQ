package msgreliability.producertoexchange.confirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 生产者开启confirm消息同步确认,当前消息确认后，才会发送并确认下一条消息
 *                消息成功发送到Exchange 则消息确认成功
 * @author: Mr.He
 * @date: 2019-08-21 21:14
 **/
public class ProducerSyncConfirm {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_producerConfirm";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 开启消息确认模式
        channel.confirmSelect();

        // 定义日志级别   作为路由key使用
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            String msg = "Hello ,这是["+logLevel+"]消息"+i;

            // 发布消息到 test_producerConfirm 交换机
            channel.basicPublish(EXCHANGE_NAME, logLevel, null, msg.getBytes());
            System.out.println("发送消息："+msg);

            // 等待消息确认
            if(channel.waitForConfirms()){
                System.out.println("send success");
            }else{
                System.out.println("send failure");
            }



        }
        // 开启了监听不要把信道关了，不然发送完消息就结束了
//        channel.close();
//        connection.close();

    }
}
