package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StarRocks in datax
 */
public class StarRocksDataxSubExchangisJobHandler extends AuthEnabledSubExchangisJobHandler {

    private static final int DEFAULT_HTTP_PORT = 8030;
    /**
     * Host
     */
    private static final JobParamDefine<String> SINK_HOST = JobParams.define("connection[0].host", JobParamConstraints.HOST);

    /**
     * TCP_Port
     */
    private static final JobParamDefine<String> SINK_PORT = JobParams.define("connection[0].port", JobParamConstraints.PORT);

    /**
     * HTTP_Port
     */
    private static final JobParamDefine<String> SINK_LOAD_URL = JobParams.define("loadUrl[0]", paramSet -> {
        JobParam<String> host = paramSet.get("connection[0].host");
        JobParam<String> httpPortParams = paramSet.get(JobParamConstraints.HTTP_PORT);
        String httpPort = DEFAULT_HTTP_PORT + "";
        if (Objects.nonNull(httpPortParams) && StringUtils.isNotBlank(httpPortParams.getValue())){
            httpPort = httpPortParams.getValue();
        }
        if (Objects.nonNull(host) && StringUtils.isNotBlank(host.getValue())) {
            return host.getValue() + ":" + httpPort;
        }
        return null;
    });

    /**
     * Database
     */
    private static final JobParamDefine<String> SINK_DATABASE = JobParams.define("database", JobParamConstraints.DATABASE);

    /**
     * Table
     */
    private static final JobParamDefine<String> SINK_TABLE = JobParams.define("table", JobParamConstraints.TABLE);

    /**
     * Connect params
     */
    private static final JobParamDefine<Map<String, String>> SINK_PARAMS_MAP = JobParams.define("connection[0].connParams", JobParamConstraints.CONNECT_PARAMS,
            connectParams -> Json.fromJson(connectParams, Map.class), String.class);

    /**
     * SQL column
     */
    private static final JobParamDefine<List<String>> SQL_COLUMN = JobParams.define("column", job -> {
        List<String> columns = job.getSinkColumns().stream().map(SubExchangisJob.ColumnDefine::getName).collect(Collectors.toList());
        if (columns.isEmpty()){
            columns.add("*");
        }
        return columns;
    }, SubExchangisJob.class);

    @Override
    public void handleJobSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
    }

    @Override
    public void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        if (Objects.nonNull(paramSet)){
            JobParamDefine<?>[] jobParamDefines = sinkMappings();
            Arrays.asList(jobParamDefines).forEach(
                    define -> paramSet.addNonNull(define.get(paramSet))
            );
        }
    }

    @Override
    public String dataSourceType() {
        return "starrocks";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "datax".equalsIgnoreCase(engineType);
    }

    private JobParamDefine<?>[] sourceMappings(){
        return null;
    }

    public JobParamDefine<?>[] sinkMappings(){
        return new JobParamDefine[]{USERNAME, PASSWORD, SINK_HOST, SINK_PORT, SINK_LOAD_URL, SINK_DATABASE, SINK_TABLE, SINK_PARAMS_MAP};
    }
}
