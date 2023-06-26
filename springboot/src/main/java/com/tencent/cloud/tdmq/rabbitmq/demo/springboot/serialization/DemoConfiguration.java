package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.serialization;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(
            RabbitTemplateConfigurer rabbitTemplateConfigurer,
            ConnectionFactory connectionFactory,
            @Qualifier("tempQueue") Queue tempQueue
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplateConfigurer.configure(rabbitTemplate, connectionFactory);
        rabbitTemplate.setRoutingKey(tempQueue.getActualName());
        rabbitTemplate.setDefaultReceiveQueue(tempQueue.getActualName());

        // 可以在这里修改要使用的 MessageConverter
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        return rabbitTemplate;
    }

    @Bean
    public Queue tempQueue() {
        return QueueBuilder.nonDurable().build();
    }

}
