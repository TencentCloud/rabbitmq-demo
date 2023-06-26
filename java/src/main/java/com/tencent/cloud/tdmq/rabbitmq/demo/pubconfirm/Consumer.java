package com.tencent.cloud.tdmq.rabbitmq.demo.pubconfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.nio.charset.StandardCharsets;

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

            // 声明交换机、队列，以及其间的绑定
            channel.exchangeDeclare("demo.pubconfirm", BuiltinExchangeType.FANOUT);
            channel.queueDeclare("demo.pubconfirm", false, false, false, null);
            channel.queueBind("demo.pubconfirm", "demo.pubconfirm", "");

            // 消费消息
            channel.basicConsume("demo.pubconfirm", true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("接收到消息 '" + message + "'");
            }, consumerTag -> {
            });

            System.out.println("已开始接收消息，请执行 " + ProducerSync.class.getName() +
                    " 或 " + ProducerSyncBulk.class.getName() + " 或 " + ProducerAsync.class.getName());
            System.in.read();
        }
    }
}
