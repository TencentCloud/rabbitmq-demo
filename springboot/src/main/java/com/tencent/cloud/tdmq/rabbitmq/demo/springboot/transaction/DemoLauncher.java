package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DemoLauncher {

    @Autowired
    DemoService demoService;

    public void runDemo() {
        demoService.clearQueue();

        log.info("调用事务，期望能收到消息");
        demoService.declarativeTransaction();
        demoService.clearQueue();

        try {
            log.info("调用事务，期望收不到消息");
            demoService.declarativeTransactionException();
        } catch (Exception ex) {
            log.info("捕捉到用于回滚的异常");
            log.debug("捕捉到用于回滚的异常", ex);
        } finally {
            demoService.clearQueue();
        }
    }
}
