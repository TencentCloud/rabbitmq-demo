package com.tencent.cloud.tdmq.rabbitmq.demo.federation.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.ExchangeInfo;
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
     * Federation Exchange 演示程序的生产者程序。
     * 请先修改 {@link ConnectionProps.Federation} 中的配置项。
     */
    public static void main(String[] args) throws Exception {
        // 发送消息 ---> 上游交换机 --(Federation)-> 下游交换机 ---> 下游队列 ---> 接收消息
        // 联邦交换机会从上游交换机复制消息到下游

        // 需要使用 RabbitMQ HTTP API 的客户端，而非 AMQP 客户端
        Client apiDownstream = new Client(new ClientParameters()
                .url(DownstreamFromClient.API_SERVER_PREFIX)
                .username(DownstreamFromClient.USERNAME)
                .password(DownstreamFromClient.PASSWORD));
        Client apiUpstream = new Client(new ClientParameters()
                .url(UpstreamFromClient.API_SERVER_PREFIX)
                .username(UpstreamFromClient.USERNAME)
                .password(UpstreamFromClient.PASSWORD));

        // 要从上游的哪个交换机复制消息？
        String sourceExchangeOnUpstream = "demo.fed.ex.upstream.source";
        // 要将消息同步到下游的哪个交换机？
        String destinationExchangeOnDownstream = "demo.fed.ex.downstream.destination";
        // 下游服务器上，为该 Federation Upstream 配置项赋予的名称
        String upstreamNameOnDownstream = "demo-fed-ex-downstream-upstream";
        // 下游服务器上，为该 Federation Policy 配置项赋予的名称
        String policyNameOnDownstream = "demo-fed-ex-downstream-policy";

        // 声明一下交换机、队列、绑定；这里也可以使用 AMQP 客户端来完成
        apiUpstream.declareExchange(
                UpstreamFromClient.VHOST,
                sourceExchangeOnUpstream,
                new ExchangeInfo(BuiltinExchangeType.FANOUT.getType(), false, false)
        );
        apiUpstream.declareQueue(
                UpstreamFromClient.VHOST,
                sourceExchangeOnUpstream,
                new QueueInfo(false, false, false)
        );
        apiUpstream.bindQueue(
                UpstreamFromClient.VHOST,
                sourceExchangeOnUpstream,
                sourceExchangeOnUpstream,
                ""
        );
        apiDownstream.declareExchange(
                DownstreamFromClient.VHOST,
                destinationExchangeOnDownstream,
                new ExchangeInfo(BuiltinExchangeType.FANOUT.getType(), false, false)
        );
        apiDownstream.declareQueue(
                DownstreamFromClient.VHOST,
                destinationExchangeOnDownstream,
                new QueueInfo(false, false, false)
        );
        apiDownstream.bindQueue(
                DownstreamFromClient.VHOST,
                destinationExchangeOnDownstream,
                destinationExchangeOnDownstream,
                ""
        );

        // 根据之前的参数，建立 Upstream 和 Policy
        apiDownstream.declareUpstream(DownstreamFromClient.VHOST, upstreamNameOnDownstream,
                new UpstreamDetails()
                        .setUri(ConnectionProps.getConnectionString(UpstreamFromDownstream.HOST,
                                UpstreamFromDownstream.PORT, UpstreamFromDownstream.USERNAME,
                                UpstreamFromDownstream.PASSWORD, UpstreamFromDownstream.VHOST))
                        .setExchange(sourceExchangeOnUpstream)
                        .setExpiresMillis((long) 1 /* hour */ * 60 /* minutes */ * 60 /* seconds */ * 1000));
        apiDownstream.declarePolicy(DownstreamFromClient.VHOST, policyNameOnDownstream,
                new PolicyInfo(destinationExchangeOnDownstream, 1, "exchanges",
                        Collections.singletonMap("federation-upstream", upstreamNameOnDownstream)));

        System.out.println("配置完成");
    }
}
