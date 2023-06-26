package com.tencent.cloud.tdmq.rabbitmq.demo.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;

public class ProducerFanout {

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

            channel.exchangeDeclare("demo.exchange.fanout", BuiltinExchangeType.FANOUT);
            for (String queueName : Consumer.QUEUES) {
                channel.queueDeclare(queueName, false, false, false, null);
                channel.queueBind(queueName, "demo.exchange.fanout", "");
            }

            // 发送消息，fanout 交换机会无视 routingKey 而直接发送到所有绑定了的队列
            channel.basicPublish("demo.exchange.fanout", "whatever", null, "Hello everyone!".getBytes());

            System.out.println("发送完毕");
        }
    }
}
