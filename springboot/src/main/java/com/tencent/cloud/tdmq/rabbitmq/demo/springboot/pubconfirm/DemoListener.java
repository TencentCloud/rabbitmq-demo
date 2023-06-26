package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.pubconfirm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoListener {

    @RabbitListener(queues = "${demo.pubconfirm.queue}")
    public void listen(String content) {
        log.info("收到消息：" + content);
    }
}
