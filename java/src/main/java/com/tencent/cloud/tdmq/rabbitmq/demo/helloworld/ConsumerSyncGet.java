package com.tencent.cloud.tdmq.rabbitmq.demo.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.nio.charset.StandardCharsets;

public class ConsumerSyncGet {

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
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 声明队列
            channel.queueDeclare(queueName, false, false, false, null);

            // 从指定队列中获取一条消息
            System.out.println("尝试接收一条消息");
            GetResponse response = channel.basicGet(queueName, true);
            if (response == null) {
                System.out.println("现在没有消息，请执行 " + Producer.class.getName());
            } else {
                String messageBody = new String(response.getBody(), StandardCharsets.UTF_8);
                System.out.println("接收到消息 '" + messageBody + "'");
            }
        }
    }
}
