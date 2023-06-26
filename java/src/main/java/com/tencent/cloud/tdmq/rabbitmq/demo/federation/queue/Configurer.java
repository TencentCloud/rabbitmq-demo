package com.tencent.cloud.tdmq.rabbitmq.demo.federation.queue;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.PolicyInfo;
import com.rabbitmq.http.client.domain.QueueInfo;
import com.rabbitmq.http.client.domain.UpstreamDetails;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Federation.DownstreamFromClient;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Federation.UpstreamFromClient;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Federation.UpstreamFromDownstream;
import java.util.Collections;

public class Configurer {

    /**
     * Federation Queue 演示程序的生产者程序。
     * 请先修改 {@link ConnectionProps.Federation} 中的配置项。
     */
    public static void main(String[] args) throws Exception {
        // 发送消息 ---> 上游交换机 ----> 上游队列 --(Federation)-> 下游队列 ---> 接收消息
        // 联邦队列的下游队列在缺少消息时，会从上游队列中拉取消息，以供下游消费者消费；消息从上游转移（而非复制）到下游

        // 需要使用 RabbitMQ HTTP API 的客户端，而非 AMQP 客户端
        Client apiDownstream = new Client(new ClientParameters()
                .url(DownstreamFromClient.API_SERVER_PREFIX)
                .username(DownstreamFromClient.USERNAME)
                .password(DownstreamFromClient.PASSWORD));
        Client apiUpstream = new Client(new ClientParameters()
                .url(UpstreamFromClient.API_SERVER_PREFIX)
                .username(UpstreamFromClient.USERNAME)
                .password(UpstreamFromClient.PASSWORD));

        // 要从上游的哪个队列拉取消息？
        String sourceQueueOnUpstream = "demo.fed.queue.upstream.source";
        // 要将消息拉取到下游的哪个队列？
        String destinationQueueOnDownstream = "demo.fed.queue.downstream.destination";
        // 下游服务器上，为该 Federation Upstream 配置项赋予的名称
        String upstreamNameOnDownstream = "demo-fed-queue-downstream-upstream";
        // 下游服务器上，为该 Federation Policy 配置项赋予的名称
        String policyNameOnDownstream = "demo-fed-queue-downstream-policy";

        // 声明一下队列；这里也可以使用 AMQP 客户端来完成
        apiUpstream.declareQueue(
                UpstreamFromClient.VHOST,
                sourceQueueOnUpstream,
                new QueueInfo(false, false, false)
        );
        apiDownstream.declareQueue(
                DownstreamFromClient.VHOST,
                destinationQueueOnDownstream,
                new QueueInfo(false, false, false)
        );

        // 根据之前的参数，建立 Upstream 和 Policy
        apiDownstream.declareUpstream(DownstreamFromClient.VHOST, upstreamNameOnDownstream,
                new UpstreamDetails()
                        .setUri(ConnectionProps.getConnectionString(UpstreamFromDownstream.HOST,
                                UpstreamFromDownstream.PORT, UpstreamFromDownstream.USERNAME,
                                UpstreamFromDownstream.PASSWORD, UpstreamFromDownstream.VHOST))
                        .setQueue(sourceQueueOnUpstream));
        apiDownstream.declarePolicy(DownstreamFromClient.VHOST, policyNameOnDownstream,
                new PolicyInfo(destinationQueueOnDownstream, 1, "queues",
                        Collections.singletonMap("federation-upstream", upstreamNameOnDownstream)));

        System.out.println("配置完成");
    }
}
