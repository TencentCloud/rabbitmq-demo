package com.tencent.cloud.tdmq.rabbitmq.demo.mqtt;

import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.MQTT;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class Consumer {

    /**
     * MQTT 演示程序的消费者程序。
     * 请先修改 {@link ConnectionProps.MQTT} 中的配置项。
     */
    public static void main(String[] args) throws Exception {
        // 生成一个唯一的 clientId，并由此创建 MqttClient
        String clientId = UUID.randomUUID().toString();
        IMqttClient mqttClient = new MqttClient(MQTT.SERVER_URI, clientId);

        // 一些连接配置
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(MQTT.USERNAME);
        connectOptions.setPassword(MQTT.PASSWORD.toCharArray());
        connectOptions.setAutomaticReconnect(true);
        connectOptions.setConnectionTimeout(10);
        connectOptions.setCleanSession(true);

        // 使用刚才的配置来建立连接
        mqttClient.connect(connectOptions);

        String targetTopic = "demo/mqtt";

        // 接收消息
        mqttClient.subscribe(targetTopic, (topic, message) -> {
            String receivedContent = new String(message.getPayload(), StandardCharsets.UTF_8);
            System.out.println("收到消息：" + receivedContent);
        });

        System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
        System.in.read();

        // 断开连接并关闭客户端
        mqttClient.disconnect();
        mqttClient.close();
    }
}
