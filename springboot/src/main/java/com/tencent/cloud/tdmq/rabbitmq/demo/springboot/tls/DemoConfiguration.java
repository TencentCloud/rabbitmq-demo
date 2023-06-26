package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.tls;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfiguration {

    @Bean
    public Queue tempQueue() {
        return QueueBuilder.nonDurable().build();
    }

}
