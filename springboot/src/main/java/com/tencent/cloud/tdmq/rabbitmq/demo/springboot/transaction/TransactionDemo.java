package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement  // 启用声明式事务
@SpringBootApplication
public class TransactionDemo {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TransactionDemo.class, args);
        context.getBean(DemoLauncher.class).runDemo();
        context.close();
    }
}
