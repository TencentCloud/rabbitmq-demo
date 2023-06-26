package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.transaction;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            RabbitTemplateConfigurer rabbitTemplateConfigurer
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplateConfigurer.configure(rabbitTemplate, connectionFactory);
        rabbitTemplate.setChannelTransacted(true);  // 为 Channel 启用事务
        return rabbitTemplate;
    }

    @Bean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);  // 提供事务管理器
    }

    @Bean
    public Queue tempQueue() {
        return QueueBuilder.nonDurable().build();
    }
}
