package com.tencent.cloud.tdmq.rabbitmq.demo.stomp;

import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.STOMP;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class Producer {

    /**
     * STOMP over WebSocket 演示程序的生产者程序。
     * 请先修改 {@link ConnectionProps.STOMP} 中的配置项。
     */
    public static void main(String[] args) throws Exception {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        // 传入用户名和密码
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.set(StompHeaders.LOGIN, STOMP.USERNAME);
        connectHeaders.set(StompHeaders.PASSCODE, STOMP.PASSWORD);

        System.out.println("尝试连接服务器...");
        StompSession stompSession = stompClient.connect(URI.create(STOMP.WS_ENDPOINT), null, connectHeaders,
                new StompSessionHandlerAdapter() {
                }).get();

        // 发送消息
        for (int i = 0; i < 100; i++) {
            String message = "这是第 " + i + " 条消息";
            stompSession.send("/queue/demo-stomp-ws", message.getBytes(StandardCharsets.UTF_8));
            System.out.println(message);
            Thread.sleep(1000);
        }
    }
}
