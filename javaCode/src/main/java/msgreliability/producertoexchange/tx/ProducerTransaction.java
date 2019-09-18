package msgreliability.producertoexchange.tx;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: 生产者开启事务，发送消息到Exchange，提交事务。
 *               如果RabbitMQ接收消息失败，则抛出异常IoExceptionIoException,
 *               生产者可捕获异常并回滚，对失败消息进行处理
 * @author: Mr.He
 * @date: 2019-08-21 20:32
 **/
public class ProducerTransaction {
    // 定义交换机
    private final static String EXCHANGE_NAME = "test_producerTX";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 建立连接到RabbitMQ
        Connection connection = ConnectionUtil.getConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        /* 声明交换机.在发送方或者消费方都可以定义，保险起见两边都定义 */
        // 创建direct类型交换机(防止发送消息的时候RabbitMQ 没有该exchange)
//        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);  测试事务失败 不创建交换机

        try {
            //开启事务
            channel.txSelect();
            // 定义日志级别   作为路由key使用
            String[] logsLevel = {"error","info","warning"};
            for (int i = 0; i < 3; i++) {
                String logLevel = logsLevel[i&3];
                String msg = "Hello ,这是["+logLevel+"]消息"+i;
                // 发布消息到 test_direct_logs 交换机
                channel.basicPublish(EXCHANGE_NAME, logLevel, null, msg.getBytes());
                System.out.println("发送消息："+msg);
            }
            //提交事务
            channel.txCommit();
        }catch (IOException e){ // 消息发送失败错误
            System.out.println("消息发送失败");
            e.getStackTrace();
            channel.txRollback();
            // 对失败消息消息进行处理
        }
        catch (Exception e){ //其他错误
            e.getStackTrace();
        }finally {
            channel.close();
            connection.close();
        }



    }
}
