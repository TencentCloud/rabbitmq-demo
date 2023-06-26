package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.reply;

import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoLauncher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void runDemo() {
        Scanner sc = new Scanner(System.in);

        System.out.println("输入文本并回车以发送消息");
        while (sc.hasNextLine()) {
            String content = sc.nextLine();
            rabbitTemplate.convertAndSend(content);
            log.info("发送消息：" + content);
        }
    }
}
