package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.reply;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class ReplyingDemo {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReplyingDemo.class, args);
        context.getBean(DemoLauncher.class).runDemo();
        context.close();
    }
}
