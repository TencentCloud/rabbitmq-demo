package com.tencent.cloud.tdmq.rabbitmq.demo.tls;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLConfig {

    static SSLContext sslContext() throws GeneralSecurityException, IOException {
        // 加载 KeyManager 的 KeyStore，可以直接使用本示例自带的或官方文档下载来的证书
        // 这里是从 resource 中加载，所以使用了 getResourceAsStream；如果证书放置在外部，则可以修改为通过 FileInputStream 加载，下同
        KeyStore ks = KeyStore.getInstance("PKCS12");
        char[] keyPassword = "rabbitmq".toCharArray();
        try (InputStream stream = SSLConfig.class.getResourceAsStream("/rabbit-client.keycert.p12")) {
            ks.load(stream, keyPassword);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keyPassword);

        // 加载 TrustManager 的 KeyStore
        KeyStore tks = KeyStore.getInstance("JKS");
        char[] trustPassword = "rabbitmq".toCharArray();
        try (InputStream stream = SSLConfig.class.getResourceAsStream("/rabbitStore")) {
            tks.load(stream, trustPassword);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        // 使用之前的 KeyManager 和 TrustManager 初始化 SSLContext
        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return c;
    }
}
