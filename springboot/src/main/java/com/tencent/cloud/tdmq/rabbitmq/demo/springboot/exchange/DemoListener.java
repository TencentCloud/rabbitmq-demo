package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange;

import com.tencent.cloud.tdmq.rabbitmq.demo.springboot.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoListener {

    public final Collection<Message> topicReceived = new ConcurrentLinkedQueue<>();

    /**
     * 注册 RabbitMQ 队列的监听器（消费者）
     */
    @RabbitListener(queues = "${demo.exchange.topic.queue}")
    public void topicListener(Message message) {
        log.info("接收到来自 Topic 队列的消息：{}", new String(message.getBody(), StandardCharsets.UTF_8));
        topicReceived.add(message);  // 把收到的消息存储起来以供 Controller 展示；实际项目中应当进行业务处理
    }

    /**
     * 批量监听
     */
    @Component
    public static class CustomListenerContainer extends SimpleMessageListenerContainer {

        public final Collection<Message> received = new ConcurrentLinkedQueue<>();

        public CustomListenerContainer(
                @Autowired ConnectionFactory connectionFactory,
                @Autowired Environment environment
        ) {
            // 获取 application.yml 的配置项
            ExchangeDemoProperties props = Utilities.requireProps(environment, ExchangeDemoProperties.class);

            // 添加要监听的队列
            List<String> queueNames = new ArrayList<>();
            queueNames.addAll(props.getDirect().getBindings().values());
            queueNames.addAll(props.getFanout().getQueues());

            setConnectionFactory(connectionFactory);
            setQueueNames(queueNames.toArray(new String[0]));  // 设置要监听的队列
            setMessageListener(message -> {  // 设置消息消费处理器
                String queueName = message.getMessageProperties().getConsumerQueue();
                String messageContent = new String(message.getBody(), StandardCharsets.UTF_8);
                log.info(queueName + " 队列接收到消息：{}", messageContent);
                received.add(message);  // 把收到的消息存储起来以供 Controller 展示；实际项目中应当进行业务处理
            });
        }
    }
}
