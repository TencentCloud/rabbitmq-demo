package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.reply;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoListener {

    @RabbitListener(queues = "${demo.reply.entrance}")
    @SendTo("${demo.reply.exit}")
    public String replyingListener(String content) {
        log.info("收到消息，并向其它队列发信：" + content);
        return content;
    }

    @RabbitListener(queues = "${demo.reply.exit}")
    public void exit(String content) {
        log.info("收到消息：" + content);
    }

    @RabbitListener(queues = "${demo.reply.entrance}")
    @SendTo("${demo.reply.exit}")
    public Message<String> replyingListener2(String content) {
        log.info("收到消息，并向其它队列发信：" + content);
        return MessageBuilder.withPayload(content).build();
    }
}
