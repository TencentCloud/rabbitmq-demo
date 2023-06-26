package com.tencent.cloud.tdmq.rabbitmq.demo.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;

public class Producer {

    public static void main(String[] args) throws Exception {
        // 建立工厂，设置连接信息
        // 请将 ConnectionProps 中的常量修改为你的连接信息，或者直接在下面修改
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConnectionProps.HOST);
        factory.setPort(ConnectionProps.PORT);
        factory.setUsername(ConnectionProps.USERNAME);
        factory.setPassword(ConnectionProps.PASSWORD);
        factory.setVirtualHost(ConnectionProps.VHOST);

        // 设置队列名称
        String queueName = "demo.helloworld";

        // 创建连接并创建 Channel
        // try-with-resources 可以在块结束时关闭资源
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 声明队列；确保队列存在，如果没有则会按给定配置新建一个
            channel.queueDeclare(queueName, false, false, false, null);

            for (int i = 0; i < 1000; i++) {
                String message = "Hello World! " + i;
                // 发送消息，这里使用默认交换机（空白字符串），可以直接使用队列名作为 routingKey
                channel.basicPublish("", queueName, null, message.getBytes());
                System.out.println("已发送消息 '" + message + "'");
                Thread.sleep(500);
            }
        }
    }
}
