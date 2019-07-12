package com.xiekong.scheduler.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpClientConnectionManager;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 定期清理无效的http连接
 *
 * @author XieKong
 * @date 2019/7/9 17:35
 */
@Slf4j
public class IdleConnectionEvictor extends Thread {

    private final HttpClientConnectionManager manager;

    private Integer waitTime;

    private Integer idleConTime;

    private volatile boolean shutdown = true;

    public IdleConnectionEvictor(HttpClientConnectionManager manager, Integer waitTime, Integer idleConTime) {
        this.manager = manager;
        this.waitTime = waitTime;
        this.idleConTime = idleConTime;
        this.start();
    }

    @Override
    public void run() {
        try {
            if (shutdown) {
                synchronized (this) {
                    wait(waitTime);
                    manager.closeIdleConnections(idleConTime, TimeUnit.SECONDS);
                    // 关闭失效的连接
                    manager.closeExpiredConnections();
                }
            }
        } catch (Exception e) {
            log.error("shutdown ---> {}", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        shutdown = false;
        synchronized (this) {
            notifyAll();
        }
    }
}