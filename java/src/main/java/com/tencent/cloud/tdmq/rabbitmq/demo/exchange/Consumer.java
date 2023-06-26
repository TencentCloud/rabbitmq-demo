package com.tencent.cloud.tdmq.rabbitmq.demo.exchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.nio.charset.StandardCharsets;

public class Consumer {

    public static final String[] QUEUES = new String[]{
            "demo.exchange.alice",
            "demo.exchange.bob",
            "demo.exchange.carol",
            "demo.exchange.dan",
            "demo.exchange.eve"
    };

    public static void main(String[] args) throws Exception {
        // 建立工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConnectionProps.HOST);
        factory.setPort(ConnectionProps.PORT);
        factory.setUsername(ConnectionProps.USERNAME);
        factory.setPassword(ConnectionProps.PASSWORD);
        factory.setVirtualHost(ConnectionProps.VHOST);

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            for (String queueName : QUEUES) {
                channel.queueDeclare(queueName, false, false, false, null);
                channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println("队列 " + queueName + " 收到消息 '" + message + "'");
                }, consumerTag -> {
                });
            }

            System.out.println("已开始接收消息，请执行 " + ProducerDirect.class.getName() + " 或 "
                    + ProducerFanout.class.getName());
            System.in.read();
        }
    }
}
