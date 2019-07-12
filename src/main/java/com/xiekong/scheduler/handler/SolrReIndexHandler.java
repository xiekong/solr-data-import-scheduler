package com.xiekong.scheduler.handler;

import com.xiekong.scheduler.http.HttpClientHelper;
import com.xiekong.scheduler.solr.SolrCore;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

/**
 * @author XieKong
 * @date 2019/7/9 18:02
 */
@Slf4j
public class SolrReIndexHandler implements Runnable {
    private HttpClientHelper httpClientHelper;
    private SolrCore solrCore;

    public SolrReIndexHandler(HttpClientHelper httpClientHelper, SolrCore solrCore) {
        this.httpClientHelper = httpClientHelper;
        this.solrCore = solrCore;
    }

    @Override
    public void run() {
        log.info(solrCore.getCoreName() + " -------> " + Thread.currentThread().getName() + " " + OffsetDateTime.now().toLocalTime());
    }
}
