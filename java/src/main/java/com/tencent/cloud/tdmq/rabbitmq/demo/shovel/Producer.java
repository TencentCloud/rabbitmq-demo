package com.tencent.cloud.tdmq.rabbitmq.demo.shovel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Shovel.UpstreamFromClient;

public class Producer {

    /**
     * Shovel 演示程序的生产者程序。
     * 请先修改 {@link ConnectionProps.Shovel} 中的配置项，并执行 {@link Configurer} 完成配置。
     */
    public static void main(String[] args) throws Exception {
        System.out.println("执行 " + Configurer.class.getName() + " 以建立 Shovel");
        Thread.sleep(2000);

        // 建立工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(UpstreamFromClient.HOST);
        factory.setPort(UpstreamFromClient.PORT);
        factory.setUsername(UpstreamFromClient.USERNAME);
        factory.setPassword(UpstreamFromClient.PASSWORD);
        factory.setVirtualHost(UpstreamFromClient.VHOST);

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 声明队列
            channel.queueDeclare("demo.shovel.upstream.source", false, false, false, null);

            for (int i = 0; i < 1000; i++) {
                String message = "Hello World! " + i;
                channel.basicPublish("", "demo.shovel.upstream.source", null, message.getBytes());
                System.out.println("已发送消息 '" + message + "'");
                Thread.sleep(500);
            }
        }
    }
}
