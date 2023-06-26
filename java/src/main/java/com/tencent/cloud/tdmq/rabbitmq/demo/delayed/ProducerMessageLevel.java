package com.tencent.cloud.tdmq.rabbitmq.demo.delayed;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProducerMessageLevel {

    public static void main(String[] args) throws Exception {
        // 发送消息 ---> 交换机 demo.delayed.messagelevel.pending
        //          ---> 队列 demo.delayed.messagelevel.pending
        // --指定时间后-> 交换机 demo.delayed.messagelevel
        //          ---> 队列 demo.delayed

        // 建立工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConnectionProps.HOST);
        factory.setPort(ConnectionProps.PORT);
        factory.setUsername(ConnectionProps.USERNAME);
        factory.setPassword(ConnectionProps.PASSWORD);
        factory.setVirtualHost(ConnectionProps.VHOST);

        System.out.println("该种延时方法会存在头部阻塞问题，请优先考虑使用 "
                + ProducerExchangeLevel.class.getName() + " 的方案。");
        Thread.sleep(2000);

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 创建接收发信的交换机
            channel.exchangeDeclare("demo.delayed.messagelevel.pending", BuiltinExchangeType.FANOUT);
            // 创建死信交换机，利用死信队列实现延时队列
            channel.exchangeDeclare("demo.delayed.messagelevel", BuiltinExchangeType.FANOUT);

            // 配置死信队列，但不在这里配置任何死亡条件
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange", "demo.delayed.messagelevel");
            arguments.put("x-dead-letter-routing-key", "delayedmsg");

            // 创建队列
            channel.queueDeclare("demo.delayed.messagelevel.pending", false, false, false, arguments);
            channel.queueDeclare("demo.delayed", false, false, false, null);

            // 绑定各自的交换机和队列
            channel.queueBind("demo.delayed.messagelevel.pending", "demo.delayed.messagelevel.pending", "");
            channel.queueBind("demo.delayed", "demo.delayed.messagelevel", "");

            // 发送消息
            for (int i = 0; i < 1000; i++) {
                int ttl = (int) (4500 * Math.random() + 500);  // 为每条消息单独设置延时时长，这里随机延时 0.5 ~ 5 秒
                BasicProperties props = new Builder().expiration(Integer.toString(ttl)).build();  // 创建 props 以供传入
                String message = "发送于 " + LocalDateTime.now() + "，预计延时 " + ttl + " 毫秒";
                channel.basicPublish("demo.delayed.messagelevel.pending", "", props, message.getBytes());
                System.out.println(message);
                Thread.sleep(500);
            }
        }
    }
}
