package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Elastic search datax mapping
 */
public class EsDataxParamsMapping extends AbstractExchangisJobParamsMapping{

    private static final Logger LOG = LoggerFactory.getLogger(EsDataxParamsMapping.class);

    /**
     * Elastic search urls
     */
    private static final JobParamDefine<String>  ELASTIC_URLS = JobParams.define("elasticUrls", "elasticUrls", urls -> {
        List<String> elasticUrls = Json.fromJson(urls, List.class, String.class);
        if (Objects.nonNull(elasticUrls)){
            return StringUtils.join(elasticUrls, ",");
        }
        return null;
    }, String.class);

    /**
     * Index name
     */
    private static final JobParamDefine<String> INDEX = JobParams.define("index", JobParamConstraints.DATABASE);

    /**
     * Index type
     */
    private static final JobParamDefine<String> TYPE = JobParams.define("type", JobParamConstraints.TABLE);

    /**
     * If in security connection
     */
    private static final JobParamDefine<String> SECURE = JobParams.define("secure");

    /**
     * Max merge count
     */
    private static final JobParamDefine<Map<String, Object>> SETTINGS = JobParams.define("settings",
            () -> {
                Map<String, Object> settings = new HashMap<>();
                settings.put("index.merge.scheduler.max_merge_count", 100);
                return settings;
            });

    /**
     * Clean up
     */
    private static final JobParamDefine<String> CLEANUP = JobParams.define("cleanUp", () -> "false");

    /**
     * Max pool size
     */
    private static final JobParamDefine<String> CLIENT_MAX_POOL_SIZE = JobParams.define("clientConfig.maxPoolSize", () -> "1");

    /**
     * Socket time out
     */
    private static final JobParamDefine<String> CLIENT_SOCK_TIMEOUT = JobParams.define("clientConfig.sockTimeout", () -> "60000");

    /**
     * Connection timeout
     */
    private static final JobParamDefine<String> CLIENT_CONN_TIMEOUT = JobParams.define("clientConfig.connTimeout", () -> "60000");

    /**
     * Timeout
     */
    private static final JobParamDefine<String> CLIENT_TIMEOUT = JobParams.define("clientConfig.timeout", () -> "60000");

    /**
     * Compress
     */
    private static final JobParamDefine<String> CLIENT_COMPRESS = JobParams.define("clientConfig.compress", () -> "true");
    @Override
    public String dataSourceType() {
        return "elasticsearch";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "datax".equalsIgnoreCase(engineType);
    }

    @Override
    public JobParamDefine<?>[] sourceMappings() {
        return new JobParamDefine[0];
    }

    @Override
    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[]{USERNAME, PASSWORD, ELASTIC_URLS, INDEX, TYPE, SECURE,
                SETTINGS, CLEANUP, CLIENT_MAX_POOL_SIZE, CLIENT_SOCK_TIMEOUT, CLIENT_CONN_TIMEOUT,
                CLIENT_TIMEOUT, CLIENT_COMPRESS
        };
    }
}
