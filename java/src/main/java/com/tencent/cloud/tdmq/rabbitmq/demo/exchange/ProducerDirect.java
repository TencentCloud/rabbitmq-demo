package com.tencent.cloud.tdmq.rabbitmq.demo.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;

public class ProducerDirect {

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

            channel.exchangeDeclare("demo.exchange.direct", BuiltinExchangeType.DIRECT);
            for (String queueName : Consumer.QUEUES) {
                channel.queueDeclare(queueName, false, false, false, null);
            }

            // 创建交换机和队列的绑定
            channel.queueBind("demo.exchange.alice", "demo.exchange.direct", "alice");
            channel.queueBind("demo.exchange.bob", "demo.exchange.direct", "bob");
            channel.queueBind("demo.exchange.carol", "demo.exchange.direct", "carol");
            channel.queueBind("demo.exchange.dan", "demo.exchange.direct", "dan");
            channel.queueBind("demo.exchange.eve", "demo.exchange.direct", "eve");

            // 通过绑定的 routingKey 进行发送
            channel.basicPublish("demo.exchange.direct", "alice", null, "Hello Alice!".getBytes());
            channel.basicPublish("demo.exchange.direct", "bob", null, "Hello Bob!".getBytes());
            channel.basicPublish("demo.exchange.direct", "carol", null, "Hello Carol!".getBytes());
            channel.basicPublish("demo.exchange.direct", "dan", null, "Hello Dan!".getBytes());
            channel.basicPublish("demo.exchange.direct", "eve", null, "Hello Eve!".getBytes());

            System.out.println("发送完毕");
        }
    }
}
