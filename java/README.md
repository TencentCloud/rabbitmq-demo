# TDMQ RabbitMQ Examples - Java

## 快速开始

### 购买 RabbitMQ

如果您已经拥有了 RabbitMQ 实例，则可以跳过本节。

如果您还没有可用的 RabbitMQ 实例，可以到[腾讯云](https://cloud.tencent.com/product/trabbit)上[购买消息队列 RabbitMQ 版](https://cloud.tencent.com/document/product/1495/81860)。

购买完成后，进入[控制台](https://console.cloud.tencent.com/tdmq/rabbit-cluster)，在对应地域下可以找到刚才购买的集群。
等待集群状态由 `创建中` 变为 `正常` 后，可以点击集群名称，进入集群的管理页面，在页面底部获取 RabbitMQ 实例的连接信息。

### 导入项目

若您使用的是 IntelliJ IDEA，则可以直接使用 `File` → `Open...` 打开项目根目录（而非当前模块的子目录），从而导入项目。

### 修改连接配置

运行示例程序之前，还需要将 `com.tencent.cloud.tdmq.rabbitmq.demo.ConnectionProps` 中的常量修改为实际需要的连接信息。

`ConnectionProps` 中的具体配置项有：
* `ConnectionProps.HOST` - RabbitMQ 的 IP 或域名，例如 `123.45.67.89`
* `ConnectionProps.PORT` - RabbitMQ 服务所在的端口，一般是 `5672`
* `ConnectionProps.USERNAME` - 用户名
* `ConnectionProps.PASSWORD` - 密码
* `ConnectionProps.VHOST` - 虚拟主机，连接到默认虚拟主机可以使用 `/`

使用腾讯云提供的 RabbitMQ 时，可以通过以下方法寻找到配置项：
* `ConnectionProps.HOST`
  * 可以在 `集群管理` → `客户端接入` 中寻找 `公网域名接入` 类型所对应的 `网络` 连接信息，找到其中的 IP 地址并填入即可
  * 如果表格中不存在 `公网域名接入` 类型的连接信息，可以使用 `客户端接入` 右上方的 `添加路由策略` 来进行添加
* `ConnectionProps.PORT`
  * 保持该配置为 `5672` 即可
* `ConnectionProps.USERNAME`
  * 可以在 `集群管理` → `Web 控制台访问地址` 中找到 `用户名`
* `ConnectionProps.PASSWORD`
  * 可以在 `集群管理` → `Web 控制台访问地址` 中找到 `密码`，密码默认是隐藏不显示的，可以使用右侧的按钮来调整为显示，或者直接复制
* `ConnectionProps.VHOST`
  * 一般不需要修改此项

除了上述配置之外，还存在 `MQTT`、`STOMP`、`TLS`、`Federation`、`Shovel` 等配置项，但目前不需要修改这些配置。
需要额外修改更多配置时，会在对应的章节中进行详细说明。

### 运行

若您使用的是 IntelliJ IDEA，则可以到 `src/main/java` 下找到对应的包，进入其中的类，使用行号处的 ▶️ 运行按钮开始运行。

后文将对各示例进行具体说明。

## Hello World

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.helloworld` 包下，对基础的收发消息进行了演示。

在一般的消息队列使用场景中，我们通常需要用到三个关键组件：消息生产者、消息消费者和消息中间件（broker），例如 RabbitMQ。消息生产者负责向消息中间件发送消息，而消息消费者从消息中间件中获取消息，并对消息做出相应的后续操作。

通过消息中间件，生产者能够将消息暂存和分发到多个消费者之间，以避免直接连接生产者和消费者所带来的耦合性和不可靠性。同时，消息中间件还能够提供一些高级的消息路由和过滤功能，以便优化消息传递的效率和可靠性。

本例中的生产者实现为 `Producer`，消费者实现为 `Consumer`。`Producer` 生成若干条消息，然后发送到指定队列中；`Consumer` 则监听指定队列中，在队列中到达新消息时及时获取并消费。

按文档起始处的说明修改 `ConnectionProps` 中的连接配置后，可以以任意顺序直接执行此包下的 `Consumer` 和 `Producer` 来进行演示。

## 交换机

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.exchange` 包下，对内置类型的交换机进行了演示。

在 Hello World 示例中，我们使用默认交换机（空字符串）来将消息直接发送到指定队列，但实际场景中可能有更多样的需求，需要用到各种类型的交换机：
* Direct 交换机 - 将消息直接发送到与 binding key 完全匹配的队列中
  * 可以用于高效的简单消息传递，例如登录系统的验证消息
* Fanout 交换机 - 将消息广播到它所绑定到的**所有**队列中，而不考虑 binding key
  * 可以用于群发广播消息，例如系统通知，推广信息
* Topic 交换机 - 将消息根据 binding key 的匹配规则发送到一个或多个队列中
  * 可以用于复杂的消息路由，例如新闻推送，商品分类

对于 Direct、Fanout 类型的交换机，可以启动 `com.tencent.cloud.tdmq.rabbitmq.demo.exchange.Consumer`，然后使用同包下的 `ProducerDirect` 或 `ProducerFanout` 来发布消息。

对于 Topic 类型的交换机，可以使用 `com.tencent.cloud.tdmq.rabbitmq.demo.exchange.topic` 包下的 `Consumer` 与 `Producer` 进行演示。

## 死信队列

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.deadletter` 包下，对死信机制进行了演示。

假设有一个消息队列，其中包含了需要进行异步处理的任务，例如用户上传的图片需要进行图片转换和缩放等操作。如果任务执行失败或超时，就可能导致任务变成死信，这种消息可能会占用队列资源，并阻止其他需要处理的任务进展。

为了避免这种情况，可以使用 RabbitMQ 的死信队列功能，设定死信的认定条件（消息超时、堆积到指定条数、堆积到指定字节数等），并将达到这些条件的消息发送到一个专门的交换机。此后，可以编写一个消费者来监听死信队列，并对来自死信队列的消息执行一些自定义处理逻辑，例如将任务重新发送到队列中，或将其持久化到磁盘中以便后续处理。

演示的死信机制有根据条件死亡（在队列上设置）和手动杀死（消费者拒绝消息），消费者在消费时会打印消息的首次死亡原因。
`Consumer` 和 `Producer` 可以以任意顺序启动。

如果希望修改队列上的死亡条件，则需要同时修改 `Consumer` 和 `Producer` 中的参数，并进入 RabbitMQ 控制台中删除原有队列（如果已存在），然后再次执行演示程序。

## 延时队列

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.delayed` 包下，对延时队列进行了演示。

延时队列是一种常见的消息队列应用场景，通常适用于需要在一定时间后才能处理的任务，例如：
* 订单超时取消：用户下单后一定时间内未支付时，可以将取消订单的任务添加到延时队列中，并在订单超时后自动执行取消操作
* 重试：需要发送消息到某个外部系统时，如果由于网络问题等原因发送失败，可以将消息添加到延时队列中，并在一定时间后重试发送

具体演示有：
* `ProducerMessageLevel`：使用死信队列来实现延时，在发送消息时为每一条消息设定存活时间，这样一来指定时间后消息就会被自动移出原先队列而移入死信队列，死信队列中的消息就是已经延时的消息
* `ProducerQueueLevel`：使用死信队列来实现延时，与 `ProducerMessageLevel` 的唯一区别是这里在队列上设定了统一的延时时间
* `ProducerExchangeLevel`：使用死信交换机来实现延时，**RabbitMQ 必须已经安装了死信交换机插件**，可以为每一条消息设定不同的延时时间。

修改 `ConnectionProps` 连接配置后，可以以任意顺序执行消费者与发布者。

## 发布确认

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.pubconfirm` 包下，对发布确认机制进行了演示。

在生产者发布消息时，因为网络、硬件故障等原因，消息有可能传输失败，这时候就需要使用发布确认机制来保证消息的可靠性。发布确认机制可以在消息发送到队列之后，向生产者发送确认消息，表示消息已经被正确地接收和处理。

在消息生产者端，可以使用多种方法来处理发布确认回执。本例演示了常见的三种方法：
* `ProducerSync`：在每次发送消息后调用 `Channel#waitForConfirms` 来同步等待确认
* `ProducerSyncBulk`：在发送一系列消息后调用 `Channel#waitForConfirms` 来同步等待所有的确认
* `ProducerAsync`：使用 `Channel#addConfirmListener` 来注册发布者确认到达时的回调函数，由此完成异步处理

修改 `ConnectionProps` 连接配置后，可以以任意顺序执行消费者与发布者。

## 事务

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.transaction` 包下，对事务进行了演示。

与数据库类似，RabbitMQ 提供了事务功能。开启事务后，只有在全部的操作成功后，才会把消息发送到队列中。如果出现任何一个操作失败，那么整个事务都会被回滚。

修改 `ConnectionProps` 连接配置后，可以以任意顺序执行消费者与发布者。

默认情况下事务会成功提交，你可以取消注释 `Producer` 中的第 39 行来使得事务失败，从而调用 `Channel#txRollback` 方法来回滚事务。

## TLS (SSL) 加密连接

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.tls` 包下，对基础的收发消息进行了演示。

一些场景对数据传输的安全性存在要求，部分行业和法规也可能规定必须使用加密协议来保护敏感信息和数据，此时可以通过 TLS 来建立后端服务器与 RabbitMQ 的连接。

请注意**当前仅支持从 VPC 使用内网 IP 来进行 TLS 加密连接访问，而不支持公网 TLS**，请确认[目标 RabbitMQ 集群存在已开启 SSL / TLS 的路由策略](https://cloud.tencent.com/document/product/1495/88463)。

`src/main/resources` 下已放置了可用的 `rabbit-client.keycert.p12` 和 `rabbitStore`，且已在 `SSLConfig` 中完成证书相关配置，无需再修改。

由于需要腾讯云内网环境，在运行演示程序时，请：
1. 修改 `ConnectionProps.TLS` 中的连接配置为 [VPC 网络](https://cloud.tencent.com/document/product/1495/88463)的连接配置信息
    * 请在 RabbitMQ 集群管理页面点击您的 RabbitMQ 集群，点击 `客户端接入` 中的 `添加路由策略`，添加一个勾选了 `开启 SSL/TLS` 的路由策略
2. 使用 `mvn package` 来编译程序，并打包成 JAR
   * 打包完成的 JAR 会位于 target 目录中
3. 将该 JAR [上传](https://cloud.tencent.com/document/product/213/39138)到该 VPC 下的任意[云服务器](https://cloud.tencent.com/product/cvm)上
   * 您需要在 RabbitMQ 所在地域下[购买](https://buy.cloud.tencent.com/cvm)一台 Linux 云服务器，在购买时选择 RabbitMQ TLS 接入点所在的 VPC
   * 如果您希望使用已经事先购买了的云服务器，可以前往[云服务器实例控制台](https://console.cloud.tencent.com/cvm)，点击表格右侧 `操作` 列中的 `更多` → `IP/网卡` → `绑定弹性网卡` 来[将云服务器绑定到 TLS 接入点所在的 VPC]((https://cloud.tencent.com/document/product/213/17400))
4. 使用 `ssh` 连接到服务器，在服务器上运行 JAR 以进行测试
   * 使用 `java -cp rabbitmq-demo-java-1.0-SNAPSHOT-jar-with-dependencies.jar com.tencent.cloud.tdmq.rabbitmq.demo.tls.Producer` 发送消息
   * 使用 `java -cp rabbitmq-demo-java-1.0-SNAPSHOT-jar-with-dependencies.jar com.tencent.cloud.tdmq.rabbitmq.demo.tls.Consumer` 接收消息
   * 请将 `rabbitmq-demo-java-1.0-SNAPSHOT-jar-with-dependencies.jar` 修改为实际的 JAR 文件名

## MQTT

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.mqtt` 包下，使用 Eclipse Paho 客户端，演示了 MQTT 通信的内容。

MQTT 是一种不同于 AMQP 的轻量级的通信协议，适用于各种物联网设备之间的无线通信，广泛应用于物联网、智能家居、传感器网络、能源管理等众多领域。
如果您的业务对 MQTT 存在需求，可以参考本例中的相关代码。

该演示需要开启 RabbitMQ 的 [MQTT 插件](https://www.rabbitmq.com/mqtt.html)。

MQTT 使用独立的连接配置，具体配置项位于 `ConnectionProps.MQTT` 中。
可以到原生控制台的 Overview 页面底部的 Ports and Context 中确认端口号。

## STOMP over WebSocket

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.stomp` 包下，使用 Spring Web 与 Tyrus，演示了 STOMP over WebSocket 通信的内容。

STOMP over WebSocket 是在 WebSocket 标准上添加的一层协议，其使用 STOMP 作为应用程序层协议，通过 WebSocket 连接进行交互，常用于实时 Web 应用程序和推送通知。

该演示使用了 STOMP over WebSocket，需要开启 [Web STOMP 插件](https://www.rabbitmq.com/web-stomp.html)（而非 [STOMP 插件](https://www.rabbitmq.com/stomp.html)）。

STOMP 使用独立的连接配置，具体配置项位于 `ConnectionProps.STOMP` 中。
可以到原生控制台的 Overview 页面底部的 Ports and Context 中确认端口号。

## Federation

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.federation` 包下，对联邦队列与联邦交换机的建立与使用效果进行了演示。

Federation 适合用于在不同的 RabbitMQ 服务器之间传输消息。
例如，在一个企业内部的不同数据中心之间建立 RabbitMQ 消息队列的复杂环境下，可以使用 Federation 在不同的队列和 Exchange 之间传输消息。此外，Federation 还可以用于在多个分区之间转移数据，如将分析结果转移到实时处理引擎中，以便更快地做出响应。

需要为 RabbitMQ 开启 [Federation 插件](https://www.rabbitmq.com/federation.html)。

Federation 涉及两个不同的 RabbitMQ，消息从上游复制或移动到下游；需要修改 `ConnectionProps.Federation` 中的连接配置：
* 需要能够从本机客户端连接到两个远程服务器，对应的连接配置类为 `UpstreamFromClient` 和 `DownstreamFromClient`
* 如果希望下游 RabbitMQ 使用内网（或其它任何不同于 `UpstreamFromClient` 的连接配置）来连接到上游，则可以单独配置 `UpstreamFromDownstream`

AMQP 客户端不能独立完成该项配置，所以还需提供 `API_SERVER_PREFIX`，具体内容为 `http://原生控制台的IP和端口/api/`。
本演示中使用 API 来快速完成配置，实际应用时，除 API 外，也可启用 `rabbitmq_federation_management` 插件来从原生控制台管理。

请先执行 `Configurer` 完成 Federation 的配置工作，然后再执行同一包下的 `Consumer` 与 `Producer`。

## Shovel

位于 `com.tencent.cloud.tdmq.rabbitmq.demo.shovel` 包下，对 Shovel 的配置与使用效果进行了演示。

Shovel 适合用于复制数据到另一个 RabbitMQ 集群中的特定 Exchange 或 Queue。
例如，如果在一个数据中心内部有多个 RabbitMQ 集群的数据需要进行复制，我们可以使用 Shovel 将每个集群中的数据通过指定的 Exchange 复制到另一个 RabbitMQ 集群，达到数据备份和同步的目的。此外，Shovel 还可以用于控制流量，例如提高容错性或缓解流量压力。

需要为 RabbitMQ 开启 [Shovel 插件](https://www.rabbitmq.com/shovel.html)。

Shovel 将消息从上游复制或移动到下游；需要修改 `ConnectionProps.Shovel` 中的连接配置：
* 消息如何从本机客户端连接到两个远程服务器：对应的连接配置类为 `UpstreamFromClient` 和 `DownstreamFromClient`
* 消息如何从 Shovel 所在服务器连接到两个远程服务器：对应的连接配置类为 `UpstreamFromShovel` 和 `DownstreamFromShovel`
  * 如果没有特殊连接需求，可以使用与 `UpstreamFromClient` 和 `DownstreamFromClient` 相同的配置

AMQP 客户端不能独立完成该项配置，所以还需提供 `API_SERVER_PREFIX`，具体内容为 `http://原生控制台的IP和端口/api/`。
本演示中使用 API 来快速完成配置，实际应用时，除 API 外，也可启用 `rabbitmq_shovel_management` 插件来从原生控制台管理。

请先执行 `Configurer` 完成 Federation 的配置工作，然后再执行同一包下的 `Consumer` 与 `Producer`。
