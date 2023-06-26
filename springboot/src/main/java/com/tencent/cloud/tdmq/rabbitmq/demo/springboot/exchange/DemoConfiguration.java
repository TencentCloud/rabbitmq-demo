package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange;

import com.tencent.cloud.tdmq.rabbitmq.demo.springboot.Utilities;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DemoConfiguration {

    @Bean
    public Exchange exchangeDirect(@Value("${demo.exchange.direct.exchange}") String exchangeName) {
        // 可以通过向 Spring 注册一个 Exchange，使得 RabbitMQ 在交换机不存在时创建该交换机
        return ExchangeBuilder.directExchange(exchangeName).durable(false).build();
    }

    @Bean
    public Exchange exchangeFanout(@Value("${demo.exchange.fanout.exchange}") String exchangeName) {
        return ExchangeBuilder.fanoutExchange(exchangeName).durable(false).build();
    }

    @Bean
    public Exchange exchangeTopic(@Value("${demo.exchange.topic.exchange}") String exchangeName) {
        return ExchangeBuilder.topicExchange(exchangeName).durable(false).build();
    }

    @Bean
    public Queue queueTopic(@Value("${demo.exchange.topic.queue}") String exchangeName) {
        // 可以通过向 Spring 注册 Queue 来在 RabbitMQ 中声明队列
        return QueueBuilder.nonDurable(exchangeName).build();
    }

    @Bean
    public Binding bindingTopic(
            @Qualifier("exchangeTopic") @Autowired Exchange exchange,
            @Qualifier("queueTopic") @Autowired Queue queue,
            @Value("${demo.exchange.topic.pattern}") String pattern
    ) {
        // 可以通过向 Spring 注册 Binding 来在 RabbitMQ 中声明绑定
        return BindingBuilder.bind(queue).to(exchange).with(pattern).noargs();
    }

    @Bean
    public static BeanFactoryPostProcessor batchRegister(
            @Qualifier("exchangeDirect") @Autowired Exchange direct,
            @Qualifier("exchangeFanout") @Autowired Exchange fanout,
            @Autowired Environment environment
    ) {
        // 使用 BeanFactoryPostProcessor 进行批量注册
        return beanFactory -> {
            ExchangeDemoProperties props = Utilities.requireProps(environment, ExchangeDemoProperties.class);

            Utilities.declareBindingsAndQueues(beanFactory, props.getDirect().getBindings(), direct);
            Utilities.declareFanoutBindingsAndQueues(beanFactory, props.getFanout().getQueues(), fanout);
        };
    }
}
