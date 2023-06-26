package com.tencent.cloud.tdmq.rabbitmq.demo;

/**
 * 存储用于连接 RabbitMQ 的相关信息，在运行示例程序之前请先修改这里的常量。
 */
public final class ConnectionProps {

    public static final String HOST = "123.45.67.89";  // RabbitMQ 消息队列的 IP 地址，从本地访问需要填入公网接入点的 IP
    public static final int PORT = 5672;  // 端口号
    public static final String USERNAME = "admin";  // 用户名
    public static final String PASSWORD = "password";  // 密码
    public static final String VHOST = "/";  // Vhost，一般不用改

    /**
     * 仅在 {@link com.tencent.cloud.tdmq.rabbitmq.demo.mqtt} 下的示例中使用。
     */
    public static final class MQTT {

        public static final String SERVER_URI = "tcp://123.45.67.89:1883";  // tcp://消息队列IP地址:端口号  端口号可以在原生控制台中查询到，一般是 1883
        public static final String USERNAME = "admin";  // 用户名，与一般的 RabbitMQ 连接相同
        public static final String PASSWORD = "password";  // 密码，与一般的 RabbitMQ 连接相同
    }

    /**
     * 仅在 {@link com.tencent.cloud.tdmq.rabbitmq.demo.stomp} 下的示例中使用。
     */
    public static final class STOMP {

        public static final String WS_ENDPOINT = "ws://123.45.67.89:15674/ws";  // ws://消息队列IP地址:端口号/ws  端口号可以在原生控制台中查询到，一般是 15674
        public static final String USERNAME = "admin";  // 用户名，与一般的 RabbitMQ 连接相同
        public static final String PASSWORD = "password";  // 密码，与一般的 RabbitMQ 连接相同
    }

    /**
     * 仅在 {@link com.tencent.cloud.tdmq.rabbitmq.demo.tls} 下的示例中使用。
     * 请务必查看 README.md
     */
    public static final class TLS {

        public static final String HOST = "10.0.0.123";  // 请确认这里是 VPC 的内网 IP，且该 VPC 接入点开启了 TLS
        public static final int PORT = 5671;  // 端口号，注意不是明文连接使用的 5672
        public static final String USERNAME = "admin";  // 用户名
        public static final String PASSWORD = "password";  // 密码
        public static final String VHOST = "/";  // Vhost
    }

    /**
     * 仅在 {@link com.tencent.cloud.tdmq.rabbitmq.demo.federation} 下的示例中使用。
     */
    public static final class Federation {

        /**
         * 用于从本地客户端连接到上游服务器
         */
        public static final class UpstreamFromClient {

            public static final String API_SERVER_PREFIX = "http://123.45.67.89:15672/api/";  // 原生控制台的公网访问地址，后面加上 /api/
            public static final String HOST = "123.45.67.89";  // 消息队列的 IP 地址
            public static final int PORT = 5672;  // 消息队列的端口
            public static final String USERNAME = "admin";  // 用户名
            public static final String PASSWORD = "password";  // 密码
            public static final String VHOST = "/";  // Vhost
        }

        /**
         * 用于从下游服务器连接到上游服务器
         */
        public static final class UpstreamFromDownstream {

            public static final String HOST = UpstreamFromClient.HOST;  // 消息队列的 IP 地址
            public static final int PORT = UpstreamFromClient.PORT;  // 消息队列的端口
            public static final String USERNAME = UpstreamFromClient.USERNAME;  // 用户名
            public static final String PASSWORD = UpstreamFromClient.PASSWORD;  // 密码
            public static final String VHOST = UpstreamFromClient.VHOST;  // Vhost
        }

        /**
         * 用于从本地客户端连接到下游服务器
         */
        public static final class DownstreamFromClient {

            public static final String API_SERVER_PREFIX = "http://127.0.0.1:15672/api/";  // 消息队列的 IP 地址
            public static final String HOST = "127.0.0.1";  // 消息队列的端口
            public static final int PORT = 5672;  // 消息队列的端口
            public static final String USERNAME = "user";  // 用户名
            public static final String PASSWORD = "bitnami";  // 密码
            public static final String VHOST = "/";  // Vhost
        }
    }

    /**
     * 仅在 {@link com.tencent.cloud.tdmq.rabbitmq.demo.shovel} 下的示例中使用。
     */
    public static final class Shovel {

        /**
         * 用于从本地客户端连接到上游服务器
         */
        public static final class UpstreamFromClient {

            public static final String API_SERVER_PREFIX = "http://123.45.67.89:15672/api/";  // 消息队列的 IP 地址
            public static final String HOST = "123.45.67.89";  // 消息队列的端口
            public static final int PORT = 5672;  // 消息队列的端口
            public static final String USERNAME = "admin";  // 用户名
            public static final String PASSWORD = "password";  // 密码
            public static final String VHOST = "/";  // Vhost
        }

        /**
         * 用于从 Shovel 所在服务器连接到上游服务器
         */
        public static final class UpstreamFromShovel {

            public static final String HOST = "123.45.67.89";  // 消息队列的 IP 地址
            public static final int PORT = 5672;  // 消息队列的端口
            public static final String USERNAME = UpstreamFromClient.USERNAME;  // 用户名
            public static final String PASSWORD = UpstreamFromClient.PASSWORD;  // 密码
            public static final String VHOST = UpstreamFromClient.VHOST;  // Vhost
        }

        /**
         * 用于从本地客户端连接到下游服务器
         */
        public static final class DownstreamFromClient {

            public static final String API_SERVER_PREFIX = "http://127.0.0.1:15672/api/";  // 消息队列的 IP 地址
            public static final String HOST = "127.0.0.1";  // 消息队列的端口
            public static final int PORT = 5672;  // 消息队列的端口
            public static final String USERNAME = "user";  // 用户名
            public static final String PASSWORD = "bitnami";  // 密码
            public static final String VHOST = "/";  // Vhost
        }

        /**
         * 用于从 Shovel 所在服务器连接到下游服务器
         */
        public static final class DownstreamFromShovel {

            public static final String HOST = "127.0.0.1";  // 消息队列的 IP 地址
            public static final int PORT = 5672;  // 消息队列的端口
            public static final String USERNAME = DownstreamFromClient.USERNAME;  // 用户名
            public static final String PASSWORD = DownstreamFromClient.PASSWORD;  // 密码
            public static final String VHOST = DownstreamFromClient.VHOST;  // Vhost
        }
    }

    public static String getConnectionString(String host, int port, String username, String password, String vhost) {
        if (vhost == null || vhost.length() == 0 || vhost.equals("/")) {
            return String.format("amqp://%s:%s@%s:%d", username, password, host, port);
        } else {
            return String.format("amqp://%s:%s@%s:%d/%s", username, password, host, port, vhost);
        }
    }
}
