package com.tencent.cloud.tdmq.rabbitmq.demo.tls;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.TLS;

public class Producer {

    /**
     * TLS / SSL 演示程序的生产者程序。
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

            for (int i = 0; i < 1000; i++) {
                String message = "Hello World! " + i;
                // 发送消息，这里使用默认交换机（空白字符串），可以直接使用队列名作为 routingKey
                channel.basicPublish("", "demo.helloworld", null, message.getBytes());
                System.out.println("已发送消息 '" + message + "'");
                Thread.sleep(500);
            }
        }
    }
}
