package com.tencent.cloud.tdmq.rabbitmq.demo.pubconfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;

public class ProducerSyncBulk {

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

            // 启用发送确认功能
            channel.confirmSelect();

            // 创建交换机、队列，以及其间的绑定
            channel.exchangeDeclare("demo.pubconfirm", BuiltinExchangeType.FANOUT);
            channel.queueDeclare("demo.pubconfirm", false, false, false, null);
            channel.queueBind("demo.pubconfirm", "demo.pubconfirm", "");

            long timestamp = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                String message = "第 " + i + " 条消息，发送于 " + LocalDateTime.now();
                channel.basicPublish("demo.pubconfirm", "", null, message.getBytes());

                System.out.println("已经发送消息：" + message);
            }

            // 一直等待，直至之前发送过的所有消息都收到了 broker 的确认回执
            channel.waitForConfirms();

            System.out.println("所有消息已发送并收到回执");
            System.out.printf("批量确认共计耗时 %.3f 秒\n", (System.nanoTime() - timestamp) / 1000000 / 1000.0);
        }
    }
}
