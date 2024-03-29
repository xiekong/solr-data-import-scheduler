package com.xiekong.scheduler.http;

import lombok.Data;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author XieKong
 * @date 2019/7/9 16:29
 */
@Data
@Component
@ConditionalOnExpression("!'${http}'.isEmpty()")
@ConfigurationProperties(prefix = "http")
public class HttpClientConfig {
    private Integer maxTotal;
    private Integer defaultMaxPerRoute;
    private Integer connectTimeout;
    private Integer connectionRequestTimeout;
    private Integer socketTimeout;
    private Integer validateAfterInactivity;
    private Integer waitTime;
    private Integer idleConTime;

    /**
     * 首先实例化一个连接池管理器，设置最大连接数、并发连接数
     *
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        //最大连接数
        httpClientConnectionManager.setMaxTotal(maxTotal);
        //并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        httpClientConnectionManager.setValidateAfterInactivity(validateAfterInactivity);
        return httpClientConnectionManager;
    }

    /**
     * 实例化连接池，设置连接池管理器。
     * 这里需要以参数形式注入上面实例化的连接池管理器
     *
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder httpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {
        //HttpClientBuilder中的构造方法被protected修饰，
        // 所以这里不能直接使用new来实例化一个HttpClientBuilder，
        // 可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        return httpClientBuilder;
    }

    /**
     * 注入连接池，用于获取httpClient
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient closeableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    /**
     * Builder是RequestConfig的一个内部类
     * 通过RequestConfig的custom方法来获取到一个Builder对象
     * 设置builder的连接信息
     * 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     *
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder
                // 连接超时时间
                .setConnectTimeout(connectTimeout)
                // 从连接池中取连接的超时时间
                .setConnectionRequestTimeout(connectionRequestTimeout)
                // 请求超时时间
                .setSocketTimeout(socketTimeout);
    }

    /**
     * 使用builder构建一个RequestConfig对象
     *
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig requestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }

    @Bean
    public IdleConnectionEvictor idleConnectionEvictor(PoolingHttpClientConnectionManager poolManager) {
        return new IdleConnectionEvictor(poolManager, waitTime, idleConTime);
    }
}
