package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange;

import com.tencent.cloud.tdmq.rabbitmq.demo.springboot.Utilities.MessageView;
import com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange.DemoListener.CustomListenerContainer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("exchange_consumerController")
public class ConsumerController {

    @Autowired
    ExchangeDemoProperties props;

    @Autowired
    DemoListener listener;

    @Autowired
    CustomListenerContainer batchListener;

    @GetMapping("/exchange/direct/view")
    public String viewDirect(Model model) {
        List<MessageView> messages = batchListener.received.stream()
                .filter(message -> Objects.equals(
                        message.getMessageProperties().getReceivedExchange(),
                        props.getDirect().getExchange()
                ))
                .map(MessageView::fromMessage)  // 转换数据格式以供显示
                .collect(Collectors.toList());
        model.addAttribute("exchangeType", "Direct");
        model.addAttribute("sendUrl", "/exchange/direct/send");
        model.addAttribute("messages", messages);
        return "view";
    }

    @GetMapping("/exchange/fanout/view")
    public String viewFanout(Model model) {
        List<MessageView> messages = batchListener.received.stream()
                .filter(message -> Objects.equals(
                        message.getMessageProperties().getReceivedExchange(),
                        props.getFanout().getExchange()
                ))
                .map(MessageView::fromMessage)  // 转换数据格式以供显示
                .collect(Collectors.toList());
        model.addAttribute("exchangeType", "Fanout");
        model.addAttribute("sendUrl", "/exchange/fanout/send");
        model.addAttribute("messages", messages);
        return "view";
    }

    @GetMapping("/exchange/topic/view")
    public String viewTopic(Model model) {
        List<MessageView> messages = listener.topicReceived.stream()
                .map(MessageView::fromMessage).collect(Collectors.toList());  // 转换数据格式以供显示
        model.addAttribute("messages", messages);
        model.addAttribute("bindingKey", props.getTopic().getPattern());
        return "view-topic";
    }
}
