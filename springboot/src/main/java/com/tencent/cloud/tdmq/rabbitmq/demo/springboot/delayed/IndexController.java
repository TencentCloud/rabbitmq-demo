package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.delayed;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("delayedIndexController")
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index-delayed";
    }
}
