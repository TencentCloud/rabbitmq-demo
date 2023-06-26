package com.tencent.cloud.tdmq.rabbitmq.demo.deadletter;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Producer {

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

            // 创建接收发信的交换机
            channel.exchangeDeclare("demo.deadletter.alive", BuiltinExchangeType.FANOUT);
            // 创建死信交换机
            channel.exchangeDeclare("demo.deadletter.dead", BuiltinExchangeType.FANOUT);

            // 死信队列需要对队列传入额外参数
            Map<String, Object> arguments = new HashMap<>();
            // 配置"死掉的消息"发送到哪个交换机、以何种 routingKey 发送
            arguments.put("x-dead-letter-exchange", "demo.deadletter.dead");
            arguments.put("x-dead-letter-routing-key", "deadmsg");
            // 配置一条消息怎样才算"死掉了"
            arguments.put("x-message-ttl", 10000);  // 指定毫秒后仍未被消费的消息成为死信
            arguments.put("x-max-length", 5);  // 堆积到指定条数后，新的消息成为死信
            arguments.put("x-max-length-bytes", 2000);  // 堆积到指定字节后，新的消息成为死信

            // 利用指定的信息创建死信队列
            channel.queueDeclare("demo.deadletter.alive", false, false, false, arguments);
            // 创建接收死信的队列，死信会根据前面的配置发送到死信交换机，然后进入死信队列
            channel.queueDeclare("demo.deadletter.dead", false, false, false, null);

            // 绑定各自的交换机和队列
            channel.queueBind("demo.deadletter.alive", "demo.deadletter.alive", "");
            channel.queueBind("demo.deadletter.dead", "demo.deadletter.dead", "");

            for (int i = 0; i < 1000; i++) {
                String message = "第 " + i + " 条消息，发送于 " + LocalDateTime.now();
                channel.basicPublish("demo.deadletter.alive", "", null, message.getBytes());
                System.out.println(message);
                Thread.sleep(1000);
            }
        }
    }
}
