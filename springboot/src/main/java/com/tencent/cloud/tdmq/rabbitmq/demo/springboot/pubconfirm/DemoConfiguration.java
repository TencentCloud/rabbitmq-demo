package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.pubconfirm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DemoConfiguration {

    @Value("${demo.pubconfirm.queue}")
    String queueName;

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            RabbitTemplateConfigurer rabbitTemplateConfigurer
    ) {
        RabbitTemplate output = new RabbitTemplate();
        rabbitTemplateConfigurer.configure(output, connectionFactory);  // 应用 application.yml 中的配置

        output.setRoutingKey(queueName);
        output.setDefaultReceiveQueue(queueName);

        // 发布者确认的回调函数
        output.setConfirmCallback((correlation, ack, reason) -> {
            if (correlation != null) {
                log.info("ConfirmCallback 收到 " + (ack ? "ACK" : "NACK") + " - " + correlation);
            }
        });

        // 发布者返回的回调函数（mandatory 且 NACK 时会被调用）
        output.setReturnsCallback(returned -> {
            log.info("发布者返回："
                    + "replyCode - " + returned.getReplyCode() + "；"
                    + "replyText - " + returned.getReplyText() + "；"
                    + "交换机 - " + returned.getExchange() + "；"
                    + "Routing Key - " + returned.getRoutingKey() + "；"
                    + "消息 - " + returned.getMessage());
        });

        return output;
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.nonDurable(queueName).build();
    }
}
