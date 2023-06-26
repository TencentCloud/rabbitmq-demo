package com.tencent.cloud.tdmq.rabbitmq.demo.shovel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Shovel.DownstreamFromClient;
import java.nio.charset.StandardCharsets;

public class Consumer {

    /**
     * Shovel 演示程序的消费者程序。
     * 请先修改 {@link ConnectionProps.Shovel} 中的配置项，并执行 {@link Configurer} 完成配置。
     */
    public static void main(String[] args) throws Exception {
        // 建立工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(DownstreamFromClient.HOST);
        factory.setPort(DownstreamFromClient.PORT);
        factory.setUsername(DownstreamFromClient.USERNAME);
        factory.setPassword(DownstreamFromClient.PASSWORD);
        factory.setVirtualHost(DownstreamFromClient.VHOST);

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 声明队列
            channel.queueDeclare("demo.shovel.downstream.destination", false, false, false, null);

            // 消费消息
            channel.basicConsume("demo.shovel.downstream.destination", true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("接收到消息 '" + message + "'");
            }, consumerTag -> {
            });

            System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
            System.in.read();
        }
    }
}
