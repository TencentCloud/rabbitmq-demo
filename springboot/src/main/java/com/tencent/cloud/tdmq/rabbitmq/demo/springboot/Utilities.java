package com.tencent.cloud.tdmq.rabbitmq.demo.springboot;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

public class Utilities {

    /**
     * 从 Environment 中读取标有 @ConfigurationProperties 注解的配置类。
     * 仅在特殊情况下使用，一般情况下可以直接 @Autowire。
     */
    public static <T> T requireProps(Environment environment, Class<T> type) {
        ConfigurationProperties annotation = type.getAnnotation(ConfigurationProperties.class);
        String prefix = Optional.of(annotation.prefix()).filter(s -> !"".equals(s)).orElse(annotation.value());
        return Binder.get(environment).bind(prefix, type).orElseThrow(() -> new RuntimeException("无法读取配置文件"));
    }

    // region 批量注册

    private static String getBeanNameForQueue(String queueName) {
        return "queue-" + queueName;
    }

    private static String getBeanNameForBinding(String queueName, String bindingKey, String exchangeName) {
        return "binding-" + queueName + "-" + bindingKey + "-" + exchangeName;
    }

    private static <T> T findBeanInBeanFactory(BeanFactory beanFactory, String beanName, Class<T> type) {
        try {
            return beanFactory.getBean(beanName, type);
        } catch (NoSuchBeanDefinitionException | BeanNotOfRequiredTypeException ex) {
            return null;
        }
    }

    private static Queue findQueueInBeanFactory(BeanFactory beanFactory, String queueName) {
        return findBeanInBeanFactory(beanFactory, getBeanNameForQueue(queueName), Queue.class);
    }

    private static Binding findBindingInBeanFactory(BeanFactory beanFactory, String queueName, String bindingKey,
            String exchangeName) {
        return findBeanInBeanFactory(
                beanFactory,
                getBeanNameForBinding(queueName, bindingKey, exchangeName),
                Binding.class
        );
    }

    /**
     * 批量注册队列和 binding 到 Spring，并且绑定到 Direct 交换机
     */
    public static <T> void declareBindingsAndQueues(
            ConfigurableBeanFactory beanFactory,
            Collection<T> bindingKeyAndQueueNames,
            Exchange bindTo,
            Function<T, String> bindingKeyExtractor,
            Function<T, String> queueNameExtractor
    ) {
        for (T item : bindingKeyAndQueueNames) {
            String bindingKey = bindingKeyExtractor.apply(item);
            String queueName = queueNameExtractor.apply(item);

            Queue queue = findQueueInBeanFactory(beanFactory, queueName);
            if (queue == null) {
                queue = QueueBuilder.nonDurable(queueName).build();
                beanFactory.registerSingleton(getBeanNameForQueue(queueName), queue);
            }

            Binding binding = BindingBuilder.bind(queue).to(bindTo).with(bindingKey).noargs();
            beanFactory.registerSingleton(
                    getBeanNameForBinding(queue.getName(), bindingKey, bindTo.getName()), binding
            );
        }
    }

    /**
     * 批量注册队列和 binding 到 Spring
     */
    public static void declareBindingsAndQueues(
            ConfigurableBeanFactory beanFactory,
            Map<String, String> bindingKeyAndQueueNames,
            Exchange bindTo
    ) {
        declareBindingsAndQueues(
                beanFactory,
                bindingKeyAndQueueNames.entrySet(),
                bindTo,
                Entry::getKey,
                Entry::getValue
        );
    }

    /**
     * 批量注册队列和 binding 到 Spring，并且绑定到 Fanout 交换机
     */
    public static void declareFanoutBindingsAndQueues(
            ConfigurableBeanFactory beanFactory,
            List<String> queueNames,
            Exchange bindTo
    ) {
        declareBindingsAndQueues(beanFactory, queueNames, bindTo, e -> "", e -> e);
    }

    // endregion 批量注册

    /**
     * 用于展示消息的 View Object
     */
    public static class MessageView {

        public final String queue;
        public final String content;

        public MessageView(String queue, String content) {
            this.queue = queue;
            this.content = content;
        }

        public static MessageView fromMessage(Message message) {
            return new MessageView(message.getMessageProperties().getConsumerQueue(),
                    new String(message.getBody(), StandardCharsets.UTF_8));
        }
    }
}
