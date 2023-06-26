package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("demo.exchange")
@Data
public class ExchangeDemoProperties {

    private Direct direct;

    @Data
    public static class Direct {

        private String exchange;
        private Map<String, String> bindings;
    }

    private Fanout fanout;

    @Data
    public static class Fanout {

        private String exchange;
        private List<String> queues;
    }

    private Topic topic;

    @Data
    public static class Topic {

        private String exchange;
        private String pattern;
        private List<String> routingKeyToSend;
        private String queue;
    }
}
