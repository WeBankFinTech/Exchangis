package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.builder.api.AbstractExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SINK;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SOURCE;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_SETTINGS;
import static com.webank.wedatasphere.exchangis.job.server.builder.engine.SqoopExchangisEngineJobBuilder.MODE_TYPE.EXPORT;
import static com.webank.wedatasphere.exchangis.job.server.builder.engine.SqoopExchangisEngineJobBuilder.MODE_TYPE.IMPORT;

public class SqoopExchangisEngineJobBuilder extends AbstractExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    private static final Logger LOG = LoggerFactory.getLogger(SqoopExchangisEngineJobBuilder.class);

    private static final List<String> SUPPORT_BIG_DATA_TYPES = Arrays.asList("HIVE", "HBASE");

    private static final List<String> SUPPORT_RDBMS_TYPES = Arrays.asList("MYSQL", "ORACLE");

    public enum MODE_TYPE { IMPORT, EXPORT}
    /**
     * Verbose, default null (means not open verbose)
     */
    private static final JobParamDefine<String> VERBOSE = JobParams.define("sqoop.args.verbose", (BiFunction<String, SubExchangisJob, String>) (k, job) -> null);
    /**
     * Sqoop mode
     */
    private static final JobParamDefine<String> MODE = JobParams.define("sqoop.mode", (BiFunction<String, SubExchangisJob, String>) (k, job) -> SUPPORT_BIG_DATA_TYPES.contains(job.getSourceType().toUpperCase())? "export": "import");

    private static final JobParamDefine<MODE_TYPE> MODE_ENUM = JobParams.define("sqoop.mode.enum", (BiFunction<String, SubExchangisJob, MODE_TYPE>) (k, job) -> SUPPORT_BIG_DATA_TYPES.contains(job.getSourceType().toUpperCase())? EXPORT: IMPORT);
    /**
     * Sqoop RDBMS mode params
     */
    private static final JobParamDefine<JobParamSet> MODE_RDBMS_PARAMS = JobParams.define("sqoop.mode.rdbms.params", (BiFunction<String, SubExchangisJob, JobParamSet>) (k, job) -> {
        MODE_TYPE modeParam = MODE_ENUM.getValue(job);
        return modeParam.equals(IMPORT)? job.getRealmParams(REALM_JOB_CONTENT_SOURCE) : job.getRealmParams(REALM_JOB_CONTENT_SINK);
    });

    /**
     * Sqoop hadoop mode params
     */
    private static final JobParamDefine<JobParamSet> MODE_HADOOP_PARAMS = JobParams.define("sqoop.mode.hadoop.params", (BiFunction<String, SubExchangisJob, JobParamSet>) (k, job) -> {
        MODE_TYPE modeParam = MODE_ENUM.getValue(job);
        return modeParam.equals(IMPORT)? job.getRealmParams(REALM_JOB_CONTENT_SINK) : job.getRealmParams(REALM_JOB_CONTENT_SOURCE);
    });
    /**
     * //TODO Get the file type from service
     * Whether hcatalog
     */
    private static final JobParamDefine<Boolean> IS_USE_HCATALOG = JobParams.define("sqoop.use.hcatalog", (BiFunction<String, SubExchangisJob, Boolean>)(k, job) ->{
//        getCurrentBuilderContext().getMetadataInfoService().getTableProps()
        return true;
    });

    /**
     * Driver default 'com.mysql.jdbc.Driver'
     */
    private static final JobParamDefine<String> CONNECT_DRIVER = JobParams.define("sqoop.args.driver", (BiFunction<String, SubExchangisJob, String>)(k, job) -> "com.mysql.jdbc.Driver");

    /**
     * Protocol
     */
    private static final JobParamDefine<String> CONNECT_PROTOCOL = JobParams.define("sqoop.args.protocol", () -> "jdbc:mysql://%s:%s/%s");

    /**
     * Number of mapper
     */
    private static final JobParamDefine<Integer> NUM_MAPPERS = JobParams.define("sqoop.args.num.mappers", (BiFunction<String, SubExchangisJob, Integer>) (k, job) -> {
        int numMappers = 1;
        JobParamSet settings = job.getRealmParams(REALM_JOB_SETTINGS);
        JobParam<?> parallel = settings.get(JobParamConstraints.SETTINGS_MAX_PARALLEL);
        if (Objects.nonNull(parallel)){
            Object value = parallel.getValue();
            try {
                return Integer.parseInt(String.valueOf(value));
            }catch( NumberFormatException exception){
                //Ignore
            }
        }
        return numMappers;
    });

    /**
     * Connect string
     */
    private static final JobParamDefine<String> CONNECT_STRING = JobParams.define("sqoop.args.connect", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        JobParamSet paramSet = MODE_RDBMS_PARAMS.getValue(job);
        String host = paramSet.get(JobParamConstraints.HOST, String.class).getValue();
        String database = paramSet.get(JobParamConstraints.DATABASE, String.class).getValue();
        JobParam<String> connectParams = paramSet.get(JobParamConstraints.CONNECT_PARAMS, String.class);
        Map<String, String> paramsMap = null;
        if (Objects.nonNull(connectParams)){
            paramsMap = Json.fromJson(connectParams.getValue(), Map.class);
        }
        Integer port = Integer.parseInt(String.valueOf(paramSet.get(JobParamConstraints.PORT).getValue()));
        String connectStr =  String.format(CONNECT_PROTOCOL.getValue(job), host, port, database);
        if (Objects.nonNull(paramsMap) && !paramsMap.isEmpty()){
            connectStr += "?" + paramsMap.entrySet().stream().map(entry -> {
                try {
                    return entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }).filter(StringUtils::isNotBlank).collect(Collectors.joining("&"));
        }
        return connectStr;
    });

    /**
     * Username
     */
    private static final JobParamDefine<String> USERNAME = JobParams.define("sqoop.args.username", (BiFunction<String, SubExchangisJob, String>) (k, job) ->
            MODE_RDBMS_PARAMS.getValue(job).get(JobParamConstraints.USERNAME, String.class).getValue());

    /**
     * Password
     */
    private static final JobParamDefine<String> PASSWORD = JobParams.define("sqoop.args.password", (BiFunction<String, SubExchangisJob, String>) (k, job) ->
            MODE_RDBMS_PARAMS.getValue(job).get(JobParamConstraints.PASSWORD, String.class).getValue());

    /**
     * Table
     */
    private static final JobParamDefine<String> TABLE = JobParams.define("sqoop.args.table", (BiFunction<String, SubExchangisJob, String>) (k, job) ->
            MODE_RDBMS_PARAMS.getValue(job).get(JobParamConstraints.TABLE, String.class).getValue());

    /**
     * Import: Query string in params, //TODO where to use query
     */
    private static final JobParamDefine<String> QUERY_STRING = JobParams.define("sqoop.args.query", "query");

    /**
     * Import: Where
     */
    private static final JobParamDefine<String> WHERE_CLAUSE = JobParams.define("sqoop.args.where", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == MODE_TYPE.IMPORT){
            JobParam<String> where = MODE_RDBMS_PARAMS.getValue(job).get(JobParamConstraints.WHERE);
            if (Objects.nonNull(where) && StringUtils.isNotBlank(where.getValue())){
                return where.getValue();
            }
        }
        return null;
    });


    /**
     * Import: Hive-import
     */
    private static final JobParamDefine<String> HIVE_IMPORT = JobParams.define("sqoop.args.hive.import", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == IMPORT && job.getSinkType().equalsIgnoreCase("hive") && !IS_USE_HCATALOG.getValue(job)){
            return "";
        }
        return null;
    });

    /**
     * Export: Hive-export
     */
    private static final JobParamDefine<String> HIVE_EXPORT = JobParams.define("sqoop.hive.export", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == EXPORT && job.getSourceType().equalsIgnoreCase("hive") && !IS_USE_HCATALOG.getValue(job)){
            return "";
        }
        return null;
    });

    /**
     * Import: Hive-overwrite
     */
    private static final JobParamDefine<String> HIVE_OVERWRITE = JobParams.define("sqoop.args.hive.overwrite", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HIVE_IMPORT.getValue(job))){
            JobParam<String> writeMode = MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.WRITE_MODE);
            if (Objects.nonNull(writeMode) && "overwrite".equals(writeMode.getValue())){
                return "";
            }
        }
        return null;
    });

    /**
     * Hive-partition-map
     */
    @SuppressWarnings("unchecked")
    private static final JobParamDefine<Map<String, String>> PARTITION_MAP = JobParams.define("sqoop.partition.map", (BiFunction<String, SubExchangisJob, Map<String, String>>) (k, job) -> {
        if ("hive".equalsIgnoreCase(job.getSinkType()) || "hive".equalsIgnoreCase(job.getSourceType())){
            JobParam<?> partitionParam = MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.PARTITION);
            if (Objects.nonNull(partitionParam)) {
                Object partition = partitionParam.getValue();
                if (partition instanceof Map) {
                    return (Map<String, String>) partition;
                } else {
                    String partitionStr = String.valueOf(partition);
                    if (StringUtils.isNotBlank(partitionStr)){
                        Map<String, String> partitionMap = new HashMap<>();
                        String[] partValues = partitionStr.split(",");
                        for (String partValue : partValues){
                            String[] parts = partValue.split("=");
                            if (parts.length == 2){
                                partitionMap.put(parts[0], parts[1]);
                            }
                        }
                        return partitionMap;
                    }
                }
            }
        }
        return null;
    });

    /**
     * Import: Hive-database
     */
    private static final JobParamDefine<String> HIVE_DATABASE = JobParams.define("sqoop.args.hive.database", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HIVE_IMPORT.getValue(job))){
            return MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.DATABASE, String.class).getValue();
        }
       return null;
    });

    /**
     * Import: Hive-table
     */
    private static final JobParamDefine<String> HIVE_TABLE = JobParams.define("sqoop.args.hive.table", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
       if (Objects.nonNull(HIVE_DATABASE.getValue(job))) {
           return MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.TABLE, String.class).getValue();
       }
       return null;
    });

    /**
     * Import: Hive-partition-key
     */
    private static final JobParamDefine<String> HIVE_PARTITION_KEY = JobParams.define("sqoop.args.hive.partition.key", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        AtomicReference<String> keys = new AtomicReference<>(null);
        if (Objects.nonNull(HIVE_TABLE.getValue(job))){
            Optional.ofNullable(PARTITION_MAP.getValue(job)).ifPresent(partitionMap -> keys.set(StringUtils.join(partitionMap.keySet(), ",")));
        }
        return keys.get();
    });

    /**
     * Import: Hive-partition-values
     */
    private static final JobParamDefine<String> HIVE_PARTITION_VALUE = JobParams.define("sqoop.args.hive.partition.value", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
       if (Objects.nonNull(HIVE_PARTITION_KEY.getValue(job))){
           return StringUtils.join(PARTITION_MAP.getValue(job).values(), ",");
       }
       return null;
    });

    /**
     * Import: Hive-append
     */
    private static final JobParamDefine<String> HIVE_APPEND = JobParams.define("sqoop.args.append", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HIVE_IMPORT.getValue(job))){
            JobParam<String> writeMode = MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.WRITE_MODE);
            if (Objects.nonNull(writeMode) && "append".equalsIgnoreCase(writeMode.getValue())){
                return "";
            }
        }
       return null;
    });
    /**
     * Import: Hive-target-dir
     */
    private static final JobParamDefine<String> HIVE_TARGET_DIR = JobParams.define("sqoop.args.target.dir", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HIVE_IMPORT.getValue(job)) && Objects.nonNull(QUERY_STRING.getValue(job))){
            return "/user/linkis/exchangis/sqoop/" + HIVE_TABLE.getValue(job) + "/";
        }
        return null;
    });

    /**
     * Import: Hive-delete-target-dir
     */
    private static final JobParamDefine<String> HIVE_DELETE_TARGET = JobParams.define("sqoop.args.delete.target.dir", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HIVE_IMPORT.getValue(job)) && Objects.isNull(HIVE_APPEND.getValue(job))){
            return "";
        }
       return null;
    });

    /**
     * TODO get the properties from hive
     * Import: Hive-fields-terminated-by
     */
    private static final JobParamDefine<String> HIVE_FIELDS_TERMINATED_BY = JobParams.define("sqoop.args.fields.terminated.by", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == IMPORT && "hive".equalsIgnoreCase(job.getSinkType())){
            return "\u0001";
        }
        return null;
    });

    /**
     * TODO get the properties from hive
     * Import: Hive-null-string
     */
    private static final JobParamDefine<String> HIVE_NULL_STRING = JobParams.define("sqoop.args.null.string", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == IMPORT && "hive".equalsIgnoreCase(job.getSinkType())){
            return "\\\\N";
        }
        return null;
    });

    /**
     * TODO get the properties from hive
     * Import: Hive-null-non-string
     */
    private static final JobParamDefine<String> HIVE_NULL_NON_STRING = JobParams.define("sqoop.args.null.non.string", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == IMPORT && "hive".equalsIgnoreCase(job.getSinkType())){
            return "\\\\N";
        }
        return null;
    });

    /**
     * TODO get the properties from hive to build the export directory
     * Export: Export-dir
     */
    private static final JobParamDefine<String> EXPORT_DIR = JobParams.define("sqoop.args.export.dir", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HIVE_EXPORT.getValue(job))){

        }
        return null;
    });

    /**
     * TODO get the primary keys from RDBMS
     * Export: Update-key
     */
    private static final JobParamDefine<String> UPDATE_KEY = JobParams.define("sqoop.args.update.key", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == EXPORT ){
            JobParam<String> writeMode = MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.WRITE_MODE, String.class);
            if (Objects.nonNull(writeMode) && StringUtils.isNotBlank(writeMode.getValue()) && !"insert".equalsIgnoreCase(writeMode.getValue())){

            }
        }
        return null;
    });

    /**
     * Export: Update mode
     */
    private static final JobParamDefine<String> UPDATE_MODE = JobParams.define("sqoop.args.update.mode", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(UPDATE_KEY.getValue(job))){
            JobParam<String> writeMode = MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.WRITE_MODE, String.class);
            return "update".equals(writeMode.getValue())? "allowinsert" : "updateonly";
        }
        return null;
    });

    /**
     * Export: Hcatalog-database
     */
    private static final JobParamDefine<String> HCATALOG_DATABASE = JobParams.define("sqoop.args.hcatalog.database", (BiFunction<String, SubExchangisJob, String>) (k, job) ->{
        if (IS_USE_HCATALOG.getValue(job)){
            return MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.DATABASE, String.class).getValue();
        }
        return null;
    });

    /**
     * Export: Hcatalog-table
     */
    private static final JobParamDefine<String> HCATALOG_TABLE = JobParams.define("sqoop.args.hcatalog.table", (BiFunction<String, SubExchangisJob, String>) (k, job) ->{
        if (Objects.nonNull(HCATALOG_DATABASE.getValue(job))){
            return MODE_HADOOP_PARAMS.getValue(job).get(JobParamConstraints.TABLE, String.class).getValue();
        }
        return null;
    });

    /**
     * Export: Hcatalog-partition-key
     */
    private static final JobParamDefine<String> HCATALOG_PARTITION_KEY = JobParams.define("sqoop.args.hcatalog.partition.keys", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        AtomicReference<String> keys = new AtomicReference<>(null);
        if (Objects.nonNull(HCATALOG_TABLE.getValue(job))){
            Optional.ofNullable(PARTITION_MAP.getValue(job)).ifPresent(partitionMap -> keys.set(StringUtils.join(partitionMap.keySet(), ",")));
        }
        return keys.get();
    });

    /**
     * Export: Hcatalog-partition-values
     */
    private static final JobParamDefine<String> HCATALOG_PARTITION_VALUE = JobParams.define("sqoop.args.hcatalog.partition.values", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (Objects.nonNull(HCATALOG_PARTITION_KEY.getValue(job))){
            return StringUtils.join(PARTITION_MAP.getValue(job).values(), ",");
        }
        return null;
    });

    /**
     * TODO get the properties from hive
     * Export: Hive-input-fields-terminated-by
     */
    private static final JobParamDefine<String> HIVE_INPUT_FIELDS_TERMINATED_KEY = JobParams.define("sqoop.args.input.fields.terminated.by", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == EXPORT && "hive".equalsIgnoreCase(job.getSourceType())){
            return "\u0001";
        }
        return null;
    });

    /**
     * TODO get the properties from hive
     * Export: Hive-input-null-string
     */
    private static final JobParamDefine<String> HIVE_INPUT_NULL_STRING = JobParams.define("sqoop.args.input.null.string", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == EXPORT && "hive".equalsIgnoreCase(job.getSourceType())){
            return "\\\\N";
        }
        return null;
    });

    /**
     * TODO get the properties from hive
     * Export: Hive-input-null-non-string
     */
    private static final JobParamDefine<String> HIVE_INPUT_NULL_NON_STRING = JobParams.define("sqoop.args.input.null.non.string", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        if (MODE_ENUM.getValue(job) == EXPORT && "hive".equalsIgnoreCase(job.getSourceType())){
            return "\\\\N";
        }
        return null;
    });


    /**
     * Mapping params (ExchangisJobContent -> transforms -> mapping)
     */
    private static final JobParamDefine<List<Map<String, Object>>> TRANSFORM_MAPPING = JobParams.define("sqoop.transform.mapping", "mapping");
    /**
     * Source field name in mapping
     */
    private static final JobParamDefine<String> SOURCE_FIELD_NAME = JobParams.define("sqoop.source.name", "source_field_name", String.class);
    /**
     * Sink field name in mapping
     */
    private static final JobParamDefine<String> SINK_FIELD_NAME = JobParams.define("sqoop.sink.name", "sink_field_name", String.class);

    /**
     * Column serializer
     */
    private static final JobParamDefine<String> COLUMN_SERIAL = JobParams.define("sqoop.args.columns", (BiFunction<String, SubExchangisJob, String>) (key, job) -> {
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.getValue(job.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING));
        List<String> columnSerial = new ArrayList<>();
        if (Objects.nonNull(mappings)) {
            if (SUPPORT_RDBMS_TYPES.contains(job.getSourceType().toUpperCase())) {
                mappings.forEach(mapping -> Optional.ofNullable(SOURCE_FIELD_NAME.newParam(mapping).getValue()).ifPresent(columnSerial::add));
            } else if (SUPPORT_RDBMS_TYPES.contains(job.getSinkType().toUpperCase())) {
                mappings.forEach(mapping -> Optional.ofNullable(SINK_FIELD_NAME.newParam(mapping).getValue()).ifPresent(columnSerial::add));
            }
        }
        return StringUtils.join(columnSerial, ",");
    });

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public ExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectOut, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        try {
            SqoopExchangisEngineJob engineJob = new SqoopExchangisEngineJob();
            engineJob.setId(inputJob.getId());
            JobParamDefine<?>[] definitions = getParamDefinitions();
            Map<String, Object> jobContent = engineJob.getJobContent();
            for (JobParamDefine<?> definition : definitions){
                Object paramValue = definition.getValue(inputJob);
                if (Objects.nonNull(paramValue)){
                    jobContent.put(definition.getKey(), String.valueOf(paramValue));
                }
            }
            engineJob.setRuntimeParams(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_SETTINGS, false));
            engineJob.setName(inputJob.getName());
            if (Objects.nonNull(expectOut)) {
                engineJob.setName(expectOut.getName());
                engineJob.setEngineType(expectOut.getEngineType());
                jobContent.putAll(expectOut.getJobContent());
            }
            engineJob.setCreateUser(inputJob.getCreateUser());
            return engineJob;
        } catch (Exception e) {
            throw new ExchangisJobException(ExchangisJobExceptionCode.ENGINE_JOB_ERROR.getCode(),
                    "Fail to build sqoop engine job, message:[" + e.getMessage() + "]", e);
        }
    }

    @Override
    public boolean canBuild(SubExchangisJob inputJob) {
        return "sqoop".equalsIgnoreCase(inputJob.getEngineType());
    }

    /**
     * Definition list
     * @return list
     */
    private JobParamDefine<?>[] getParamDefinitions(){
        return new JobParamDefine<?>[]{
                VERBOSE, MODE, CONNECT_DRIVER, CONNECT_STRING, NUM_MAPPERS,
                USERNAME, PASSWORD, TABLE, WHERE_CLAUSE, HIVE_IMPORT, HIVE_OVERWRITE,
                HIVE_DATABASE, HIVE_TABLE, HIVE_PARTITION_KEY, HIVE_PARTITION_VALUE, HIVE_APPEND,
                HIVE_TARGET_DIR, HIVE_DELETE_TARGET, HIVE_FIELDS_TERMINATED_BY, HIVE_NULL_STRING, HIVE_NULL_NON_STRING,
                EXPORT_DIR, UPDATE_KEY, UPDATE_MODE,
                HCATALOG_DATABASE, HCATALOG_TABLE, HCATALOG_PARTITION_KEY, HCATALOG_PARTITION_VALUE,
                HIVE_INPUT_FIELDS_TERMINATED_KEY, HIVE_INPUT_NULL_STRING, HIVE_INPUT_NULL_NON_STRING,
                COLUMN_SERIAL
        };
    }
}
