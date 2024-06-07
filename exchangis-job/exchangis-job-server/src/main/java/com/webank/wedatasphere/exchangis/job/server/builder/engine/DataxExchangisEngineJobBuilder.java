package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.engine.domain.EngineBmlResource;
import com.webank.wedatasphere.exchangis.engine.resource.loader.datax.DataxEngineResourceConf;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformTypes;
import com.webank.wedatasphere.exchangis.job.server.utils.JsonEntity;
import com.webank.wedatasphere.exchangis.job.utils.MemUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Datax engine job builder
 */
public class DataxExchangisEngineJobBuilder extends AbstractResourceEngineJobBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DataxExchangisEngineJob.class);

    private static final String BYTE_SPEED_SETTING_PARAM = "setting.speed.byte";

    private static final String PROCESSOR_SWITCH = "setting.useProcessor";

    private static final String PROCESSOR_BASE_PATH = "core.processor.loader.plugin.sourcePath";

    private static final Map<String, String> PLUGIN_NAME_MAPPER = new HashMap<>();

    static{
        //hive use hdfs plugin resource
        PLUGIN_NAME_MAPPER.put("hive", "hdfs");
        PLUGIN_NAME_MAPPER.put("tdsql", "mysql");
    }

    /**
     * Column mappings define
     */
    private static final JobParamDefine<DataxMappingContext> COLUMN_MAPPINGS = JobParams.define("column.mappings", job -> {
        DataxMappingContext mappingContext = new DataxMappingContext();
        job.getSourceColumns().forEach(columnDefine -> mappingContext.getSourceColumns().add(
                new DataxMappingContext.Column(columnDefine.getName(), columnDefine.getType(),
                        columnDefine.getRawType(), columnDefine.getIndex() + "")
        ));
        job.getSinkColumns().forEach(columnDefine -> mappingContext.getSinkColumns().add(
                new DataxMappingContext.Column(columnDefine.getName(), columnDefine.getType(),
                        columnDefine.getRawType(), columnDefine.getIndex() + "")
        ));
        job.getColumnFunctions().forEach(function -> {
            DataxMappingContext.Transformer.Parameter parameter = new DataxMappingContext.Transformer.Parameter();
            parameter.setColumnIndex(function.getIndex() + "");
            parameter.setParas(function.getParams());
            mappingContext.getTransformers()
                    .add(new DataxMappingContext.Transformer(function.getName(), parameter));
        });
        return mappingContext;
    }, SubExchangisJob.class);

    /**
     * Source content
     */
    private static final JobParamDefine<String> PLUGIN_SOURCE_NAME = JobParams.define("content[0].reader.name", job ->
            getPluginName(job.getSourceType(), "reader"), SubExchangisJob.class);

    private static final JobParamDefine<Map<String, Object>> PLUGIN_SOURCE_PARAM = JobParams.define("content[0].reader.parameter", job ->
            job.getParamsToMap(SubExchangisJob.REALM_JOB_CONTENT_SOURCE, false), SubExchangisJob.class);

    /**
     * Sink content
     */
    private static final JobParamDefine<String> PLUGIN_SINK_NAME = JobParams.define("content[0]].writer.name", job ->
            getPluginName(job.getSinkType(), "writer"), SubExchangisJob.class);

    private static final JobParamDefine<Map<String, Object>> PLUGIN_SINK_PARAM = JobParams.define("content[0].writer.parameter", job ->
            job.getParamsToMap(SubExchangisJob.REALM_JOB_CONTENT_SINK, false), SubExchangisJob.class);

    /**
     * Source columns
     */
    private static final JobParamDefine<List<DataxMappingContext.Column>> SOURCE_COLUMNS = JobParams.define("content[0].reader.parameter.column",
            DataxMappingContext::getSourceColumns,DataxMappingContext.class);

    /**
     * Sink columns
     */
    private static final JobParamDefine<List<DataxMappingContext.Column>> SINK_COLUMNS = JobParams.define("content[0].writer.parameter.column",
            DataxMappingContext::getSinkColumns,DataxMappingContext.class);

    /**
     * Transform list
     */
    private static final JobParamDefine<List<DataxMappingContext.Transformer>> TRANSFORM_LIST = JobParams.define("content[0].transformer",
            DataxMappingContext::getTransformers, DataxMappingContext.class);

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean canBuild(SubExchangisJob inputJob) {
        return "datax".equalsIgnoreCase(inputJob.getEngineType());
    }

    @Override
    public DataxExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {

        try {
            DataxExchangisEngineJob engineJob = new DataxExchangisEngineJob(expectOut);
            engineJob.setId(inputJob.getId());
            Map<String, Object> codeMap = buildDataxCode(inputJob, ctx);
            if (Objects.nonNull(codeMap)){
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datax-code built complete, output: " + Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(codeMap));
                    }
                    info("Datax-code built complete, output: " + Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(codeMap));
                } catch (JsonProcessingException e) {
                    //Ignore
                }
                engineJob.setCode(codeMap);
            }
            // engine resources
            engineJob.getResources().addAll(
                    getResources(inputJob.getEngineType().toLowerCase(Locale.ROOT), getResourcesPaths(inputJob)));
            if (inputJob instanceof TransformExchangisJob.TransformSubExchangisJob){
                TransformExchangisJob.TransformSubExchangisJob transformJob = ((TransformExchangisJob.TransformSubExchangisJob) inputJob);
                TransformTypes type = transformJob.getTransformType();
                if (type == TransformTypes.PROCESSOR){
                    settingProcessorInfo(transformJob, engineJob);
                }
            }
            engineJob.setName(inputJob.getName());
            //Unit MB
            Optional.ofNullable(engineJob.getRuntimeParams().get(BYTE_SPEED_SETTING_PARAM)).ifPresent(byteLimit -> {
                long limit = Long.parseLong(String.valueOf(byteLimit));
                // Convert to bytes
                engineJob.getRuntimeParams().put(BYTE_SPEED_SETTING_PARAM,
                        MemUtils.convertToByte(limit, MemUtils.StoreUnit.MB.name()));
            });

            engineJob.setCreateUser(inputJob.getCreateUser());
            // Lock the memory unit
            engineJob.setMemoryUnitLock(true);
            return engineJob;

        } catch (Exception e) {
            throw new ExchangisJobException(ExchangisJobExceptionCode.BUILDER_ENGINE_ERROR.getCode(),
                    "Fail to build datax engine job, message:[" + e.getMessage() + "]", e);
        }
    }

    /**
     * Build datax code content
     * @param inputJob input job
     * @param ctx ctx
     * @return code map
     */
    private Map<String, Object> buildDataxCode(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx){
        JsonEntity dataxJob = JsonEntity.from("{}");
        dataxJob.set(PLUGIN_SOURCE_NAME.getKey(), PLUGIN_SOURCE_NAME.getValue(inputJob));
        Optional.ofNullable(PLUGIN_SOURCE_PARAM.getValue(inputJob)).ifPresent(source -> source.forEach((key, value) ->{
            dataxJob.set(PLUGIN_SOURCE_PARAM.getKey() + "." + key, value);
        }));
        dataxJob.set(PLUGIN_SINK_NAME.getKey(), PLUGIN_SINK_NAME.getValue(inputJob));
        Optional.ofNullable(PLUGIN_SINK_PARAM.getValue(inputJob)).ifPresent(sink -> sink.forEach((key, value) -> {
            dataxJob.set(PLUGIN_SINK_PARAM.getKey() + "." + key, value);
        }));
        DataxMappingContext mappingContext = COLUMN_MAPPINGS.getValue(inputJob);
        if (Objects.isNull(dataxJob.get(SOURCE_COLUMNS.getKey()))) {
            dataxJob.set(SOURCE_COLUMNS.getKey(), SOURCE_COLUMNS.getValue(mappingContext));
        }
        if (Objects.isNull(dataxJob.get(SINK_COLUMNS.getKey()))){
            dataxJob.set(SINK_COLUMNS.getKey(), SINK_COLUMNS.getValue(mappingContext));
        }
        dataxJob.set(TRANSFORM_LIST.getKey(), TRANSFORM_LIST.getValue(mappingContext));
        return dataxJob.toMap();
    }

    /**
     * Setting processor info into engine job
     * @param transformJob transform job
     * @param engineJob engine job
     */
    private void settingProcessorInfo(TransformExchangisJob.TransformSubExchangisJob transformJob, ExchangisEngineJob engineJob){
        Optional.ofNullable(transformJob.getCodeResource()).ifPresent(codeResource ->{
            engineJob.getRuntimeParams().put(PROCESSOR_SWITCH, true);
            Object basePath = engineJob.getRuntimeParams().computeIfAbsent(PROCESSOR_BASE_PATH, key -> "proc/src");
            engineJob.getResources().add(new EngineBmlResource(engineJob.getEngineType(), ".",
                    String.valueOf(basePath) + IOUtils.DIR_SEPARATOR_UNIX + "code_" + transformJob.getId(),
                    codeResource.getResourceId(), codeResource.getVersion(), transformJob.getCreateUser()));
        });
    }
    private String[] getResourcesPaths(SubExchangisJob inputJob){
        return new String[]{
                DataxEngineResourceConf.RESOURCE_PATH_PREFIX.getValue() + IOUtils.DIR_SEPARATOR_UNIX + "reader" + IOUtils.DIR_SEPARATOR_UNIX +
                        PLUGIN_SOURCE_NAME.getValue(inputJob),
                DataxEngineResourceConf.RESOURCE_PATH_PREFIX.getValue() + IOUtils.DIR_SEPARATOR_UNIX + "writer" + IOUtils.DIR_SEPARATOR_UNIX +
                        PLUGIN_SINK_NAME.getValue(inputJob)
        };
    }
    // core.processor.loader.plugin.sourcePath
    /**
     * Plugin name
     * @param typeName type name
     * @param suffix suffix
     * @return plugin name
     */
    private static String getPluginName(String typeName, String suffix){
        return Objects.nonNull(typeName) ? PLUGIN_NAME_MAPPER.getOrDefault(typeName.toLowerCase(Locale.ROOT),
                typeName.toLowerCase(Locale.ROOT))
                + suffix : null;
    }
}
