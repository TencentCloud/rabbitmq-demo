package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.transaction;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DemoService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("tempQueue")
    Queue queue;

    public int clearQueue() {
        int counter = 0;
        Message message;
        while ((message = rabbitTemplate.receive(queue.getActualName(), 100)) != null) {
            log.info("收到消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
            counter++;
        }
        return counter;
    }

    /**
     * 演示事务成功的情况
     */
    @Transactional
    public void declarativeTransaction() {
        for (int i = 0; i < 3; i++) {
            rabbitTemplate.convertAndSend(queue.getActualName(), "你应该能看到这条消息 " + i);
        }
    }

    /**
     * 演示事务失败回滚的情况
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void declarativeTransactionException() {
        for (int i = 0; i < 3; i++) {
            rabbitTemplate.convertAndSend(queue.getActualName(), "你不应能看到这条消息 " + i);
        }
        throw new RuntimeException("抛出此异常是正常行为");
    }
}
