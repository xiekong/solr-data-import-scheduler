package com.xiekong.scheduler.solr;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author XieKong
 * @date 2019/7/11 11:02
 */
@Data
@Accessors(chain = true)
public class SolrParam {
    private String name;
    private String command;
    private Boolean verbose;
    private Boolean clean;
    private Boolean commit;
}
