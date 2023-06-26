package com.tencent.cloud.tdmq.rabbitmq.demo.exchange.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;

public class Producer {

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

            channel.exchangeDeclare("demo.exchange.topic", BuiltinExchangeType.TOPIC);

            channel.basicPublish("demo.exchange.topic", "demo.exchange.alice.e", null, "Hello Alice!".getBytes());
            channel.basicPublish("demo.exchange.topic", "demo.exchange.bob.b", null, "Hello Bob!".getBytes());
            channel.basicPublish("demo.exchange.topic", "demo.exchange.carol.l", null, "Hello Carol!".getBytes());
            channel.basicPublish("demo.exchange.topic", "demo.exchange.dan.n", null, "Hello Dan!".getBytes());
            channel.basicPublish("demo.exchange.topic", "demo.exchange.eve.e", null, "Hello Eve!".getBytes());

            System.out.println("发送完毕");
        }
    }
}
