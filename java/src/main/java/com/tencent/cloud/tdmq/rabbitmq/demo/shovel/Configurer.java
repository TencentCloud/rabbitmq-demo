package com.tencent.cloud.tdmq.rabbitmq.demo.shovel;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.QueueInfo;
import com.rabbitmq.http.client.domain.ShovelDetails;
import com.rabbitmq.http.client.domain.ShovelInfo;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Shovel.DownstreamFromClient;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Shovel.DownstreamFromShovel;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Shovel.UpstreamFromClient;
import com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps.Shovel.UpstreamFromShovel;
import java.util.Collections;

public class Configurer {

    /**
     * Shovel 演示程序的配置程序。
     * 请先修改 {@link ConnectionProps.Shovel} 中的配置项。
     */
    public static void main(String[] args) throws Exception {
        // 需要使用 RabbitMQ HTTP API 的客户端，而非 AMQP 客户端
        Client apiDownstream = new Client(new ClientParameters()
                .url(DownstreamFromClient.API_SERVER_PREFIX)
                .username(DownstreamFromClient.USERNAME)
                .password(DownstreamFromClient.PASSWORD));
        Client apiUpstream = new Client(new ClientParameters()
                .url(UpstreamFromClient.API_SERVER_PREFIX)
                .username(UpstreamFromClient.USERNAME)
                .password(UpstreamFromClient.PASSWORD));

        // 要从上游的哪个队列消费消息？
        String sourceQueueOnUpstream = "demo.shovel.upstream.source";
        // 要将消息转发到下游的哪个队列？
        String destinationQueueOnDownstream = "demo.shovel.downstream.destination";
        // 为该 shovel 配置项赋予的名称
        String policyNameOnDownstream = "demo-shovel-downstream";

        String upstreamConnectionString = ConnectionProps.getConnectionString(
                UpstreamFromShovel.HOST, UpstreamFromShovel.PORT,
                UpstreamFromShovel.USERNAME, UpstreamFromShovel.PASSWORD,
                UpstreamFromShovel.VHOST
        );
        String downstreamConnectionString = ConnectionProps.getConnectionString(
                DownstreamFromShovel.HOST, DownstreamFromShovel.PORT,
                DownstreamFromShovel.USERNAME, DownstreamFromShovel.PASSWORD,
                DownstreamFromShovel.VHOST
        );

        // 声明一下队列；这里也可以使用 AMQP 客户端来完成
        apiUpstream.declareQueue(UpstreamFromClient.VHOST, sourceQueueOnUpstream,
                new QueueInfo(false, false, false));
        apiDownstream.declareQueue(DownstreamFromClient.VHOST, destinationQueueOnDownstream,
                new QueueInfo(false, false, false));

        // 根据之前的参数，建立 Shovel
        ShovelDetails shovelDetails = new ShovelDetails();
        shovelDetails.setSourceURIs(Collections.singletonList(upstreamConnectionString));
        shovelDetails.setSourceQueue(sourceQueueOnUpstream);
        shovelDetails.setDestinationURIs(Collections.singletonList(downstreamConnectionString));
        shovelDetails.setDestinationQueue(destinationQueueOnDownstream);
        apiDownstream.declareShovel(DownstreamFromClient.VHOST, new ShovelInfo(policyNameOnDownstream, shovelDetails));

        System.out.println("配置完成");
    }
}
