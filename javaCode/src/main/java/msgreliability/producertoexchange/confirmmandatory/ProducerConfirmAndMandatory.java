package msgreliability.producertoexchange.confirmmandatory;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 生产者开启confirm消息异步确认+mandatory失败通知
 *
 * @author: Mr.He
 * @date: 2019-08-21 21:14
 **/
public class ProducerConfirmAndMandatory {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_producerConfirm";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 开启消息确认模式
        channel.confirmSelect();

        // 开启消息确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("RabbitMQ成功接收到["+deliveryTag+"]消息···");
            }
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("RabbitMQ没有接收到["+deliveryTag+"]消息！！！");
            }
        });

        // 监听路由失败的返回信息
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText,
                                     String exchange, String routingKey,
                                     AMQP.BasicProperties properties,
                                     byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("返回的replyText ："+replyText);
                System.out.println("返回的exchange ："+exchange);
                System.out.println("返回的routingKey ："+routingKey);
                System.out.println("返回的message ："+message);
            }
        });

        // 定义日志级别   作为路由key使用
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            String msg = "Hello ,这是["+logLevel+"]消息"+i;
            // 发布消息到 test_producerConfirm 交换机
            channel.basicPublish(EXCHANGE_NAME, logLevel,true, null, msg.getBytes());
            System.out.println("发送消息："+msg);
        }
        // 开启了监听不要把信道关了，不然发送完消息就结束了
//        channel.close();
//        connection.close();

    }
}
