package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategy;
import com.webank.wedatasphere.exchangis.datasource.core.splitter.DataSourceSplitStrategyFactory;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.utils.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract implement, to fetch the data source params of job
 */
public class GenericSubExchangisJobHandler extends AbstractLoggingSubExchangisJobHandler{

    public static final String ID_SPLIT_SYMBOL = "\\.";

    private static final JobParamDefine<String> SOURCE_ID = JobParams.define("source_id");
    private static final JobParamDefine<Map<String, Object>> SOURCE = JobParams.define("source");

    private static final JobParamDefine<String> SINK_ID = JobParams.define("sink_id");
    private static final JobParamDefine<Map<String, Object>> SINK = JobParams.define("sink");

    @Override
    public String dataSourceType() {
        return DEFAULT_DATA_SOURCE_TYPE;
    }

    @Override
    public void handleJobSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        ExchangisJobInfo originJob = ctx.getOriginalJob();
        JobParamSet idParamSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_DATA_SOURCE);
        JobParamSet sourceParamSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
        if (Objects.nonNull(idParamSet) && Objects.nonNull(sourceParamSet)){
            info("Fetch data source parameters in [{}]", subExchangisJob.getSourceType());
            appendDataSourceParams(idParamSet.load(SOURCE), idParamSet.load(SOURCE_ID), sourceParamSet,
                    subExchangisJob.getSourceSplitParts(), originJob.getCreateUser());
        }

    }

    @Override
    public void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException{
        ExchangisJobInfo originJob = ctx.getOriginalJob();
        JobParamSet idParamSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_DATA_SOURCE);
        JobParamSet sinkParamSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        if (Objects.nonNull(idParamSet) && Objects.nonNull(sinkParamSet)){
            info("Fetch data source parameters in [{}]", subExchangisJob.getSinkType());
            appendDataSourceParams(idParamSet.load(SINK), idParamSet.load(SINK_ID), sinkParamSet,
                    subExchangisJob.getSinkSplitParts(), originJob.getCreateUser());
        }
    }

    /**
     * Append data source params
     * @param param param
     * @param idParam id param
     * @param paramSet param set
     * @param userName username
     * @throws ErrorException
     */
    private void appendDataSourceParams(JobParam<Map<String, Object>> param, JobParam<String> idParam,
                                        JobParamSet paramSet, List<Map<String, Object>> splitParts, String userName) throws ErrorException {
        DataSourceService dataSourceService = GenericSubExchangisJobHandler.GenericDataSourceService.instance;
        Map<String, Object> paramValue = param.getValue();
        String sourceId = idParam.getValue();
        Object dsType = null;
        String creator = null;
        Map<String, Object> connectParams = null;
        if (Objects.nonNull(paramValue) && !paramValue.isEmpty()) {
            String id = String.valueOf(paramValue.get("id"));
            if (StringUtils.isNoneBlank(id)){
                dsType = paramValue.get("type");
                creator = String.valueOf(paramValue.get("creator"));
                connectParams =
                        dataSourceService.getDataSourceConnectParamsById(creator, Long.valueOf(id));
                Optional.ofNullable(connectParams).ifPresent(params ->
                        params.forEach((key, value) -> paramSet.add(JobParams.newOne(key, value, !key.equals("_model_")))));
            }
        } else {
            // {TYPE}.{ID}.{DB}.{TABLE}
            String[] idSerial = sourceId.split(ID_SPLIT_SYMBOL);
            if (idSerial.length >= 2){
                dsType = idSerial[0];
                connectParams = dataSourceService.getDataSourceConnectParamsById(userName, Long.valueOf(idSerial[1]));
                Optional.ofNullable(connectParams).ifPresent(params ->
                        params.forEach((key, value) -> paramSet.add(JobParams.newOne(key, value, !key.equals("_model_")))));
            }
        }
        if (Objects.nonNull(dsType) && Objects.nonNull(connectParams)){
            ExchangisDataSourceDefinition dsDefinition = Objects.requireNonNull(getBean(ExchangisDataSourceContext.class))
                    .getExchangisDsDefinition(String.valueOf(dsType));
            if (Objects.nonNull(dsDefinition)){
                // Try to split data source
                String strategyName = dsDefinition.splitStrategyName();
                DataSourceSplitStrategy splitStrategy = StringUtils.isNotBlank(strategyName) ?
                        Objects.requireNonNull(getBean(DataSourceSplitStrategyFactory.class)).getOrCreateSplitter(strategyName) : dsDefinition.getSplitStrategy();
                if (Objects.nonNull(splitStrategy)){
                    Optional.ofNullable(splitStrategy.getSplitValues(connectParams, dsDefinition.splitKeys())).ifPresent(splitParts::addAll);
                }

            }
        }
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    public static class GenericDataSourceService{

        /**
         * Lazy load data source service
         */
        public static DataSourceService instance;

        static{
            instance = SpringContextHolder.getBean(DataSourceService.class);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + dataSourceType() + ")";
    }
}
