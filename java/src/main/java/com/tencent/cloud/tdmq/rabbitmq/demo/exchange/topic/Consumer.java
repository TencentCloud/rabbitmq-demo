package com.tencent.cloud.tdmq.rabbitmq.demo.exchange.topic;

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

        String[] bindingKeys = {
                "demo.exchange.alice.e",  // 可以收到发往 demo.exchange.alice.e 的消息
                "demo.exchange.*.e",      // 可以收到发往 demo.exchange.任意.e 的消息
                "demo.exchange.#"         // 可以收到发往 demo.exchange 开头的 routingKey 的消息
        };

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare("demo.exchange.topic", BuiltinExchangeType.TOPIC);

            for (String bindingKey : bindingKeys) {
                // 创建一个临时的队列，名称随机生成
                String tempQueueName = channel.queueDeclare().getQueue();

                // 使用指定的 routingKey 来将刚才创建的队列绑定到 topic 交换机
                // 这样一来我们的临时队列就可以收到所有使用某一模式的 routingKey 发送到该 topic 交换机的消息
                channel.queueBind(tempQueueName, "demo.exchange.topic", bindingKey);

                System.out.println("已将临时队列 " + tempQueueName + " 以 " + bindingKey + " 绑定到交换机");

                // 接收临时队列的消息
                channel.basicConsume(tempQueueName, true, (consumerTag, delivery) -> {
                    String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println("[" + bindingKey + "] " + msg);
                }, consumerTag -> {
                });
            }

            System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
            System.in.read();
        }
    }
}
