package com.tencent.cloud.tdmq.rabbitmq.demo.transaction;

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

            // 创建交换机、队列，以及其间的绑定
            channel.exchangeDeclare("demo.tx", BuiltinExchangeType.FANOUT);
            channel.queueDeclare("demo.tx", false, false, false, null);
            channel.queueBind("demo.tx", "demo.tx", "");

            // 获取消息
            channel.basicConsume("demo.tx", false, (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("收到消息 '" + msg + "'，拒绝该消息，以放入死信队列。");
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            }, consumerTag -> {
            });

            System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
            System.in.read();
        }
    }
}
