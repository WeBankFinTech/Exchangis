package com.webank.wedatasphere.exchangis.job.server.builder.engine;

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

import java.util.*;
import java.util.function.BiFunction;

import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SINK;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SOURCE;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_SETTINGS;
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
    private static final JobParamDefine<String> MODE = JobParams.define("sqoop.mode", (BiFunction<String, SubExchangisJob, String>) (k, job) -> SUPPORT_BIG_DATA_TYPES.contains(job.getSourceType().toUpperCase())? "import": "export");

    /**
     * Driver default 'com.mysql.jdbc.Driver'
     */
    private static final JobParamDefine<String> CONNECT_DRIVER = JobParams.define("sqoop.args.driver", (BiFunction<String, SubExchangisJob, String>)(k, job) -> "com.mysql.jdbc.Driver");

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
        JobParam<String> modeParam = MODE.get(job);
        if (StringUtils.isNotBlank(modeParam.getValue())){
            MODE_TYPE mode = MODE_TYPE.valueOf(modeParam.getValue().toUpperCase());
            JobParamSet paramSet = mode.equals(IMPORT)? job.getRealmParams(REALM_JOB_CONTENT_SOURCE) : job.getRealmParams(REALM_JOB_CONTENT_SINK);
            String host = (String)paramSet.get(JobParamConstraints.HOST).getValue();
            String database = (String)paramSet.get(JobParamConstraints.DATABASE).getValue();
            Integer port = Integer.parseInt(String.valueOf(paramSet.get(JobParamConstraints.PORT).getValue()));
            return String.format(CONNECT_PROTOCOL.get(job).getValue(), host, port, database);
        }
        return null;
    });
    /**
     * Mapping params (ExchangisJobContent -> transforms -> mapping)
     */
    private static final JobParamDefine<List<Map<String, Object>>> TRANSFORM_MAPPING = JobParams.define("mapping");
    /**
     * Source field name in mapping
     */
    private static final JobParamDefine<String> SOURCE_FIELD_NAME = JobParams.define("name", "source_field_name", String.class);
    /**
     * Sink field name in mapping
     */
    private static final JobParamDefine<String> SINK_FIELD_NAME = JobParams.define("name", "sink_field_name", String.class);

    /**
     * Query string in params
     */
    private static final JobParamDefine<String> QUERY_STRING = JobParams.define("sqoop.args.query", "query");

    /**
     * Column serializer
     */
    private static final JobParamDefine<String> COLUMN_SERIAL = JobParams.define("sqoop.args.columns", (BiFunction<String, SubExchangisJob, String>) (key, job) -> {
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.get(job.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING)).getValue();
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


    private static final JobParamDefine<Map<String, Object>> HIVE_HCATALOG_EXPORT = JobParams.define("sqoop.hcatalog", (BiFunction<String, SubExchangisJob, Map<String, Object>>) (k, job) -> {
        Map<String, Object> params = new HashMap<>();
        if (!job.getSourceType().equalsIgnoreCase("HIVE")) {
            return params;
        }
        JobParamSet jobParams = job.getRealmParams(REALM_JOB_CONTENT_SOURCE);

        if (null == jobParams.get("exchangis.job.ds.params.sqoop.hive.r.trans_proto")) {
            return params;
        }
        if (null == jobParams.get("exchangis.job.ds.params.sqoop.hive.r.trans_proto").getValue()) {
            return params;
        }
        if (!jobParams.get("exchangis.job.ds.params.sqoop.hive.r.trans_proto").getValue().toString().equals("二进制")) {
            return params;
        }

        params.put("sqoop.args.hcatalog.database", jobParams.get("database").getValue());
        params.put("sqoop.args.hcatalog.table", jobParams.get("table").getValue());

        Optional.ofNullable(jobParams.get("exchangis.job.ds.params.sqoop.hive.r.partition")).ifPresent(setting -> {
            Optional.ofNullable(setting.getValue()).ifPresent(partition -> {
                String[] _partition = partition.toString().split("=");
                if (_partition.length >= 2) {
                    params.put("sqoop.args.hcatalog.partition.keys", _partition[0]);
                    params.put("sqoop.args.hcatalog.partition.values", _partition[1]);
                }
            });
        });

        return params;
    });



    private static final JobParamDefine<String> RDBMS = JobParams.define("rdbms.type", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        return job.getSourceType().equalsIgnoreCase("HIVE") ? job.getSinkType() : job.getSourceType();
    });

    private static final JobParamDefine<String> TARGET_DIR = JobParams.define("sqoop.args.target.dir", (BiFunction<String, String, String>) (k, table) -> {
        return "/user/linkis/exchangis/sqoop/" + table + "/";
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

            Map<String, Object> sqoopParams = new HashMap<>();

            JobParamSet jobSettings = inputJob.getRealmParams(REALM_JOB_SETTINGS);
            JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);
            JobParamSet sinkSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SINK);

            String mode = MODE.newParam(inputJob).getValue();
            sqoopParams.put("sqoop.mode", mode);
            sqoopParams.put("sqoop.args.verbose", "");
            sqoopParams.put("sqoop.args.num.mappers", NUM_MAPPERS.newParam(jobSettings).getValue());

            if (mode.equals("import")) {
                Map<String, Object> importParams = this.buildImportParam(inputJob, expectOut, ctx, jobSettings, sourceSettings, sinkSettings);
                sqoopParams.putAll(importParams);
            }

            if (mode.equals("export")) {
                Map<String, Object> exportParams = this.buildExportParam(inputJob, expectOut, ctx, jobSettings, sourceSettings, sinkSettings);
                sqoopParams.putAll(exportParams);
            }


            resolveTransformMappings(inputJob, inputJob.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING), sqoopParams);

            engineJob.getJobContent().put("sqoop-params", sqoopParams);
            engineJob.setRuntimeParams(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_SETTINGS, false));
            engineJob.setName(inputJob.getName());
            if (Objects.nonNull(expectOut)) {
                engineJob.setName(expectOut.getName());
                engineJob.setEngineType(expectOut.getEngineType());
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
     * Resolve transform jobParams
     *
     * @param transformJobParamSet param set
     */
    private void resolveTransformMappings(SubExchangisJob subExchangisJob, JobParamSet transformJobParamSet, Map<String, Object> sqoopParams) {
        String queryString = QUERY_STRING.newParam(sqoopParams).getValue();
        if (StringUtils.isBlank(queryString)) {
            DataSourceJobParamSet dataSourceJobParamSet = new DataSourceJobParamSet();
            dataSourceJobParamSet.sourceType = subExchangisJob.getSourceType();
            dataSourceJobParamSet.sinkType = subExchangisJob.getSinkType();
            dataSourceJobParamSet.jobParamSet = transformJobParamSet;
            String column = COLUMN_SERIAL.newParam(dataSourceJobParamSet).getValue();
            sqoopParams.put(COLUMN_SERIAL.getKey(), column);
        }
    }

    private Map<String, Object> buildImportParam(SubExchangisJob inputJob, ExchangisEngineJob expectJob, ExchangisJobBuilderContext ctx, JobParamSet jobSettings, JobParamSet sourceSettings, JobParamSet sinkSettings) {
        Map<String, Object> params = new HashMap<>();

        if (RDBMS.newParam(inputJob).getValue().equalsIgnoreCase("MYSQL")) {
            params.put("sqoop.args.driver", "com.mysql.jdbc.Driver");
            String connectProtocol = "jdbc:mysql://";
            Object host = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("host");
            Object port = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("port");
            Object database = sourceSettings.get("database").getValue();
            params.put("sqoop.args.connect", String.format("%s%s:%s/%s", connectProtocol, host, port, database));
        }

        params.put("sqoop.args.username", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("username"));
        params.put("sqoop.args.password", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("password"));
        params.put("sqoop.args.table", sourceSettings.get("table").getValue().toString());

        if (null != sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition")
              && null != sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition").getValue()
              && StringUtils.isNotBlank(sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition").getValue().toString())) {
            params.put("sqoop.args.where", sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition").getValue().toString());
        }

        params.put("sqoop.args.hive.import", "");
        params.put("sqoop.args.hive.overwrite", "");

//        params.put("sqoop.args.null.string", "");
//        params.put("sqoop.args.null.non.string", "");

        params.put("sqoop.args.hive.database", sinkSettings.get("database").getValue().toString());
        params.put("sqoop.args.hive.table", sinkSettings.get("table").getValue().toString());


        if (null != sinkSettings.get("exchangis.job.ds.params.sqoop.hive.w.row_format")
                && null != sinkSettings.get("exchangis.job.ds.params.sqoop.hive.w.row_format").getValue()
                && !StringUtils.isBlank(sinkSettings.get("exchangis.job.ds.params.sqoop.hive.w.row_format").getValue().toString())) {
            params.put("sqoop.args.fields.terminated.by", sinkSettings.get("exchangis.job.ds.params.sqoop.hive.w.row_format").getValue().toString());
        }

        JobParam<?> partitionSetting = sinkSettings.get("exchangis.job.ds.params.sqoop.hive.w.partition");
        if (null != partitionSetting) {
            Object partitionValue = partitionSetting.getValue();
            if (null != partitionValue) {
                String partition = partitionValue.toString();
                if (StringUtils.isNotBlank(partition)) {
                    String[] _partition = partition.split("=");
                    if (_partition.length >= 2) {
                        params.put("sqoop.args.hive.partition.key", _partition[0]);
                        params.put("sqoop.args.hive.partition.value", _partition[1]);
                    }
                }
            }
        }

        params.put("sqoop.args.target.dir", TARGET_DIR.newParam(sinkSettings.get("table").getValue()).getValue());
        params.put("sqoop.args.delete.target.dir", "");

        return params;
    }

    private Map<String, Object> buildExportParam(SubExchangisJob inputJob, ExchangisEngineJob expectJob, ExchangisJobBuilderContext ctx, JobParamSet jobSettings, JobParamSet sourceSettings, JobParamSet sinkSettings) {
        Map<String, Object> params = new HashMap<>();

        if (RDBMS.newParam(inputJob).getValue().equalsIgnoreCase("MYSQL")) {
            params.put("sqoop.args.driver", "com.mysql.jdbc.Driver");
            String connectProtocol = "jdbc:mysql://";
            Object host = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("host");
            Object port = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("port");
            Object database = sinkSettings.get("database").getValue();
            params.put("sqoop.args.connect", String.format("%s%s:%s/%s", connectProtocol, host, port, database));
        }

        params.put("sqoop.args.username", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("username"));
        params.put("sqoop.args.password", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("password"));
        params.put("sqoop.args.table", sinkSettings.get("table").getValue().toString());

        params.put("sqoop.args.update.mode", sinkSettings.get("exchangis.job.ds.params.sqoop.mysql.w.write_type").getValue().toString().toLowerCase(Locale.ROOT));
        // params.put("sqoop.args.update.key", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("primary-keys"));
        JobParam<?> partitionSetting = sourceSettings.get("exchangis.job.ds.params.sqoop.hive.r.partition");
        if (null != partitionSetting) {
            Object partitionValue = partitionSetting.getValue();
            if (null != partitionValue) {
                String partition = partitionValue.toString();
                if (StringUtils.isNotBlank(partition)) {
                    String[] _partition = partition.split("=");
                    if (_partition.length >= 2) {
                        params.put("sqoop.args.hive.partition.key", _partition[0]);
                        params.put("sqoop.args.hive.partition.value", _partition[1]);
                    }
                }
            }
        }

        if (null != sinkSettings.get("exchangis.job.ds.params.sqoop.hive.r.row_format")
            && null != sinkSettings.get("exchangis.job.ds.params.sqoop.hive.r.row_format").getValue()
            && !StringUtils.isBlank(sinkSettings.get("exchangis.job.ds.params.sqoop.hive.r.row_format").getValue().toString())) {
            params.put("sqoop.args.fields.terminated.by", sinkSettings.get("exchangis.job.ds.params.sqoop.hive.r.row_format").getValue().toString());
        }
        // params.put("sqoop.args.export.dir", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("location"));

        return params;
    }

    private static class DataSourceJobParamSet {
        /**
         * type
         */
        private String sinkType;

        private String sourceType;
        /**
         * Job param set
         */
        private JobParamSet jobParamSet;
    }
}
