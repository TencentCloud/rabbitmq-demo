package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.delayed;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayedDemoConfiguration {

    /**
     * 延迟到期的消息最终到达该队列
     */
    @Bean
    public Queue delayedQueue(@Value("${demo.delayed.delayed-queue}") String delayedQueue) {
        return new Queue(delayedQueue);
    }

    /**
     * 延时交换机
     */
    @Bean
    public Exchange exchangeDelayedMessage(@Value("${demo.delayed.exchange-x-delayed-message}") String name) {
        // delayed() 会使交换机类型变为 x-delayed-message，并设置 x-delayed-type 为延时到期后表现出的交换机类型
        return ExchangeBuilder.fanoutExchange(name).delayed().durable(false).build();
        // 或者也可以使用 CustomExchange 来创建延时交换机
        // return new CustomExchange(name, "x-delayed-message", false, false, Collections.singletonMap("x-delayed-type", "fanout"));
    }

    @Bean
    public Binding bindingDelayedExchange(
            @Qualifier("exchangeDelayedMessage") @Autowired Exchange exchange,
            @Qualifier("delayedQueue") @Autowired Queue queue
    ) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    /**
     * 延时队列，统一延时时间；
     * 实质上是一个设定了存活时间、死信交换机、死信 routingKey 的死信队列
     */
    @Bean
    public Queue pendingQueueWithTTL(
            @Value("${demo.delayed.pending-queue-ttl}") String name,
            @Value("${demo.delayed.pending-queue-ttl-ms}") int ttl,
            @Value("${demo.delayed.delayed-queue}") String finalQueue
    ) {
        return QueueBuilder.nonDurable(name).ttl(ttl)
                .deadLetterExchange("").deadLetterRoutingKey(finalQueue).build();  // 使用默认交换机直接发送到目标队列
    }

    /**
     * 延时队列，在消息上设置延时时间；
     * 实质上是一个设定了死信交换机、死信 routingKey 的死信队列
     */
    @Bean
    public Queue pendingQueueWithoutTTL(
            @Value("${demo.delayed.pending-queue-nottl}") String name,
            @Value("${demo.delayed.delayed-queue}") String finalQueue
    ) {
        return QueueBuilder.nonDurable(name)
                .deadLetterExchange("").deadLetterRoutingKey(finalQueue).build();  // 使用默认交换机直接发送到目标队列
    }
}
