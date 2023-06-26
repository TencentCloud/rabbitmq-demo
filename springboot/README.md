# TDMQ RabbitMQ Examples - Spring Boot

## 快速开始

### 购买 RabbitMQ

如果您已经拥有了 RabbitMQ 实例，则可以跳过本节。

如果您还没有可用的 RabbitMQ 实例，可以到[腾讯云](https://cloud.tencent.com/product/trabbit)上[购买消息队列 RabbitMQ 版](https://cloud.tencent.com/document/product/1495/81860)。

购买完成后，进入[控制台](https://console.cloud.tencent.com/tdmq/rabbit-cluster)，在对应地域下可以找到刚才购买的集群。
等待集群状态由 `创建中` 变为 `正常` 后，可以点击集群名称，进入集群的管理页面，在页面底部获取 RabbitMQ 实例的连接信息。

### 导入项目

若您使用的是 IntelliJ IDEA，则可以直接使用 `File` → `Open...` 打开项目根目录（而非当前模块的子目录），从而导入项目。

### 修改连接配置

运行示例程序之前，还需要将 `src/main/resources/application.yml` 中的常量修改为实际需要的连接信息。

`application.yml` 中的具体连接配置项有：
* `spring.rabbitmq.host` - 用于访问 RabbitMQ 的 IP 或域名，例如 `123.45.67.89`
* `spring.rabbitmq.port` - RabbitMQ 服务所在的端口，一般是 `5672`
* `spring.rabbitmq.username` - 连接到 RabbitMQ 的用户名
* `spring.rabbitmq.password` - 连接到 RabbitMQ 的密码

使用腾讯云提供的 RabbitMQ 时，可以通过以下方法寻找到配置项：
* `spring.rabbitmq.host`
    * 可以在 `集群管理` → `客户端接入` 中寻找 `公网域名接入` 类型所对应的 `网络` 连接信息，找到其中的 IP 地址并填入即可
    * 如果表格中不存在 `公网域名接入` 类型的连接信息，可以使用 `客户端接入` 右上方的 `添加路由策略` 来进行添加
* `spring.rabbitmq.port`
    * 使用腾讯云提供的 RabbitMQ 时，保持该配置为 `5672` 即可
* `spring.rabbitmq.username`
    * 可以在 `集群管理` → `Web 控制台访问地址` 中找到 `用户名`
* `spring.rabbitmq.password`
    * 可以在 `集群管理` → `Web 控制台访问地址` 中找到 `密码`，密码默认是隐藏不显示的，可以使用右侧的按钮来调整为显示，或者直接复制

如果需要额外修改更多配置，会在对应的章节中进行详细说明。

### 运行

若您使用的是 IntelliJ IDEA，则可以到 `src/main/java` 下找到对应的包，进入其中的类，使用行号处的 ▶️ 运行按钮开始运行。

后文将对各示例进行具体说明。

## 交换机

RabbitMQ 的消费者在发送消息时，需要指定交换机的名称与 routing key 来进行发送，而不是直接发送到某个队列。
交换机收到消息后，根据 routing key 按规则将消息分发到自身已经绑定了的部分或全部队列。

不同类型交换机的分发规则不同：
* Direct 交换机：只将消息发送到 binding key 相同的已绑定队列
* Fanout 交换机：将消息发送到所有已绑定的队列
* Topic 交换机：将消息发送到 binding key 相匹配的已绑定队列，binding key 中可以存在通配符 `*` 和 `#`

只需要将消息发送到已知名字的队列时，可以使用默认交换机（名称为空字符串），并将 routing key 设定为目标队列的名称。

修改连接配置后，执行 `com.tencent.cloud.tdmq.rabbitmq.demo.springboot.exchange.ExchangeDemoApplication`，然后在浏览器里打开 [http://localhost:8080](http://localhost:8080) 即可开始演示。

## 延时队列

延时队列是一种常见的消息队列应用场景，通常适用于需要在一定时间后才能处理的任务，例如：
* 订单超时取消：用户下单后一定时间内未支付时，可以将取消订单的任务添加到延时队列中，并在订单超时后自动执行取消操作
* 重试：需要发送消息到某个外部系统时，如果由于网络问题等原因发送失败，可以将消息添加到延时队列中，并在一定时间后重试发送

一般而言，存在两种实现方式：
* 使用死信队列实现
  * 设置了 TTL（最大存活时间）的死信队列能够将已经超出 TTL 的消息自动移出队列，并发送到另一个交换机，符合延时队列的行为，因此可以用来实现延时队列
  * TTL 可以在队列上统一设置，也可以在发送消息时为每一条消息设定其各自的 TTL
* 使用延时交换机实现
  * 死信队列实现的延时队列在消息的 TTL 不相同时会存在类似头部阻塞的问题，而延时交换机能够避免这个问题
  * 延时交换机不是 RabbitMQ 内置的，需要额外安装插件来进行支持

修改连接配置后，执行 `com.tencent.cloud.tdmq.rabbitmq.demo.springboot.delayed.DelayedDemoApplication`，然后在浏览器里打开 [http://localhost:8080](http://localhost:8080) 即可开始演示。

请注意如果 RabbitMQ 没有安装延时交换机插件，演示程序可能无法正常工作。

除了文首提及的配置项外，还存在一个 `demo.delayed.pending-queue-ttl-ms` 可以用来指定队列的统一延时毫秒数。

## 回复消息

Spring 允许在 `@RabbitListener` 处理完成后，发送一条消息到其它的队列，从而触发其它队列消费者的操作，进而推进业务逻辑流程。
只需在 `@RabbitListener` 方法上添加注解 `@SendTo` 指明目标队列，然后在方法内返回需要发送的信息对象即可。

修改连接配置后，执行 `com.tencent.cloud.tdmq.rabbitmq.demo.springboot.reply.ReplyingDemo` 即可开始演示。

## 发布确认

一般来说，存在三种消息传递质量保证：
* 最多一次：直接发送消息即可
* 至少一次：需要用到发布确认机制
* 恰好一次：在发布确认的基础上，在消费者端进行幂等判断

RabbitMQ 提供了消息的发布者确认机制来帮助实现至少一次语义。
在开启发布者确认的情况下，消息到达目标队列后，broker 会向发布者发送一个确认回执，这样发布者就能知道消息确实被发送出去了。

Spring Boot 将相关配置项暴露为 `spring.rabbitmq.publisher-confirm-type`，其值可以为：
* `none` - 不进行发布确认
* `simple` - 使用 `Channel#waitForConfirms` 进行发布确认
* `correlated` - 使用 CorrelationData 进行发布确认，这样可以得到一些额外信息

本例使用 `correlated`，并设置了消息确认及消息返回的回调函数。
演示的异常情况包括发送到不存在的队列、发送到不存在的交换机。

修改连接配置后，执行 `com.tencent.cloud.tdmq.rabbitmq.demo.springboot.pubconfirm.PublishConfirmDemo` 即可开始演示。

## 序列化 / 消息转换

直接使用 RabbitMQ 客户端时只能将对象手动转换为 `byte[]` 再发送。
使用 Spring 时，可以选择将要发送的对象交给 RabbitTemplate，让其帮助我们完成转换步骤。

RabbitTemplate 使用注册了的 MessageConverter 来完成消息的转换，本例使用了 Jackson2JsonMessageConverter，
该种消息转换器能够将对象转换为 JSON，或是将 JSON 转换为对象，这样一来实际发送的 `byte[]` 就是 UTF-8 编码了的 JSON 字符串。

修改连接配置后，执行 `com.tencent.cloud.tdmq.rabbitmq.demo.springboot.serialization.SerializationDemo` 即可开始演示。

## 事务

与数据库类似，RabbitMQ 提供了事务功能。开启事务后，只有在全部的操作成功后，才会把消息发送到队列中。如果出现任何一个操作失败，那么整个事务都会被回滚。
在使用 Spring 时，同样可以使用 `@Transactional` 注解来实现声明式事务。

修改连接配置后，执行 `com.tencent.cloud.tdmq.rabbitmq.demo.springboot.transaction.TransactionDemo` 即可开始演示。

## TLS (SSL) 加密连接

需要使用加密协议时，可以通过 TLS 来建立后端服务器与 RabbitMQ 的连接。

请注意**当前仅支持从 VPC 使用内网 IP 来进行 TLS 加密连接访问，而不支持公网 TLS**，请确认[目标 RabbitMQ 集群存在已开启 SSL / TLS 的路由策略](https://cloud.tencent.com/document/product/1495/88463)。

`src/main/resources` 下已放置了可用的 `rabbit-client.keycert.p12` 和 `rabbitStore`，且已在 `application-tls.yml` 中完成证书相关配置。

由于需要腾讯云内网环境，在运行演示程序时，请：
1. 修改 `application-tls.yml` 中的连接配置为 [VPC 网络](https://cloud.tencent.com/document/product/1495/88463)的连接配置信息
  * 请在 RabbitMQ 集群管理页面点击您的 RabbitMQ 集群，点击 `客户端接入` 中的 `添加路由策略`，添加一个勾选了 `开启 SSL/TLS` 的路由策略
2. 使用 `mvn package` 来编译程序，并打包成 JAR
  * 打包完成的 JAR 会位于 target 目录中
3. 将该 JAR [上传](https://cloud.tencent.com/document/product/213/39138)到该 VPC 下的任意[云服务器](https://cloud.tencent.com/product/cvm)上
  * 您需要在 RabbitMQ 所在地域下[购买](https://buy.cloud.tencent.com/cvm)一台 Linux 云服务器，在购买时选择 RabbitMQ TLS 接入点所在的 VPC
  * 如果您希望使用已经事先购买了的云服务器，可以前往[云服务器实例控制台](https://console.cloud.tencent.com/cvm)，点击表格右侧 `操作` 列中的 `更多` → `IP/网卡` → `绑定弹性网卡` 来[将云服务器绑定到 TLS 接入点所在的 VPC]((https://cloud.tencent.com/document/product/213/17400))
4. 使用 `ssh` 连接到服务器，在服务器上运行 JAR 以进行测试
  * 使用 `java -jar rabbitmq-demo-spring-boot-1.0-SNAPSHOT.jar` 完成演示
  * 请将 `rabbitmq-demo-spring-boot-1.0-SNAPSHOT.jar` 修改为实际的 JAR 文件名
