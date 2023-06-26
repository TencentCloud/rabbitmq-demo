package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.delayed;

import com.tencent.cloud.tdmq.rabbitmq.demo.springboot.Utilities.MessageView;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("delayedDemoConsumer")
public class ConsumerController {

    @Autowired
    DelayedDemoListener delayedDemoListener;

    @GetMapping("/delayed/view")
    public String viewDelayed(Model model) {
        List<MessageView> messages = delayedDemoListener.received.stream()
                .map(MessageView::fromMessage).collect(Collectors.toList());  // 转换数据格式以供模版使用
        model.addAttribute("messages", messages);
        return "view-delayed";
    }
}
