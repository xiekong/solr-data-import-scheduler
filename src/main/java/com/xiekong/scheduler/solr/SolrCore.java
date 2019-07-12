package com.xiekong.scheduler.solr;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author XieKong
 * @date 2019/7/9 18:39
 */
@Data
@Accessors(chain = true)
public class SolrCore {
    private String serverIp = "localhost";
    private Integer serverPort = 8983;
    private String username;
    private String password;
    private String coreName;
    // 导入间隔，单位秒
    private Integer importInterval = 300;
    private SolrParam importParam;
    // 重做索引间隔，单位秒，默认永不不重做
    private Integer reIndexInterval = 0;
    private SolrParam reIndexParam;
}
