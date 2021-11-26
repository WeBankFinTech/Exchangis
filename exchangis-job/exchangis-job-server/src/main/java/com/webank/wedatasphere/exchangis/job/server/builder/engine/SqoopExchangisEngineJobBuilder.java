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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;

import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SINK;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_CONTENT_SOURCE;
import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.REALM_JOB_SETTINGS;

public class SqoopExchangisEngineJobBuilder extends AbstractExchangisJobBuilder<SubExchangisJob, ExchangisEngineJob> {

    private static final Logger LOG = LoggerFactory.getLogger(SqoopExchangisEngineJobBuilder.class);

    /**
     * Mapping params
     */
    private static final JobParamDefine<List<Map<String, Object>>> TRANSFORM_MAPPING = JobParams.define("mapping");
    private static final JobParamDefine<String> SOURCE_FIELD_NAME = JobParams.define("name", "source_field_name", String.class);
    private static final JobParamDefine<String> SINK_FIELD_NAME = JobParams.define("name", "sink_field_name", String.class);
    private static final JobParamDefine<String> QUERY_STRING = JobParams.define("sqoop.args.query");

    private static final JobParamDefine<String> COLUMN_SERIAL = JobParams.define("sqoop.args.columns", (BiFunction<String, DataSourceJobParamSet, String>) (key, paramSet) -> {
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet.jobParamSet).getValue();
        List<String> columnSerial = new ArrayList<>();
        if (Objects.nonNull(mappings)) {
            if ("mysql".equalsIgnoreCase(paramSet.sourceType)) {
                mappings.forEach(mapping -> Optional.ofNullable(SOURCE_FIELD_NAME.newParam(mapping).getValue()).ifPresent(columnSerial::add));
            } else if ("mysql".equalsIgnoreCase(paramSet.sinkType)) {
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

    private static final JobParamDefine<Map<String, Object>> HIVE_TABLE = JobParams.define("hive.table", (BiFunction<String, SubExchangisJob, Map<String, Object>>) (k, job) -> {
        Map<String, Object> params = new HashMap<>();

        JobParamSet settings = job.getRealmParams(job.getSourceType().equalsIgnoreCase("HIVE") ? REALM_JOB_CONTENT_SOURCE : REALM_JOB_CONTENT_SINK);

        if (job.getSourceType().equalsIgnoreCase("HIVE")) {
            if (null != settings.get("exchangis.job.ds.params.sqoop.hive.r.trans_proto")) {
                if (null != settings.get("exchangis.job.ds.params.sqoop.hive.r.trans_proto").getValue()) {
                    if (!settings.get("exchangis.job.ds.params.sqoop.hive.r.trans_proto").getValue().toString().equals("记录")) {
                        return params;
                    }
                }
            }
        }

        if (job.getSinkType().equalsIgnoreCase("HIVE")) {
            if (null != settings.get("exchangis.job.ds.params.sqoop.hive.w.trans_proto")) {
                if (null != settings.get("exchangis.job.ds.params.sqoop.hive.w.trans_proto").getValue()) {
                    if (!settings.get("exchangis.job.ds.params.sqoop.hive.w.trans_proto").getValue().toString().equals("记录")) {
                        return params;
                    }
                }
            }
        }

        params.put("sqoop.args.fields.terminated.by", settings.get(job.getSourceType().equalsIgnoreCase("HIVE") ? "exchangis.job.ds.params.sqoop.hive.r.row_format" : "exchangis.job.ds.params.sqoop.hive.w.row_format").getValue().toString());
        params.put("sqoop.args.hive.database", settings.get("database").getValue().toString());
        params.put("sqoop.args.hive.table", settings.get("table").getValue().toString());
        if (job.getSinkType().equalsIgnoreCase("HIVE")) {
            params.put("sqoop.args.hive.import", "");
            params.put("sqoop.args.hive.overwrite", "");
        }

        JobParam<?> partitionSetting = settings.get(job.getSourceType().equalsIgnoreCase("HIVE") ? "exchangis.job.ds.params.sqoop.hive.r.partition" : "exchangis.job.ds.params.sqoop.hive.w.partition");
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

        return params;
    });

    private static final JobParamDefine<String> NUM_MAPPERS = JobParams.define("sqoop.args.num.mappers", (BiFunction<String, JobParamSet, String>) (k, settings) -> {
        String numMappers = "1";
        if (null != settings.get("exchangis.sqoop.setting.max.parallelism")) {
            JobParam<?> jobParam = settings.get("exchangis.sqoop.setting.max.parallelism");
            if (null != jobParam) {
                Object value = jobParam.getValue();
                if (null != value && StringUtils.isNotBlank(value.toString())) {
                    return value.toString();
                }
            }
        }
        return numMappers;
    });

    private static final JobParamDefine<String> MODE = JobParams.define("sqoop.mode", (BiFunction<String, SubExchangisJob, String>) (k, job) -> {
        return job.getSourceType().equalsIgnoreCase("HIVE") ? "export" : "import";
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
    public ExchangisEngineJob buildJob(SubExchangisJob inputJob, ExchangisEngineJob expectJob, ExchangisJobBuilderContext ctx) throws ExchangisJobException {
        try {
            SqoopExchangisEngineJob engineJob = new SqoopExchangisEngineJob();
            engineJob.setId(inputJob.getId());
            // engineJob.getJobContent().put("", "");
            // pass-through the params
            Map<String, Object> sqoopParams = new HashMap<>();

            JobParamSet jobSettings = inputJob.getRealmParams(REALM_JOB_SETTINGS);
            JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);
            JobParamSet sinkSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SINK);

            String mode = MODE.newParam(inputJob).getValue();
            sqoopParams.put("sqoop.mode", mode);
            sqoopParams.put("sqoop.args.verbose", "");
            sqoopParams.put("sqoop.args.num.mappers", NUM_MAPPERS.newParam(jobSettings).getValue());

            if (RDBMS.newParam(inputJob).getValue().equalsIgnoreCase("MYSQL")) {

                JobParamSet rdbmsSettings = null;
                if (inputJob.getSourceType().equalsIgnoreCase("MYSQL")) {
                    rdbmsSettings = sourceSettings;
                    if (null != sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition"))
                        if (StringUtils.isNotBlank(sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition").toString())) {
                            sqoopParams.put("sqoop.args.where", sourceSettings.get("exchangis.job.ds.params.sqoop.mysql.r.where_condition").toString());
                        }
                } else {
                    rdbmsSettings = sinkSettings;
                }
                String connectProtocol = "jdbc:mysql://";
                Object host = ctx.getDatasourceParam(rdbmsSettings.get("datasource").getValue().toString()).get("host");
                Object port = ctx.getDatasourceParam(rdbmsSettings.get("datasource").getValue().toString()).get("port");
                Object database = rdbmsSettings.get("database").getValue();
                sqoopParams.put("sqoop.args.connect", String.format("%s%s:%s/%s", connectProtocol, host, port, database));
                sqoopParams.put("sqoop.args.username", ctx.getDatasourceParam(rdbmsSettings.get("datasource").getValue().toString()).get("username"));
                sqoopParams.put("sqoop.args.password", ctx.getDatasourceParam(rdbmsSettings.get("datasource").getValue().toString()).get("password"));
                sqoopParams.put("sqoop.args.table", rdbmsSettings.get("table").getValue().toString());
            }

            if (mode.equals("export")) {
                sqoopParams.putAll(HIVE_HCATALOG_EXPORT.newParam(inputJob).getValue());
            }

            if (mode.equals("import")) {
                sqoopParams.put("sqoop.args.target.dir", TARGET_DIR.newParam(sinkSettings.get("table")).getValue());
                sqoopParams.put("sqoop.args.delete.target.dir", "");
            }

            sqoopParams.putAll(HIVE_TABLE.newParam(inputJob).getValue());

            sqoopParams.putAll(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_SETTINGS, false));
            // sqoopParams.putAll(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_CONTENT_SINK, false));
            // sqoopParams.putAll(inputJob.getParamsToMap(REALM_JOB_CONTENT_SOURCE, false));
            resolveTransformMappings(inputJob, inputJob.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING), sqoopParams);

            engineJob.getJobContent().put("sqoop-params", sqoopParams);

            engineJob.setTaskName(inputJob.getTaskName());
            if (Objects.nonNull(expectJob)) {
                engineJob.setJobName(expectJob.getJobName());
                engineJob.setRuntimeParams(expectJob.getRuntimeParams());
                engineJob.setEngine(expectJob.getEngine());
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
        return "sqoop".equalsIgnoreCase(inputJob.getEngine());
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
