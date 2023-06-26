package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.serialization;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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

        DemoDTO toSend = new DemoDTO("a", "b", "c");
        log.info("准备开始发送对象：" + toSend);

        rabbitTemplate.convertAndSend(toSend);
        Object receivedAndConverted = rabbitTemplate.receiveAndConvert();
        log.info("接收到已转换的对象：" + receivedAndConverted);

        rabbitTemplate.convertAndSend(toSend);
        Message receivedRaw = rabbitTemplate.receive();
        log.info("接收到原始消息：" + Arrays.toString(receivedRaw.getBody()));
        try {
            log.info("转换为文本：" + new String(receivedRaw.getBody(), StandardCharsets.UTF_8));
        } catch (Exception ex) {
            log.info("原始信息无法转换为文本");
        }
    }
}
