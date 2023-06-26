package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.tls;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoLauncher {

    @Autowired
    @Qualifier("tempQueue")
    Queue tempQueue;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void runDemo() {
        rabbitTemplate.setRoutingKey(tempQueue.getActualName());
        rabbitTemplate.setDefaultReceiveQueue(tempQueue.getActualName());

        Message msg = MessageBuilder.withBody("这是 TLS 连接发送的消息".getBytes(StandardCharsets.UTF_8)).build();
        rabbitTemplate.send(msg);
        log.info("收到消息：{}", rabbitTemplate.receive(5000));
    }
}
