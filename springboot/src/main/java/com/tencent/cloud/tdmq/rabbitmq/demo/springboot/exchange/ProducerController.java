package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("exchange_producerController")
public class ProducerController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ExchangeDemoProperties props;

    private List<String> sendToExchange(String exchangeName, Iterable<String> routingKeys) {
        List<String> output = new ArrayList<>();
        for (String routingKey : routingKeys) {
            String message = "Sent to [" + exchangeName + "] with routing key [" + routingKey + "].";
            rabbitTemplate.send(exchangeName, routingKey, new Message(message.getBytes(StandardCharsets.UTF_8)));
            output.add(message);
        }
        return output;
    }

    /**
     * 发送消息到 direct 交换机
     */
    @PostMapping("/exchange/direct/send")
    @ResponseBody
    public List<String> sendDirect() {
        return sendToExchange(props.getDirect().getExchange(), props.getDirect().getBindings().keySet());
    }

    /**
     * 发送消息到 fanout 交换机
     */
    @PostMapping("/exchange/fanout/send")
    @ResponseBody
    public List<String> sendFanout() {
        return sendToExchange(props.getFanout().getExchange(), Collections.singletonList(""));
    }

    /**
     * 发送消息到 topic 交换机
     */
    @PostMapping("/exchange/topic/send")
    @ResponseBody
    public List<String> sendTopic() {
        return sendToExchange(props.getTopic().getExchange(), props.getTopic().getRoutingKeyToSend());
    }
}
