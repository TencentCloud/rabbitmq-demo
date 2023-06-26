package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.tls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TLSDemo {

    public static void main(String[] args) {
        // 启用 tls profile，具体额外生效的配置项请参阅 application-tls.yml
        System.setProperty("spring.profiles.active", "tls");

        ConfigurableApplicationContext context = SpringApplication.run(TLSDemo.class, args);
        context.getBean(DemoLauncher.class).runDemo();
        context.close();
    }
}
