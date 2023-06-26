package com.tencent.cloud.tdmq.rabbitmq.demo.mqtt;

import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.MQTT;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Producer {

    /**
     * MQTT 演示程序的生产者程序。
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

        for (int i = 0; i < 1000; i++) {
            MqttMessage message = new MqttMessage();
            String messageContent = "第 " + i + " 条消息，发送于 " + LocalDateTime.now();
            message.setPayload(messageContent.getBytes(StandardCharsets.UTF_8));
            message.setQos(0);  // MQTT 的 QoS 级别，0 为至多一次（可能漏），1 为至少一次（可能重复），2 为恰好一次
            message.setRetained(true);

            // 发送消息
            mqttClient.publish(targetTopic, message);

            System.out.println(messageContent);
        }

        // 断开连接并关闭客户端
        mqttClient.disconnect();
        mqttClient.close();
    }
}
