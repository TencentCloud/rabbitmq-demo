package com.tencent.cloud.tdmq.rabbitmq.demo.delayed;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class ProducerExchangeLevel {

    public static void main(String[] args) throws Exception {
        // 发送消息 ---> 交换机 demo.delayed.exchange --指定时间后-> 队列 demo.delayed

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

            // 创建交换机；延迟时间到之后，使用 fanout 类型交换机的行为
            // 需要启用相应插件才能使用 x-delayed-message 类型的交换机
            channel.exchangeDeclare("demo.delayed.exchange", "x-delayed-message", false, false,
                    Collections.singletonMap("x-delayed-type", BuiltinExchangeType.FANOUT.getType()));

            // 创建队列
            channel.queueDeclare("demo.delayed", false, false, false, null);

            // 绑定交换机和队列；消息会在交换机处停留，然后按照设定的 x-delayed-type 的规则发往目标队列
            channel.queueBind("demo.delayed", "demo.delayed.exchange", "");

            // 发送消息
            for (int i = 0; i < 1000; i++) {
                int ttl = (int) (4500 * Math.random() + 500);  // 随机延时 0.5 ~ 5 秒
                String message = "发送于 " + LocalDateTime.now() + "，预计延时 " + ttl + " 毫秒";

                // 为每条消息设定延时，然后作为 headers 放置在 props 中，进而在发信时传入
                Map<String, Object> headers = Collections.singletonMap("x-delay", ttl);
                BasicProperties props = new Builder().headers(headers).build();
                channel.basicPublish("demo.delayed.exchange", "", props, message.getBytes());

                System.out.println(message);
                Thread.sleep(500);
            }
        }
    }
}
