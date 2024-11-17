package com.webank.wedatasphere.exchangis.job.server.builder.transform;

import com.webank.wedatasphere.exchangis.common.linkis.bml.BmlResource;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.GenericExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.content.*;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.GenericSubExchangisJobHandler;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformTypes;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import com.webank.wedatasphere.exchangis.job.utils.ColumnDefineUtils;
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
    public static class TransformSubExchangisJob extends SubExchangisJob{

        private static final String CODE_RESOURCE_NAME = ".code";
        /**
         * Transform type
         */
        private TransformTypes transformType;

        /**
         * Content VO
         */
        private ExchangisJobInfoContent jobInfoContent;

        /**
         * Resource map
         */
        private final Map<String, BmlResource> resources = new HashMap<>();

        /**
         * Rate params
         */
        private Map<String, Integer> rateParamMap = new HashMap<>();

        private List<Long> dsModelIds = new ArrayList<>();

        public TransformSubExchangisJob(){
            // Empty construct
        }
        public TransformSubExchangisJob(ExchangisJobInfoContent jobInfoContent, Map<String, String> jobParams){
            this.jobParams.putAll(jobParams);
            if(Objects.nonNull(jobInfoContent)) {
                this.jobInfoContent = jobInfoContent;
                this.engineType = jobInfoContent.getEngine();
                this.name = jobInfoContent.getSubJobName();
                convertContentToParams(jobInfoContent);
                Optional.ofNullable(jobInfoContent.getTransforms()).ifPresent(transforms -> {
                    if (StringUtils.isNotBlank(transforms.getType())) {
                        this.transformType = TransformTypes.valueOf(transforms.getType().toUpperCase(Locale.ROOT));
                        // TODO define different transform sub jobs
                        convertTransformToColumnDefine(transforms);
                    }
                });
            }
        }

        public ExchangisJobInfoContent getJobInfoContent() {
            return jobInfoContent;
        }

        public void setJobInfoContent(ExchangisJobInfoContent jobInfoContent) {
            this.jobInfoContent = jobInfoContent;
        }

        /**
         * Convert content to column definitions
         * @param transforms transform
         */
        private void convertTransformToColumnDefine(ExchangisJobTransformsContent transforms){
            List<ExchangisJobTransformsItem> items = transforms.getMapping();
            if (Objects.nonNull(items)){
                for(int i = 0; i < items.size(); i++){
                    final int index = i;
                    ExchangisJobTransformsItem item = items.get(i);
                    ColumnDefine srcColumn = ColumnDefineUtils.getColumn(item.getSourceFieldName(),
                            item.getSourceFieldType(), item.getSourceFieldIndex());
                    ColumnDefine sinkColumn = ColumnDefineUtils.getColumn(item.getSinkFieldName(),
                            item.getSinkFieldType(), item.getSinkFieldIndex());
                    Optional.ofNullable(item.getValidator()).ifPresent(validator ->
                            convertValidatorFunction(index, validator));
                    Optional.ofNullable(item.getTransformer()).ifPresent(transformer ->
                            convertTransformFunction(index, transformer));
                    getSourceColumns().add(srcColumn);
                    getSinkColumns().add(sinkColumn);
                }
            }
        }
        /**
         * Convert to validator function
         * @param index index
         * @param validator validator
         */
        private void convertValidatorFunction(int index, List<String> validator){
            if (validator.size() > 0) {
                ColumnFunction function = new ColumnFunction();
                function.setIndex(index);
                // TODO abstract the name
                function.setName("dx_filter");
                function.setParams(new ArrayList<>(validator));
                getColumnFunctions().add(function);
            }
        }

        /**
         * Convert to transform function
         * @param index index
         * @param transformer transformer
         */
        private void convertTransformFunction(int index, ExchangisJobTransformer transformer){
            if (StringUtils.isNotBlank(transformer.getName())) {
                ColumnFunction function = new ColumnFunction();
                function.setIndex(index);
                function.setName(transformer.getName());
                function.setParams(transformer.getParams());
                getColumnFunctions().add(function);
            }
        }
        /**
         * Convert content to params
         * @param content content
         */
        private void convertContentToParams(ExchangisJobInfoContent content){
            ExchangisJobDataSourcesContent dataSourcesContent = content.getDataSources();
            setIntoParams(REALM_JOB_DATA_SOURCE, () -> Json.convert(dataSourcesContent, Map.class, String.class, Object.class));
            if(Objects.nonNull(content.getParams())){
                if(Objects.nonNull(content.getParams().getSources())) {
                    List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getParams().getSources();
                    JobParamSet paramSet = setIntoParams(REALM_JOB_CONTENT_SOURCE, () -> items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey()) && Objects.nonNull(item.getConfigValue())).collect
                            (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                    ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue)));
                    if(Objects.nonNull(paramSet)) {
                        String sourceId = dataSourcesContent.getSourceId();
                        ExchangisJobDataSourcesContent.ExchangisJobDataSource source = dataSourcesContent.getSource();
                        this.sourceType = resolveDataSource(sourceId, source, paramSet);
                    }
                }

                if(Objects.nonNull(content.getParams().getSinks())) {
                    List<ExchangisJobParamsContent.ExchangisJobParamsItem> items = content.getParams().getSinks();
                    JobParamSet paramSet = setIntoParams(REALM_JOB_CONTENT_SINK, () -> items.stream().filter(item -> StringUtils.isNotBlank(item.getConfigKey()) && Objects.nonNull(item.getConfigValue())).collect
                            (Collectors.toMap(ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigKey,
                                    ExchangisJobParamsContent.ExchangisJobParamsItem::getConfigValue)));
                    if(Objects.nonNull(paramSet)) {
                       String sinkId = dataSourcesContent.getSinkId();
                       ExchangisJobDataSourcesContent.ExchangisJobDataSource sink = dataSourcesContent.getSink();
                       this.sinkType = resolveDataSource(sinkId, sink, paramSet);
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
         * resolve data source info
         * @param dataSourceId data source id string
         * @param dataSource data source content
         * @param paramSet param set
         * @return return data source type
         */
        private String resolveDataSource(String dataSourceId,
                                         ExchangisJobDataSourcesContent.ExchangisJobDataSource dataSource,
                                         JobParamSet paramSet){
            Calendar calendar = Calendar.getInstance();
            if (StringUtils.isNotBlank(dataSourceId)) {
                AtomicReference<String[]> result = new AtomicReference<>(new String[]{});
                result.set(dataSourceId.split(GenericSubExchangisJobHandler.ID_SPLIT_SYMBOL));
                String[] idSerial = result.get();
                if (idSerial.length > 0) {
                    if (idSerial.length >= 4) {
                        paramSet.add(JobParams.newOne(JobParamConstraints.DATA_SOURCE_ID, idSerial[1], true));
                        paramSet.add(JobParams.newOne(JobParamConstraints.DATABASE, idSerial[2], true));
                        paramSet.add(JobParams.newOne(JobParamConstraints.TABLE,
                                JobUtils.replaceVariable(idSerial[3], this.jobParams,
                                       Long.parseLong(String.valueOf(Optional.ofNullable(jobParams.get(JobParamConstraints.EXTRA_SUBMIT_DATE))
                                                .orElse(calendar.getTimeInMillis())))), true));
                    }
                    return idSerial[0];
                }
            } else if (Objects.nonNull(dataSource)){
                if (Objects.nonNull(dataSource.getId())) {
                    paramSet.addNonNull(JobParams.newOne(JobParamConstraints.DATA_SOURCE_ID, dataSource.getId() + "", true));
                }
                paramSet.addNonNull(JobParams.newOne(JobParamConstraints.DATA_SOURCE_NAME, dataSource.getName(), true));
                paramSet.addNonNull(JobParams.newOne(JobParamConstraints.DATA_SOURCE_CREATOR, dataSource.getCreator(), true));
                paramSet.addNonNull(JobParams.newOne(JobParamConstraints.DATABASE, dataSource.getDb(), true));
                paramSet.addNonNull(JobParams.newOne(JobParamConstraints.TABLE,
                        JobUtils.replaceVariable(dataSource.getTable(), this.jobParams,
                                Long.parseLong(String.valueOf(Optional.ofNullable(jobParams.get(JobParamConstraints.EXTRA_SUBMIT_DATE))
                                        .orElse(calendar.getTimeInMillis())))), true));
                return dataSource.getType();
            }
            return null;
        }

        /**
         * Transform type
         * @return type string
         */
        public TransformTypes getTransformType() {
            return transformType;
        }

        /**
         * Add code resource
         * @param bmlResource bml resource
         */
        void addCodeResource(BmlResource bmlResource){
            this.resources.put(CODE_RESOURCE_NAME, bmlResource);
        }

        /**
         * Get code resource
         * @return bml resource
         */
        public BmlResource getCodeResource(){
            return this.resources.get(CODE_RESOURCE_NAME);
        }

        public Map<String, Integer> getRateParamMap() {
            return rateParamMap;
        }

        @Override
        public SubExchangisJob copy() {
            TransformSubExchangisJob job = new TransformSubExchangisJob();
            // Basic info
            job.id = this.id;
            job.name = this.name;
            job.engineType = this.engineType;
            job.jobLabel = this.jobLabel;
            job.setJobLabels(this.getJobLabels());
            job.createTime = this.createTime;
            job.lastUpdateTime = this.lastUpdateTime;
            job.createUser = this.createUser;
            // Advance info
            job.sourceType = this.sourceType;
            job.sinkType = this.sinkType;
            job.jobParams.putAll(this.jobParams);
            job.getSourceColumns().addAll(this.getSourceColumns());
            job.getSinkColumns().addAll(this.getSinkColumns());
            job.getColumnFunctions().addAll(this.getColumnFunctions());
            //Deep copy the param realm
            this.copyParamSet(job::addRealmParams);
            job.transformType = this.transformType;
            job.jobInfoContent = this.jobInfoContent;
            // Code resource
            job.resources.putAll(this.resources);
            job.rateParamMap = this.rateParamMap;
            job.dsModelIds = this.dsModelIds;
            return job;
        }
    }



}
