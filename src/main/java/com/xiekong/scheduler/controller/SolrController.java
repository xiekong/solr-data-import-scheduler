package com.xiekong.scheduler.controller;

import com.xiekong.scheduler.solr.SolrConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieKong
 * @date 2019/7/9 15:28
 */
@RestController
@RequestMapping("/solr")
public class SolrController {
    @Autowired
    SolrConfig solrConfig;

    @GetMapping("/core/list")
    public Object list() {
        return solrConfig.getCores();
    }
}
