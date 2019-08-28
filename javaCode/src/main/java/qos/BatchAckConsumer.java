package qos;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @description: 消费者进行手动批量确认
 * @author: Mr.He
 * @date: 2019-08-27 20:38
 **/
public class BatchAckConsumer extends DefaultConsumer {

    private int messageCount = 0;

    public BatchAckConsumer(Channel channel) {
        super(channel);
        System.out.println("批量消费者启动...");
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println("BatchAckConsumer批量消费者Received["+envelope.getRoutingKey()
                +"]"+message);
        messageCount++;
        // 消费10条消息 批量确认一次
        if (messageCount % 10 == 0){
            this.getChannel().basicAck(envelope.getDeliveryTag(),
                    true);
            System.out.println("BatchAckConsumer批量消费者进行消息的确认-------------");
        }
    }
}
