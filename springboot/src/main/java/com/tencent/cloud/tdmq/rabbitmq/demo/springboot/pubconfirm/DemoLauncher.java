package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.pubconfirm;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoLauncher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void runDemo() throws ExecutionException, InterruptedException {
        log.info("正常发送，不使用 CorrelationData");
        rabbitTemplate.convertAndSend(rabbitTemplate.getRoutingKey(), "消息内容");
        log.info("消息已发送");

        log.info("正常发送，使用 CorrelationData");
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.convertAndSend(rabbitTemplate.getRoutingKey(), (Object) "消息内容", correlationData);
        log.info("消息已发送，等待发布者确认回执");
        Confirm confirm = correlationData.getFuture().get();
        log.info("收到发布者确认回执：" + (confirm.isAck() ? "ACK" : "NACK") + " - " + confirm);

        log.info("发往不存在的队列");
        correlationData = new CorrelationData("correlation2");
        rabbitTemplate.convertAndSend(UUID.randomUUID().toString(), (Object) "消息内容", correlationData);
        confirm = correlationData.getFuture().get();
        log.info("收到发布者确认回执：" + (confirm.isAck() ? "ACK" : "NACK") + " - " + confirm);

        log.info("发往不存在的交换机");
        correlationData = new CorrelationData("Correlation for message 3");
        rabbitTemplate.convertAndSend(UUID.randomUUID().toString(), "", "消息内容", correlationData);
        confirm = correlationData.getFuture().get();
        log.info("收到发布者确认回执：" + (confirm.isAck() ? "ACK" : "NACK") + " - " + confirm);
    }
}
