package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.delayed;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("delayedDemoProducer")
public class ProducerController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${demo.delayed.pending-queue-ttl}")
    String queueWithTTL;

    @Value("${demo.delayed.pending-queue-ttl-ms}")
    int queueTTL;

    @Value("${demo.delayed.pending-queue-nottl}")
    String queueWithoutTTL;

    @Value("${demo.delayed.exchange-x-delayed-message}")
    String delayedExchange;

    @Value("${demo.delayed.delayed-queue}")
    String finalQueue;

    @PostMapping("/delayed/queueTTL/send")
    @ResponseBody
    public List<String> sendToQueueWithTTL() {
        String message = "（延时队列）发送于 " + LocalDateTime.now() + "，预计延时 " + queueTTL + " 毫秒（统一设置时长）";
        rabbitTemplate.send(queueWithTTL, new Message(message.getBytes(StandardCharsets.UTF_8)));
        return Collections.singletonList(message);
    }

    @PostMapping("/delayed/queueNoTTL/send")
    @ResponseBody
    public List<String> sendToQueueWithoutTTL(@RequestParam int ttl) {
        String content = "（延时队列）发送于 " + LocalDateTime.now() + "，预计延时 " + ttl + " 毫秒";
        Message message = new Message(content.getBytes(StandardCharsets.UTF_8));
        message.getMessageProperties().setExpiration(Integer.toString(ttl));  // 设定延时时间，实质上是存活时间
        rabbitTemplate.send(queueWithoutTTL, message);
        return Collections.singletonList(content);
    }

    @PostMapping("/delayed/exchange/send")
    @ResponseBody
    public List<String> sendToDelayedExchange(@RequestParam int ttl) {
        String content = "（延时交换机）发送于 " + LocalDateTime.now() + "，预计延时 " + ttl + " 毫秒";
        Message message = new Message(content.getBytes(StandardCharsets.UTF_8));
        message.getMessageProperties().setDelay(ttl);  // 设定延时时间，只适用于延时交换机
        rabbitTemplate.send(delayedExchange, "", message);
        return Collections.singletonList(content);
    }
}
