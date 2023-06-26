package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.reply;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DemoConfiguration {

    @Value("${demo.reply.entrance}")
    String entranceQueue;

    @Value("${demo.reply.exit}")
    String exitQueue;

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate output = new RabbitTemplate(connectionFactory);
        output.setRoutingKey(entranceQueue);
        output.setDefaultReceiveQueue(exitQueue);
        output.setMessageConverter(new Jackson2JsonMessageConverter());
        return output;
    }

    @Bean
    public Queue entranceQueue() {
        return QueueBuilder.nonDurable(entranceQueue).build();
    }

    @Bean
    public Queue exitQueue() {
        return QueueBuilder.nonDurable(exitQueue).build();
    }

}
