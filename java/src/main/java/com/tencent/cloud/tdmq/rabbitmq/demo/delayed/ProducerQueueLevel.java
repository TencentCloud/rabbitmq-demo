package com.tencent.cloud.tdmq.rabbitmq.demo.delayed;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProducerQueueLevel {

    public static void main(String[] args) throws Exception {
        // 发送消息 ---> 交换机 demo.delayed.queuelevel.pending
        //          ---> 队列 demo.delayed.queuelevel.pending
        // --指定时间后-> 交换机 demo.delayed.queuelevel
        //          ---> 队列 demo.delayed

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
            channel.exchangeDeclare("demo.delayed.queuelevel.pending", BuiltinExchangeType.FANOUT);
            // 创建死信交换机，利用死信队列实现延时队列
            channel.exchangeDeclare("demo.delayed.queuelevel", BuiltinExchangeType.FANOUT);

            // 设定指定毫秒后进入死信队列，不配置其他死亡条件
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange", "demo.delayed.queuelevel");
            arguments.put("x-dead-letter-routing-key", "delayedmsg");
            arguments.put("x-message-ttl", 10000);  // 所有消息都会延时 10000 毫秒

            // 创建队列
            channel.queueDeclare("demo.delayed.queuelevel.pending", false, false, false, arguments);
            channel.queueDeclare("demo.delayed", false, false, false, null);

            // 绑定各自的交换机和队列
            channel.queueBind("demo.delayed.queuelevel.pending", "demo.delayed.queuelevel.pending", "");
            channel.queueBind("demo.delayed", "demo.delayed.queuelevel", "");

            // 发送消息
            for (int i = 0; i < 1000; i++) {
                String message = "发送于 " + LocalDateTime.now();
                channel.basicPublish("demo.delayed.queuelevel.pending", "", null, message.getBytes());
                System.out.println(message);
                Thread.sleep(500);
            }
        }
    }
}
