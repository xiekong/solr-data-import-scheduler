package com.xiekong.scheduler.solr;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author XieKong
 * @date 2019/7/9 15:24
 */
@Data
@Component
@ConditionalOnExpression("!'${solr}'.isEmpty()")
@ConfigurationProperties(prefix = "solr")
public class SolrConfig {
    private List<SolrCore> cores;
}
