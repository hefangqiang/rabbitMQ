package utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: rabbitMQ 连接获取
 * @author: Mr.He
 * @date: 2019-08-18 21:36
 **/
public class ConnectionUtil {
    private final static String HOST = "192.168.161.150";
    private final static int PORT = 5672;
    private final static String VHOST="/vhost_hfq"; // 虚拟主机
    private final static String USERNAME = "hefangqiang";
    private final static String PASSWORD = "123456";

    public static Connection getConnection() throws IOException, TimeoutException {
        // 初始化连接工厂，连接到RabbitMQ
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setVirtualHost(VHOST);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        // 创建连接
        Connection connection = connectionFactory.newConnection();
        return connection;
    }
}
