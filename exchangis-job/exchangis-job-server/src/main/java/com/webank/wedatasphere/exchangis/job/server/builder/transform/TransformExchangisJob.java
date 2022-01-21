package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.GenericExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.GenericSubExchangisJobHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Transform job
 */
public class TransformExchangisJob extends GenericExchangisJob {

    private static final Logger LOG = LoggerFactory.getLogger(TransformExchangisJob.class);

    private ExchangisJobInfo exchangisJobInfo;
    /**
     * Set of sub jobs
     */
    private List<SubExchangisJob> subJobSet = new ArrayList<>();

    public ExchangisJobInfo getExchangisJobInfo() {
        return exchangisJobInfo;
    }

    public void setExchangisJobInfo(ExchangisJobInfo exchangisJobInfo) {
        this.exchangisJobInfo = exchangisJobInfo;
    }

    public List<SubExchangisJob> getSubJobSet() {
        return subJobSet;
    }

    public void setSubJobSet(List<SubExchangisJob> subJobSet) {
        this.subJobSet = subJobSet;
    }

    /**
     * Wrap entity of 'ExchangisJobInfoContent'
     */
    public static class SubExchangisJobAdapter extends SubExchangisJob{

        /**
         * Content VO
         */
        private ExchangisJobInfoContent jobInfoContent;

        public SubExchangisJobAdapter(ExchangisJobInfoContent jobInfoContent){
            if(Objects.nonNull(jobInfoContent)) {
                this.engineType = jobInfoContent.getEngine();
                this.name = jobInfoContent.getSubJobName();
                convertContentToParams(jobInfoContent);
            }
        }

        public ExchangisJobInfoContent getJobInfoContent() {
            return jobInfoContent;
        }

        public void setJobInfoContent(ExchangisJobInfoContent jobInfoContent) {
            this.jobInfoContent = jobInfoContent;
        }

        private void convertContentToParams(ExchangisJobInfoContent content){
            setIntoParams(REALM_JOB_DATA_SOURCE, () -> Json.convert(content.getDataSources(), Map.class, String.class, String.class));
            setIntoParams(REALM_JOB_COLUMN_MAPPING, () -> Json.convert(content.getTransforms(), Map.class, String.class, Object.class));
            if(Objects.nonNull(content.getParams())){
                if(Objects.nonNull(content.getParams().getSources())) {
                    JobParamSet paramSet = setIntoParams(REALM_JOB_CONTENT_SOURCE, () -> {
                        List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getParams().getSources();
                        return items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey())).collect
                                (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                        ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue));
                    });
                    if(Objects.nonNull(paramSet)) {
                        this.sourceType = resolveDataSourceId(content.getDataSources().getSourceId(), paramSet);
                    }
                }
                if(Objects.nonNull(content.getParams().getSinks())) {
                    JobParamSet paramSet = setIntoParams(REALM_JOB_CONTENT_SINK, () -> {
                        List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getParams().getSinks();
                        return items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey())).collect
                                (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                        ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue));
                    });
                    if(Objects.nonNull(paramSet)) {
                       this.sinkType =  resolveDataSourceId(content.getDataSources().getSinkId(), paramSet);
                    }
                }
            }
            if (Objects.nonNull(content.getSettings())){
                setIntoParams(REALM_JOB_SETTINGS, () -> {
                    List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getSettings();
                    return items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey())).collect
                            (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                    ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue));
                });
            }
        }

        private JobParamSet setIntoParams(String realm, Supplier<Map<String, Object>> paramsSupplier){
            Optional<Map<String,Object>> dataSourceMap = Optional.ofNullable(paramsSupplier.get());
            dataSourceMap.ifPresent( map -> {
                JobParamSet paramSet = map.entrySet().stream().map(entry -> JobParams.newOne(entry.getKey(), entry.getValue()))
                        .reduce(new JobParamSet(), JobParamSet::add, JobParamSet::combine);
                LOG.trace("Set params into sub exchangis job, realm: [{}], paramSet: [{}]", realm, paramSet.toString());
                super.addRealmParams(realm, paramSet);
            });
            return getRealmParams(realm);
        }

        /**
         *
         * @param dataSourceId
         * @param paramSet
         * @return return data source type
         */
        private String resolveDataSourceId(String dataSourceId, JobParamSet paramSet){
            AtomicReference<String[]> result = new AtomicReference<>(new String[]{});
            Optional.ofNullable(dataSourceId).ifPresent( id ->
                    result.set(id.split(GenericSubExchangisJobHandler.ID_SPLIT_SYMBOL)));
            String[] idSerial = result.get();
            if(idSerial.length > 0){
                if(idSerial.length >= 4){
//                    paramSet.add(JobParams.newOne("datasource", idSerial[1], true));
                    paramSet.add(JobParams.newOne(JobParamConstraints.DATABASE, idSerial[2], true));
                    paramSet.add(JobParams.newOne(JobParamConstraints.TABLE, idSerial[3], true));
                }
               return idSerial[0];
            }
            return null;
        }

    }
}
