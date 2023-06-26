package com.tencent.cloud.tdmq.rabbitmq.demo.tls;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.TLS;
import java.nio.charset.StandardCharsets;

public class Consumer {

    /**
     * TLS / SSL 演示程序的消费者程序。
     * 请修改 {@link ConnectionProps.TLS} 中的配置项，并打包上传到服务器执行。
     */
    public static void main(String[] args) throws Exception {
        System.out.println("请阅读 README.md 后再使用本 demo");

        // 建立工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();

        factory.useSslProtocol(SSLConfig.sslContext());  // 启用 SSL
//        factory.enableHostnameVerification();  // 验证证书已被颁发给指定域名，腾讯云没有提供域名，所以不开启此项

        factory.setHost(TLS.HOST);
        factory.setPort(TLS.PORT);
        factory.setUsername(TLS.USERNAME);
        factory.setPassword(TLS.PASSWORD);
        factory.setVirtualHost(TLS.VHOST);

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 声明队列
            channel.queueDeclare("demo.helloworld", false, false, false, null);

            // 消费消息
            channel.basicConsume("demo.helloworld", true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("接收到消息 '" + message + "'");
            }, consumerTag -> {
            });

            System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
            System.in.read();
        }
    }
}
