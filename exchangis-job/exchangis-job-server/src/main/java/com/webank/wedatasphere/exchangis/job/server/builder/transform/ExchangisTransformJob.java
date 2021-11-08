package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobBase;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
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
         * Engine name
         */
        private String engine;

        /**
         * Name of sub job
         */
        private String subJobName;

        /**
         * Content VO
         */
        private ExchangisJobInfoContent jobInfoContent;

        public SubExchangisJobAdapter(ExchangisJobInfoContent jobInfoContent){
            if(Objects.nonNull(jobInfoContent)) {
                this.engine = jobInfoContent.getEngine();
                this.subJobName = jobInfoContent.getSubJobName();
                convertContentToParams(jobInfoContent);
            }
        }

        public String getEngine() {
            return engine;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public String getSubJobName() {
            return subJobName;
        }

        public void setSubJobName(String subJobName) {
            this.subJobName = subJobName;
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
                JobParamSet paramSet = map.entrySet().stream().map(entry -> JobParams.defineWithValue(entry.getKey(), entry.getValue()))
                        .reduce(new JobParamSet(), JobParamSet::append, JobParamSet::combine);
                LOG.trace("Set params into sub exchangis job, realm: [" + realm + "], paramSet: [" + paramSet.toString() + "]");
                super.addRealmParams(realm, paramSet);
            });
        }
    }
}
