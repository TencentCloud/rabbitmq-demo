package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.pubconfirm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class PublishConfirmDemo {

    public static void main(String[] args) throws Exception {
        // 启用 publisher-confirm-correlated profile，具体额外生效的配置项请参阅 application-publisher-confirm-correlated.yml
        System.setProperty("spring.profiles.active", "publisher-confirm-correlated");

        ConfigurableApplicationContext context = SpringApplication.run(PublishConfirmDemo.class);
        context.getBean(DemoLauncher.class).runDemo();
        context.close();
    }
}
