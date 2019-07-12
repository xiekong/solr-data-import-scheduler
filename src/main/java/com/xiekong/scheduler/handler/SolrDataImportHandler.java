package com.xiekong.scheduler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiekong.scheduler.http.HttpClientHelper;
import com.xiekong.scheduler.solr.SolrCore;
import com.xiekong.scheduler.solr.SolrParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author XieKong
 * @date 2019/7/9 18:02
 */
@Slf4j
public class SolrDataImportHandler implements Runnable {
    private HttpClientHelper httpClientHelper;
    private SolrCore solrCore;

    public SolrDataImportHandler(HttpClientHelper httpClientHelper, SolrCore solrCore) {
        this.httpClientHelper = httpClientHelper;
        this.solrCore = solrCore;
    }

    @Override
    public void run() {
        log.info("data import ---> core: {} date: {}", solrCore.getCoreName(), OffsetDateTime.now().toLocalDateTime());
        SolrParam solrParam = Optional.ofNullable(solrCore.getImportParam())
                .orElseThrow(() -> new IllegalArgumentException("solr data import params is null"));

        String sendUrl = "http://";

        if (StringUtils.isNotBlank(solrCore.getUsername())
                && StringUtils.isNotBlank(solrCore.getPassword())) {
            sendUrl += solrCore.getUsername() + ":" +solrCore.getPassword() + "@";
        }

        sendUrl += solrCore.getServerIp()
                + ":" + solrCore.getServerPort()
                + "/solr/"
                + solrCore.getCoreName()
                + "/"
                + solrParam.getName();
        log.info("data import ---> url: {} date: {}", sendUrl, OffsetDateTime.now().toLocalDateTime());

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> paramMap = objectMapper.convertValue(solrParam, HashMap.class);
        String result = httpClientHelper.post(sendUrl, paramMap, null);
        log.info("data import result --> {}", result);
    }
}
