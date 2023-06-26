package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.delayed;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DelayedDemoListener {

    public final Collection<Message> received = new ConcurrentLinkedQueue<>();

    @RabbitListener(queues = "${demo.delayed.delayed-queue}")
    public void topicListener(Message message) {
        log.info("延时队列接收到消息：{}", new String(message.getBody(), StandardCharsets.UTF_8));
        received.add(message);
    }
}
