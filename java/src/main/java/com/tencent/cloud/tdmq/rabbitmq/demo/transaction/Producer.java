package com.tencent.cloud.tdmq.rabbitmq.demo.transaction;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;

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

            // 创建交换机、队列，以及其间的绑定
            channel.exchangeDeclare("demo.tx", BuiltinExchangeType.FANOUT);
            channel.queueDeclare("demo.tx", false, false, false, null);
            channel.queueBind("demo.tx", "demo.tx", "");

            // 开启事务
            channel.txSelect();

            try {
                for (int i = 0; i < 100; i++) {
                    String message = "第 " + i + " 条消息，发送于 " + LocalDateTime.now();
                    channel.basicPublish("demo.tx", "", null, message.getBytes());
                    System.out.println(message);
                }
                // if (true) { throw new RuntimeException("取消注释以调用 Channel#txRollback 回滚"); }
                channel.txCommit();  // 提交事务
                System.out.println("事务已提交");
            } catch (Exception ex) {
                ex.printStackTrace();
                channel.txRollback();  // 回滚事务
                System.out.println("事务已回滚");
            }
        }
    }
}
