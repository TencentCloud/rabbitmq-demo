package com.tencent.cloud.tdmq.rabbitmq.demo.delayed;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class Consumer {

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

            // 声明队列，所有已经完成延时的消息最终会到达这里
            channel.queueDeclare("demo.delayed", false, false, false, null);

            // 收取已延时的消息
            channel.basicConsume("demo.delayed", true, (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("在 " + LocalDateTime.now() + " 收到消息 '" + msg + "'");
            }, consumerTag -> {
            });

            System.out.println("已开始接收消息，请执行 " + ProducerMessageLevel.class.getName() + " 或 " +
                    ProducerQueueLevel.class.getName() + " 或 " + ProducerExchangeLevel.class.getName());
            System.in.read();
        }
    }
}
