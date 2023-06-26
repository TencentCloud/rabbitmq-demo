package com.tencent.cloud.tdmq.rabbitmq.demo.stomp;

import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.STOMP;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class Consumer {

    /**
     * STOMP over WebSocket 演示程序的消费者程序。
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

        // 订阅队列；更多类型参见 https://www.rabbitmq.com/stomp.html#d
        Subscription subscription = stompSession.subscribe("/queue/demo-stomp-ws", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // 这里返回的类型会作为类型转换的目标类型，转换后传入后面的 handleFrame 方法中
                // DefaultStompSession 默认使用 SimpleMessageConverter，该转换器只会直接赋值，所以这里使用 byte[]
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("收到消息：" + new String((byte[]) payload, StandardCharsets.UTF_8));
            }
        });

        System.out.println("已开始接收消息，请执行 " + Producer.class.getName());
        System.in.read();
        subscription.unsubscribe();
    }
}
