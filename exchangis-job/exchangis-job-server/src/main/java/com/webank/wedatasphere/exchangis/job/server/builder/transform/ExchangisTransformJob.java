package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobBase;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.AbstractExchangisJobHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Transform job
 */
public class ExchangisTransformJob extends ExchangisJobBase {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisTransformJob.class);
    /**
     * Set of sub jobs
     */
    private List<SubExchangisJob> subJobSet = new ArrayList<>();

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
                this.engine = jobInfoContent.getEngine();
                this.jobName = jobInfoContent.getSubJobName();
                ExchangisJobDataSourcesContent dataSourcesContent = jobInfoContent.getDataSources();
                if(Objects.nonNull(dataSourcesContent)) {
                    Optional.ofNullable(dataSourcesContent.getSinkId()).ifPresent( sinkId ->{
                        String[] idSerial = sinkId.split(AbstractExchangisJobHandler.ID_SPLIT_SYMBOL);
                        if(idSerial.length > 0){
                            this.sinkType = idSerial[0];
                        }
                    });
                    Optional.ofNullable(dataSourcesContent.getSourceId()).ifPresent( sourceId ->{
                        String[] idSerial = sourceId.split(AbstractExchangisJobHandler.ID_SPLIT_SYMBOL);
                        if(idSerial.length > 0){
                            this.sourceType = idSerial[0];
                        }
                    });
                }
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
            setIntoParams(REALM_JOB_MAPPING, () -> Json.convert(content.getTransforms(), Map.class, String.class, Object.class));
            if(Objects.nonNull(content.getParams())){
                if(Objects.nonNull(content.getParams().getSources())) {
                    setIntoParams(REALM_JOB_CONTENT_SOURCE, () -> {
                        List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getParams().getSources();
                        return items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey())).collect
                                (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                        ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue));
                    });
                }
                if(Objects.nonNull(content.getParams().getSinks())) {
                    setIntoParams(REALM_JOB_CONTENT_SINK, () -> {
                        List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getParams().getSinks();
                        return items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey())).collect
                                (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                        ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue));
                    });
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

        private void setIntoParams(String realm, Supplier<Map<String, Object>> paramsSupplier){
            Optional<Map<String,Object>> dataSourceMap = Optional.ofNullable(paramsSupplier.get());
            dataSourceMap.ifPresent( map -> {
                JobParamSet paramSet = map.entrySet().stream().map(entry -> JobParams.newOne(entry.getKey(), entry.getValue()))
                        .reduce(new JobParamSet(), JobParamSet::add, JobParamSet::combine);
                LOG.trace("Set params into sub exchangis job, realm: [" + realm + "], paramSet: [" + paramSet.toString() + "]");
                super.addRealmParams(realm, paramSet);
            });
        }
    }
}
