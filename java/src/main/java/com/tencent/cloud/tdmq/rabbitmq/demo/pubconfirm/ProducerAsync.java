package com.tencent.cloud.tdmq.rabbitmq.demo.pubconfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;

public class ProducerAsync {

    /**
     * 用于存储已发送但未收到发送确认回执的信息。<br /><br />
     *
     * 这里使用 {@link ConcurrentNavigableMap} 是因为这里的 {@link Map}：
     * <ul>
     *     <li>必须能够安全地并发访问（{@link ConcurrentMap}）以被回调函数使用</li>
     *     <li>必须能够获取范围子集（{@link NavigableMap}）以支持累积的发送确认回执</li>
     * </ul>
     */
    private static final ConcurrentNavigableMap<Long, String> unconfirmedMessages = new ConcurrentSkipListMap<>();

    /**
     * 用于在结束程序前等待
     */
    private static final Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws Exception {
        // 建立工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConnectionProps.HOST);
        factory.setPort(ConnectionProps.PORT);
        factory.setUsername(ConnectionProps.USERNAME);
        factory.setPassword(ConnectionProps.PASSWORD);
        factory.setVirtualHost(ConnectionProps.VHOST);

        // 创建连接并创建 Channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // 启用发送确认功能
            channel.confirmSelect();

            // 设置收到发布确认时的回调函数
            channel.addConfirmListener(ProducerAsync::ackCallback, ProducerAsync::nackCallback);

            // 声明交换机、队列，以及其间的绑定
            channel.exchangeDeclare("demo.pubconfirm", BuiltinExchangeType.FANOUT);
            channel.queueDeclare("demo.pubconfirm", false, false, false, null);
            channel.queueBind("demo.pubconfirm", "demo.pubconfirm", "");

            int msgCount = 100;
            for (int i = 0; i < msgCount; i++) {
                String message = "第 " + i + " 条消息，发送于 " + LocalDateTime.now();

                // 获取之后发送时所使用的 seqNo，这个序列号是递增的
                long nextPublishSeqNo = channel.getNextPublishSeqNo();
                channel.basicPublish("demo.pubconfirm", "", null, message.getBytes());
                // 以 seqNo 为 key 把发送的消息存起来，因为之后的回调函数中只能拿到 seqNo
                unconfirmedMessages.put(nextPublishSeqNo, message);

                System.out.println("已经发送消息：" + message);
            }

            // 等待消息全部收到确认
            semaphore.acquire(msgCount);
            System.out.println("已全部获得确认回执");
        }
    }

    public static void ackCallback(long seqNo, boolean multiple) {
        if (!multiple) {  // 非累积确认，直接取出即可

            String message = unconfirmedMessages.remove(seqNo);
            System.out.println("得到 ACK 发送确认：" + message);
            semaphore.release(1);

        } else {  // multiple == true，累积确认

            // headMap 方法能够获取所有 key 小于等于（或者严格小于）某个特定值的键值对
            // 这里因为 seqNo 是递增的，所以可以利用 headMap 获取被累积确认的之前的所有消息
            Map<Long, String> confirmed = unconfirmedMessages.headMap(seqNo, true);
            int confirmedMessageCount = confirmed.size();
            System.out.println("得到 ACK 累积发送确认，共 " + confirmedMessageCount + " 条");
            confirmed.clear();  // headMap 返回的是视图而非拷贝，所以这里可以通过 clear 对原 map 进行修改
            semaphore.release(confirmedMessageCount);

        }
    }

    public static void nackCallback(long seqNo, boolean multiple) {
        if (!multiple) {
            String message = unconfirmedMessages.remove(seqNo);
            System.out.println("得到 NACK 发送确认：" + message);
            semaphore.release(1);
        } else {
            Map<Long, String> confirmed = unconfirmedMessages.headMap(seqNo, true);
            int confirmedMessageCount = confirmed.size();
            System.out.println("得到 NACK 累积发送确认，共 " + confirmedMessageCount + " 条");
            confirmed.clear();
            semaphore.release(confirmedMessageCount);
        }
    }
}
