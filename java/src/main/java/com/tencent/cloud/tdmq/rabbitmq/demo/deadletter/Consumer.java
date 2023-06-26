package com.tencent.cloud.tdmq.rabbitmq.demo.deadletter;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

            // 声明需要用到的交换机和队列，注释请参见 Producer
            channel.exchangeDeclare("demo.deadletter.alive", BuiltinExchangeType.FANOUT);
            channel.exchangeDeclare("demo.deadletter.dead", BuiltinExchangeType.FANOUT);
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange", "demo.deadletter.dead");
            arguments.put("x-dead-letter-routing-key", "deadmsg");
            arguments.put("x-message-ttl", 10000);
            arguments.put("x-max-length", 5);
            arguments.put("x-max-length-bytes", 2000);
            channel.queueDeclare("demo.deadletter.alive", false, false, false, arguments);
            channel.queueDeclare("demo.deadletter.dead", false, false, false, null);
            channel.queueBind("demo.deadletter.alive", "demo.deadletter.alive", "");
            channel.queueBind("demo.deadletter.dead", "demo.deadletter.dead", "");

            // 限制只能预取 1 条，避免消息提前被取出而导致的不能死亡的情况
            channel.basicQos(1);

            // 从一般的（消息仍然存活的）消息队列中获取消息，然后手动将这些消息"杀死"
            channel.basicConsume("demo.deadletter.alive", false, (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("收到消息 '" + msg + "'，拒绝该消息，以放入死信队列。");
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            }, consumerTag -> {
            });

            // 从死信队列中获取消息
            channel.basicConsume("demo.deadletter.dead", true, (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                Map<String, Object> headers = delivery.getProperties().getHeaders();
                System.out.println("收到死信 '" + msg + "'，"
                        + "首次死亡的原因是 [" + headers.get("x-first-death-reason") + "]");
            }, consumerTag -> {
            });

            System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
            System.in.read();
        }
    }
}
