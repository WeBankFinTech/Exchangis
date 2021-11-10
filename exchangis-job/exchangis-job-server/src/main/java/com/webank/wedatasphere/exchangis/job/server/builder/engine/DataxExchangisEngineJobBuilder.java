package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;

public class DataxExchangisEngineJobBuilder extends AbstractExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    private static final Logger LOG = LoggerFactory.getLogger(DataxExchangisEngineJob.class);

    /**
     * Mapping params
     */
    private static final JobParamDefine<List<Map<String, Object>>> TRANSFORM_MAPPING = JobParams.define("mapping");

    /**
     * Source params for column
     */
    private static final JobParamDefine<String> SOURCE_FIELD_NAME = JobParams.define("name", "source_field_name", String.class);
    private static final JobParamDefine<String> SOURCE_FIELD_TYPE = JobParams.define("type", "source_field_type", String.class);
    private static final JobParamDefine<Integer> SOURCE_FIELD_INDEX = JobParams.define("index", "source_field_index", Integer.class);

    /**
     * Sink params for column
      */
    private static final JobParamDefine<String> SINK_FIELD_NAME = JobParams.define("name", "sink_field_name", String.class);
    private static final JobParamDefine<String> SINK_FIELD_TYPE = JobParams.define("type", "sink_field_type", String.class);
    private static final JobParamDefine<Integer> SINK_FIELD_INDEX = JobParams.define("index", "sink_field_index", Integer.class);

    private static final JobParamDefine<List<Map<String, Object>>> SOURCE_COLUMN = JobParams.define("column", (BiFunction<String, JobParamSet, List<Map<String, Object>>>) (key, paramSet) ->{
        List<Map<String, Object>> columns = new ArrayList<>();
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet).getValue();
        if(Objects.nonNull(mappings)){
            mappings.forEach( mapping -> {
                Map<String, Object> column = new HashMap<>();
                columns.add(column);
                column.put(SOURCE_FIELD_NAME.getKey(), SOURCE_FIELD_NAME.newParam(mapping).getValue());
                column.put(SOURCE_FIELD_TYPE.getKey(), SOURCE_FIELD_TYPE.newParam(mapping).getValue());
                column.put(SOURCE_FIELD_INDEX.getKey(), SOURCE_FIELD_INDEX.newParam(mapping).getValue());
            });
        }
        return columns;
    });

    private static final JobParamDefine<List<Map<String, Object>>> SINK_COLUMN = JobParams.define("column", (BiFunction<String, JobParamSet, List<Map<String, Object>>>) (key, paramSet) ->{
        List<Map<String, Object>> columns = new ArrayList<>();
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet).getValue();
        if(Objects.nonNull(mappings)){
            mappings.forEach(mapping -> {
                Map<String, Object> column = new HashMap<>();
                columns.add(column);
                column.put(SINK_FIELD_NAME.getKey(), SINK_FIELD_NAME.newParam(mapping).getValue());
                column.put(SINK_FIELD_TYPE.getKey(), SINK_FIELD_TYPE.newParam(mapping).getValue());
                column.put(SINK_FIELD_INDEX.getKey(), SINK_FIELD_INDEX.newParam(mapping).getValue());
            });
        }
        return columns;
    });

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean canBuild(SubExchangisJob inputJob) {
        return "datax".equalsIgnoreCase(inputJob.getEngine());
    }

    @Override
    public DataxExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        try {
            DataxExchangisEngineJob engineJob = new DataxExchangisEngineJob();
            DataxCode dataxCode = buildDataxCode(inputJob);
            engineJob.setCode(Json.toJson(dataxCode, null));
            try {
                LOG.info("Datax-code built complete, output: " + Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dataxCode));
            } catch (JsonProcessingException e) {
                //Ignore
            }
            if (Objects.nonNull(expectJob)) {
                engineJob.setJobName(expectJob.getJobName());
                engineJob.setRuntimeParams(expectJob.getRuntimeParams());
                engineJob.setEngine(expectJob.getEngine());
            }
            return engineJob;
        }catch(Exception e){
            throw new ExchangisJobException(ExchangisJobExceptionCode.ENGINE_JOB_ERROR.getCode(),
                    "Fail to build datax engine job, message:[" + e.getMessage() + "]", e);
        }
    }

    /**
     * Datax code
     * @param inputJob input job
     * @return code
     */
    private DataxCode buildDataxCode(SubExchangisJob inputJob){
        DataxCode code = new DataxCode();
        //To construct settings
       JobParamSet paramSet =  inputJob.getRealmParams(SubExchangisJob.REALM_JOB_SETTINGS);
       if(Objects.nonNull(paramSet)) {
           JsonConfiguration setting = JsonConfiguration.from(code.getSetting());
           paramSet.toList().forEach(param -> setting.set(param.getStrKey(), param.getValue()));
           code.setSetting(setting.toMap());
       }
       //To construct content
       Content content = new Content();
       code.getContent().add(content);
       JobParamSet transformJobParamSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_MAPPING);
       paramSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
       JsonConfiguration reader = JsonConfiguration.from(content.getReader());
       if(Objects.nonNull(paramSet)){
           if (StringUtils.isNotBlank(inputJob.getSourceType())){
               reader.set("name", inputJob.getSourceType().toLowerCase() + "reader");
               reader.set("parameter", buildContentParam(paramSet, transformJobParamSet, SOURCE_COLUMN));
               content.setReader(reader.toMap());
           }
       }
       paramSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
        JsonConfiguration writer = JsonConfiguration.from(content.getReader());
       if(Objects.nonNull(paramSet)){
           if (StringUtils.isNotBlank(inputJob.getSinkType())){
               writer.set("name", inputJob.getSinkType().toLowerCase() + "writer");
               writer.set("parameter", buildContentParam(paramSet, transformJobParamSet, SINK_COLUMN));
               content.setWriter(writer.toMap());
           }
       }
        return code;
    }
    private Map<String, Object> buildContentParam(JobParamSet paramSet, JobParamSet transformJobParamSet,
                                                  JobParamDefine<List<Map<String, Object>>> columnJobParamDefine){
        JsonConfiguration item = JsonConfiguration.from("{}");
        //Ignore temp params
        paramSet.toList(false).forEach(param -> item.set(param.getStrKey(), param.getValue()));
        if(Objects.nonNull(transformJobParamSet)){
            item.set(columnJobParamDefine.getKey(), columnJobParamDefine.newParam(transformJobParamSet).getValue());
        }
        return item.toMap();
    }
    private static class DataxCode{
        /**
         * Setting
         */
        private Map<String, Object> setting = new HashMap<>();

        /**
         * Content list
         */
        private List<Content> content = new ArrayList<>();

        public Map<String, Object> getSetting() {
            return setting;
        }

        public void setSetting(Map<String, Object> setting) {
            this.setting = setting;
        }

        public List<Content> getContent() {
            return content;
        }

        public void setContent(List<Content> content) {
            this.content = content;
        }
    }

    private static class Content{
        /**
         * Reader
         */
        private Map<String, Object> reader = new HashMap<>();
        /**
         * Writer
         */
        private Map<String, Object> writer = new HashMap<>();

        public Map<String, Object> getReader() {
            return reader;
        }

        public void setReader(Map<String, Object> reader) {
            this.reader = reader;
        }

        public Map<String, Object> getWriter() {
            return writer;
        }

        public void setWriter(Map<String, Object> writer) {
            this.writer = writer;
        }

    }

    public static void main(String[] args){
        try {
            System.out.println(Json.toJson(new DataxCode(), null));
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
