package msgreliability.producertoqueue.mandatory;

import com.rabbitmq.client.*;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 生产者失败确认
 *               消息无法路由到队列，则收到失败通知(mq发送失败通知到producer，可能因为网络原因导致通过不一定可以发送到)
 * @author: Mr.He
 * @date: 2019-08-21 22:19
 **/
public class ProducerMandatory {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_producerMandatory";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);


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

        //信道关闭监听
//        channel.addShutdownListener(new ShutdownListener() {
//            @Override
//            public void shutdownCompleted(ShutdownSignalException cause) {
//                System.out.println("shutdown:"+cause);
//            }
//        });

        // 定义日志级别   作为路由key使用
        String[] logsLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String logLevel = logsLevel[i&3];
            String msg = "Hello ,这是["+logLevel+"]消息"+i;
            // 发布消息到 test_producerMandatory 交换机,开启mandatory失败通知
            channel.basicPublish(EXCHANGE_NAME, logLevel, true,null, msg.getBytes());
            System.out.println("发送消息："+msg);
        }
    }
}
