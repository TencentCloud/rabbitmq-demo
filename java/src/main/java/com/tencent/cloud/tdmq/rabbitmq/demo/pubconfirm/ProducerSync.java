package com.tencent.cloud.tdmq.rabbitmq.demo.pubconfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;

public class ProducerSync {

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

                System.out.println("已经发送消息，等待 broker 发回确认回执：" + message);

                // 同步等待 broker 的确认回执；等待的是之前发送过的所有消息的确认回执
                // 一条发往 broker 的消息可以有 ack 或 nack 回执，这两种都是 broker 发送的，不要与消费者侧的 ack / nack 混淆
                channel.waitForConfirms();  // 一直等待，直至之前发送过的所有消息都收到了回执；返回 true 表示所有消息都 ack 了，返回 false 说明至少有一条消息 nack
                // channel.waitForConfirms(10000);  // 10000 毫秒后若仍未收到回执，则抛出 TimeoutException；其他与上面相同
                // channel.waitForConfirmsOrDie();  // 一直等待；若有任何消息 nack，则抛出异常
                // channel.waitForConfirmsOrDie(10000);  // 最多等待 10000 毫秒；若有任何消息 nack，则抛出异常

                // 我们之前只发送了一条消息就等待回执，所以可以认为之前的回执只确认了刚才那一条消息
                System.out.println("消息已发送并收到回执：" + message);
            }
            System.out.printf("逐条确认共计耗时 %.3f 秒\n", (System.nanoTime() - timestamp) / 1000000 / 1000.0);
        }
    }
}
