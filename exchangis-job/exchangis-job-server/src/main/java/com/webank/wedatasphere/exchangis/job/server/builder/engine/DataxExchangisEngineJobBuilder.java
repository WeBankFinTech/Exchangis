package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.webank.wedatasphere.linkis.manager.label.utils.LabelUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.BiFunction;

import static com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob.*;

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

    private static final JobParamDefine<List<Map<String, Object>>> SOURCE_COLUMN = JobParams.define("column", (BiFunction<String, JobParamSet, List<Map<String, Object>>>) (key, paramSet) -> {

        List<Map<String, Object>> columns = new ArrayList<>();
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet).getValue();
        if (Objects.nonNull(mappings)) {
            mappings.forEach(mapping -> {
                Map<String, Object> _mapping = new HashMap<>(mapping);
                Map<String, Object> column = new HashMap<>();
                columns.add(column);
                column.put(SOURCE_FIELD_NAME.getKey(), SOURCE_FIELD_NAME.newParam(_mapping).getValue());
                column.put(SOURCE_FIELD_TYPE.getKey(), SOURCE_FIELD_TYPE.newParam(_mapping).getValue());
                column.put(SOURCE_FIELD_INDEX.getKey(), SOURCE_FIELD_INDEX.newParam(_mapping).getValue());
            });
        }
        return columns;
    });

    private static final JobParamDefine<List<Map<String, Object>>> SINK_COLUMN = JobParams.define("column", (BiFunction<String, JobParamSet, List<Map<String, Object>>>) (key, paramSet) -> {
        List<Map<String, Object>> columns = new ArrayList<>();
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet).getValue();
        if (Objects.nonNull(mappings)) {
            mappings.forEach(mapping -> {
                Map<String, Object> _mapping = new HashMap<>(mapping);
                Map<String, Object> column = new HashMap<>();
                columns.add(column);
                column.put(SINK_FIELD_NAME.getKey(), SINK_FIELD_NAME.newParam(_mapping).getValue());
                column.put(SINK_FIELD_TYPE.getKey(), SINK_FIELD_TYPE.newParam(_mapping).getValue());
                column.put(SINK_FIELD_INDEX.getKey(), SINK_FIELD_INDEX.newParam(_mapping).getValue());
            });
        }
        return columns;
    });

    private static final JobParamDefine<List<Transformer>> TRANSFORMER = JobParams.define("column", (BiFunction<String, JobParamSet, List<Transformer>>) (key, paramSet) -> {
        List<Transformer> transformers = new ArrayList<>();
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet).getValue();
        if (Objects.nonNull(mappings)) {
            mappings.forEach(mapping -> {
                Map<String, Object> _mapping = new HashMap<>(mapping);
                int fieldIndex = SOURCE_FIELD_INDEX.newParam(_mapping).getValue();
                Object validator = mapping.get("validator");
                if (null != validator) {
                    List<String> params = (List<String>) validator;
                    if (params.size() > 0) {
                        Transformer transformer = new Transformer();
                        transformer.setName("dx_filter");
                        TransformerParameter parameter = new TransformerParameter();
                        parameter.setColumnIndex(fieldIndex);
                        parameter.setParas(params.toArray(new String[0]));
                        transformer.setParameter(parameter);
                        transformers.add(transformer);
                    }
                }

                Object transfomer = mapping.get("transformer");
                if (null != transfomer) {
                    Map<String, Object> define = (Map<String, Object>) transfomer;
                    if (null != define.get("name") && !StringUtils.isBlank(define.get("name").toString())) {
                        Transformer transformer = new Transformer();
                        transformer.setName(define.get("name").toString());
                        TransformerParameter parameter = new TransformerParameter();
                        parameter.setColumnIndex(fieldIndex);
                        parameter.setParas(new String[0]);
                        Object params = define.get("params");
                        if (null != params) {
                            List<String> paramsDefine = (List<String>) params;
                            if (null != paramsDefine && paramsDefine.size() > 0) {
                                parameter.setParas(paramsDefine.toArray(new String[0]));
                            }
                        }
                        transformer.setParameter(parameter);
                        transformers.add(transformer);
                    }
                }
            });
        }
        return transformers;
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
            engineJob.setId(inputJob.getId());

            DataxCode dataxCode = buildDataxCode(inputJob, ctx);

            engineJob.setCode(Json.toJson(dataxCode, null));
            try {
                LOG.info("Datax-code built complete, output: " + Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dataxCode));
            } catch (JsonProcessingException e) {
                //Ignore
            }
            if (Objects.nonNull(expectJob)) {
                engineJob.setJobName(expectJob.getJobName());
                engineJob.setEngine(expectJob.getEngine());
            }

            engineJob.setRuntimeParams(inputJob.getParamsToMap(SubExchangisJob.REALM_JOB_SETTINGS, false));
            engineJob.setTaskName(inputJob.getTaskName());
            if (Objects.nonNull(expectJob)) {
                engineJob.setJobName(expectJob.getJobName());
                engineJob.setEngine(expectJob.getEngine());
            }
            engineJob.setCreateUser(inputJob.getCreateUser());
            return engineJob;

        } catch (Exception e) {
            throw new ExchangisJobException(ExchangisJobExceptionCode.ENGINE_JOB_ERROR.getCode(),
                    "Fail to build datax engine job, message:[" + e.getMessage() + "]", e);
        }
    }

    /**
     * Datax code
     *
     * @param inputJob input job
     * @return code
     */
    private DataxCode buildDataxCode(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {

        DataxCode code = new DataxCode();
        String sourceType = inputJob.getSourceType();
        String sinkType = inputJob.getSinkType();
        Content content = new Content();
        if (sourceType.equalsIgnoreCase("mysql")) {
            content.getReader().putAll(this.buildMySQLReader(inputJob, ctx));
        }
        if (sourceType.equalsIgnoreCase("hive")) {
            content.getReader().putAll(this.buildHiveReader(inputJob, ctx));
        }
        if (sinkType.equalsIgnoreCase("mysql")) {
            content.getWriter().putAll(this.buildMySQLWriter(inputJob, ctx));
        }
        if (sinkType.equalsIgnoreCase("hive")) {
            content.getWriter().putAll(this.buildHiveWriter(inputJob, ctx));
        }

        content.getTransformer().addAll(this.buildTransformer(inputJob, ctx));

        code.getContent().add(content);
        code.getSetting().putAll(this.buildSettings(inputJob, ctx));

        //To construct settings
//        JobParamSet paramSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_SETTINGS);
//        if (Objects.nonNull(paramSet)) {
//            JsonConfiguration setting = JsonConfiguration.from(code.getSetting());
//            paramSet.toList().forEach(param -> setting.set(param.getStrKey(), param.getValue()));
//            code.setSetting(setting.toMap());
//        }

//       //To construct content
//       Content content1 = new Content();
//       JobParamSet transformJobParamSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING);
//       paramSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
//       JsonConfiguration reader = JsonConfiguration.from(content1.getReader());
//       if(Objects.nonNull(paramSet)){
//           if (StringUtils.isNotBlank(inputJob.getSourceType())){
//               reader.set("name", inputJob.getSourceType().toLowerCase() + "reader");
//               reader.set("parameter", buildContentParam(paramSet, transformJobParamSet, SOURCE_COLUMN));
//               content1.setReader(reader.toMap());
//           }
//       }
//       paramSet = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
//        JsonConfiguration writer = JsonConfiguration.from(content1.getReader());
//       if(Objects.nonNull(paramSet)){
//           if (StringUtils.isNotBlank(inputJob.getSinkType())){
//               writer.set("name", inputJob.getSinkType().toLowerCase() + "writer");
//               writer.set("parameter", buildContentParam(paramSet, transformJobParamSet, SINK_COLUMN));
//               content1.setWriter(writer.toMap());
//           }
//       }
        return code;
    }

    private Map<String, Object> buildContentParam(JobParamSet paramSet, JobParamSet transformJobParamSet,
                                                  JobParamDefine<List<Map<String, Object>>> columnJobParamDefine) {
        JsonConfiguration item = JsonConfiguration.from("{}");
        //Ignore temp params
        paramSet.toList(false).forEach(param -> item.set(param.getStrKey(), param.getValue()));
        if (Objects.nonNull(transformJobParamSet)) {
            item.set(columnJobParamDefine.getKey(), columnJobParamDefine.newParam(transformJobParamSet).getValue());
        }
        return item.toMap();
    }

    private Map<String, Object> buildMySQLReader(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        Map<String, Object> reader = new HashMap<>();
        reader.put("name", "mysqlreader");

        JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("connParams", new HashMap<String, Object>());
        parameter.put("haveKerberos", false);
        parameter.put("datasource", Integer.parseInt(sourceSettings.get("datasource").getValue().toString()));

        parameter.put("username", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("username"));
        String password = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("password").toString();
        parameter.put("password", password);
        List<Map<String, Object>> columns = SOURCE_COLUMN.newParam(inputJob.getRealmParams(REALM_JOB_COLUMN_MAPPING)).getValue();
        parameter.put("column_i", columns);
        parameter.put("alias", "[\"A\"]");

        List<Map<String, Object>> connections = new ArrayList<>(1);
        Map<String, Object> connection = new HashMap<>();
        Object host = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("host");
        Object port = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("port");
        Object database = sourceSettings.get("database").getValue();
        List<Map<String, Object>> jdbcUrls = new ArrayList<>();
        Map<String, Object> jdbcUrl = new HashMap<>();
        jdbcUrl.put("host", host);
        jdbcUrl.put("port", port);
        jdbcUrl.put("database", database);
        jdbcUrls.add(jdbcUrl);
        connection.put("jdbcUrl", jdbcUrls);

        parameter.put("authType", "DEFAULT");

        List<String> tables = new ArrayList<>();
        tables.add(sourceSettings.get("table").getValue().toString());
        // connection.put("table", tables);
        parameter.put("table", LabelUtils.Jackson.toJson(tables, String.class));

        StringBuilder sql = new StringBuilder("SELECT ");
        for (Iterator<Map<String, Object>> iterator = columns.iterator(); iterator.hasNext(); ) {
            Map<String, Object> field = iterator.next();
            sql.append("A.").append(field.get("name"));
            if (iterator.hasNext()) {
                sql.append(", ");
            } else {
                sql.append(" ");
            }
        }

        sql.append("FROM ").append(sourceSettings.get("table").getValue().toString()).append(" A");

        if (null != sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition")
                && null != sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition").getValue()
                && StringUtils.isNotBlank(sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition").getValue().toString())) {
            sql.append(" WHERE ").append(sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition").getValue().toString());
        }

        List<String> querySql = new ArrayList<>();
        querySql.add(sql.toString());
        connection.put("querySql", querySql);

        connections.add(connection);
        parameter.put("connection", connections);

        reader.put("parameter", parameter);
        return reader;
    }

    private Map<String, Object> buildMySQLWriter(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        Map<String, Object> writer = new HashMap<>();
        writer.put("name", "mysqlwriter");
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("haveKerberos", false);
        parameter.put("connParams", new HashMap<String, Object>());

        JobParamSet sinkSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SINK);
        parameter.put("datasource", Integer.parseInt(sinkSettings.get("datasource").getValue().toString()));
        parameter.put("authType", "DEFAULT");
        parameter.put("username", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("username"));
        String password = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("password").toString();
        // parameter.put("password", Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));
        parameter.put("password", password);

        List<Map<String, Object>> columns = SINK_COLUMN.newParam(inputJob.getRealmParams(REALM_JOB_COLUMN_MAPPING)).getValue();
        parameter.put("column_i", columns);
        parameter.put("column", columns.stream().map(map -> map.get("name")).toArray());

        List<Map<String, Object>> connections = new ArrayList<>(1);
        Map<String, Object> connection = new HashMap<>();
        String connectProtocol = "jdbc:mysql://";
        Object host = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("host");
        Object port = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("port");
        Object database = sinkSettings.get("database").getValue();
        Map<String, Object> jdbcUrl = new HashMap<>();
        jdbcUrl.put("host", host);
        jdbcUrl.put("port", port);
        jdbcUrl.put("database", database);
        connection.put("jdbcUrl", jdbcUrl);
        List<String> tables = new ArrayList<>();
        tables.add(sinkSettings.get("table").getValue().toString());
        connection.put("table", tables);
        connections.add(connection);
        parameter.put("connection", connections);

        parameter.put("writeMode", sinkSettings.get("exchangis.job.ds.params.datax.mysql.w.write_type").getValue().toString().toLowerCase(Locale.ROOT));

        writer.put("parameter", parameter);
        return writer;
    }

    private Map<String, Object> buildHiveReader(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        Map<String, Object> reader = new HashMap<>();
        reader.put("name", "hdfsreader");

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("nullFormat", "\\\\N");
        parameter.put("haveKerberos", false);
        parameter.put("hadoopConfig", new HashMap<String, Object>());
        parameter.put("authType", "NONE");

        JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);
        parameter.put("hiveTable", sourceSettings.get("table").getValue().toString());
        parameter.put("hiveDatabase", sourceSettings.get("database").getValue());
        parameter.put("encoding", "UTF-8");
        parameter.put("datasource", Integer.parseInt(sourceSettings.get("datasource").getValue().toString()));
        try {
            String location = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("location").toString();
            URI uri = new URI(location);
            if (uri.getPort() != -1) {
                parameter.put("defaultFS", String.format("%s://%s:%d", uri.getScheme(), uri.getHost(), uri.getPort()));
            } else {
                parameter.put("defaultFS", String.format("%s://%s", uri.getScheme(), uri.getHost()));
            }
            parameter.put("path", uri.getPath());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> columns = SOURCE_COLUMN.newParam(inputJob.getRealmParams(REALM_JOB_COLUMN_MAPPING)).getValue();

        // column
//        List<Map<String, Object>> columns = new ArrayList<>();
//        String[] columnsType = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("columns-types").toString().split(":");
//        for (int i = 0; i < columnsType.length; i++) {
//            Map<String, Object> column = new HashMap<>();
//            column.put("index", i);
//            column.put("type", columnsType[i]);
//            columns.add(column);
//        }
        parameter.put("column", columns);


        String inputFormat = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("file-inputformat").toString().toLowerCase(Locale.ROOT);
        // org.apache.hadoop.mapred.TextInputFormat
        if (inputFormat.contains("text")) {
            parameter.put("fileType", "TEXT");
            parameter.put("fieldDelimiter", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("column-name-delimiter").toString());
        }
        if (inputFormat.contains("orc")) {
            parameter.put("fileType", "ORC");
        }
        if (inputFormat.contains("parquet")) {
            parameter.put("fileType", "PARQUET");
        }
        if (inputFormat.contains("rcfile")) {
            parameter.put("fileType", "RCFILE");
        }
        if (inputFormat.contains("sequencefile")) {
            parameter.put("fileType", "SEQ");
            parameter.put("fieldDelimiter", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("column-name-delimiter").toString());
        }

        parameter.put("hiveMetastoreUris", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("uris").toString());

        reader.put("parameter", parameter);
        return reader;
    }

    private Map<String, Object> buildHiveWriter(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        Map<String, Object> writer = new HashMap<>();
        writer.put("name", "hdfswriter");
        Map<String, Object> parameter = new HashMap<>();

        JobParamSet sinkSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SINK);
        parameter.put("nullFormat", "\\\\N");
        parameter.put("encoding", "UTF-8");
        parameter.put("hiveTable", sinkSettings.get("table").getValue().toString());
        parameter.put("hiveDatabase", sinkSettings.get("database").getValue().toString());
        parameter.put("authType", "NONE");
        parameter.put("hadoopConfig", new HashMap<String, Object>());
        parameter.put("haveKerberos", false);
        parameter.put("fileName", "exchangis_hive_w");
        parameter.put("datasource", Integer.parseInt(sinkSettings.get("datasource").getValue().toString()));
        try {
            String location = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("location").toString();
            URI uri = new URI(location);
            if (uri.getPort() != -1) {
                parameter.put("defaultFS", String.format("%s://%s:%d", uri.getScheme(), uri.getHost(), uri.getPort()));
            } else {
                parameter.put("defaultFS", String.format("%s://%s", uri.getScheme(), uri.getHost()));
            }
            parameter.put("path", uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        parameter.put("compress", "GZIP");

        List<Map<String, Object>> columns = SINK_COLUMN.newParam(inputJob.getRealmParams(REALM_JOB_COLUMN_MAPPING)).getValue();

        // column
//        List<Map<String, Object>> columns = new ArrayList<>();
//        String[] columnsType = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("columns-types").toString().split(":");
//        for (int i = 0; i < columnsType.length; i++) {
//            Map<String, Object> column = new HashMap<>();
//            column.put("index", i);
//            column.put("type", columnsType[i]);
//            columns.add(column);
//        }
        parameter.put("column", columns);

        String inputFormat = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("file-inputformat").toString().toLowerCase(Locale.ROOT);
        // org.apache.hadoop.mapred.TextInputFormat
        if (inputFormat.contains("text")) {
            parameter.put("fileType", "text");
            parameter.put("fieldDelimiter", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("column-name-delimiter").toString());
        }
        if (inputFormat.contains("orc")) {
            parameter.put("fileType", "orc");
        }
        if (inputFormat.contains("parquet")) {
            parameter.put("fileType", "parquet");
        }
        if (inputFormat.contains("rcfile")) {
            parameter.put("fileType", "rcfile");
        }
        if (inputFormat.contains("sequencefile")) {
            parameter.put("fileType", "seq");
            parameter.put("fieldDelimiter", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("column-name-delimiter").toString());
        }

        parameter.put("fileName", sinkSettings.get("table").getValue().toString());

        String writeMdoe = sinkSettings.get("exchangis.job.ds.params.datax.hive.w.write_type").getValue().toString();
        if (writeMdoe.equals("清空目录")) {
            parameter.put("writeMode", "truncate");
        } else {
            parameter.put("writeMode", "append");
        }

        parameter.put("hiveMetastoreUris", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("uris").toString());

        writer.put("parameter", parameter);
        return writer;
    }

    private List<Transformer> buildTransformer(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        List<Transformer> transformers = TRANSFORMER.newParam(inputJob.getRealmParams(REALM_JOB_COLUMN_MAPPING)).getValue();
        return transformers;
    }

    private Map<String, Object> buildSettings(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("useProcessor", "false");

        JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);
        JobParamSet sinkSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SINK);
        if (inputJob.getSinkType().equalsIgnoreCase("hive")) {
            if (null != sinkSettings.get("exchangis.job.ds.params.datax.hive.w.sync_meta")
                    && null != sinkSettings.get("exchangis.job.ds.params.datax.hive.w.sync_meta").getValue()
                    && "是".equals(sinkSettings.get("exchangis.job.ds.params.datax.hive.w.sync_meta").getValue().toString())) {
                settings.put("syncMeta", "true");
            } else {
                settings.put("syncMeta", "false");
            }
        }

        if (inputJob.getSourceType().equalsIgnoreCase("hive")) {
            Map<String, Object> transport = new HashMap<>();
            if (null != sinkSettings.get("exchangis.job.ds.params.datax.hive.r.trans_proto")
                    && null != sinkSettings.get("exchangis.job.ds.params.datax.hive.r.trans_proto").getValue()
                    && "二进制".equals(sinkSettings.get("exchangis.job.ds.params.datax.hive.r.trans_proto").getValue().toString())) {
                transport.put("type", "stream");
            } else {
                transport.put("type", "record");
            }
            settings.put("transport", transport);
        }

        JobParamSet jobSettings = inputJob.getRealmParams(SubExchangisJob.REALM_JOB_SETTINGS);
        if (Objects.nonNull(jobSettings)) {
            Map<String, Object> errorLimit = new HashMap<>();
            errorLimit.put("record", "0");
            if (null != jobSettings.get("exchangis.datax.setting.errorlimit.record")) {
                JobParam<?> _errorLimit = jobSettings.get("exchangis.datax.setting.errorlimit.record");
                if (null != _errorLimit && null != _errorLimit.getValue()) {
                    String value = _errorLimit.getValue().toString();
                    if (StringUtils.isNotBlank(value)) {
                        errorLimit.put("record", value);
                    }
                }
            }
            settings.put("errorLimit", errorLimit);

            Map<String, Object> advance = new HashMap<>();
            advance.put("mMemory", "1g");
            if (null != jobSettings.get("exchangis.datax.setting.max.memory")) {
                JobParam<?> _maxMemory = jobSettings.get("exchangis.datax.setting.max.memory");
                if (null != _maxMemory && null != _maxMemory.getValue()) {
                    String value = _maxMemory.getValue().toString();
                    if (StringUtils.isNotBlank(value)) {
                        advance.put("mMemory", value + "m");
                    }
                }
            }

            settings.put("advance", advance);

            Map<String, Object> speed = new HashMap<>();
            speed.put("byte", "10485760");
            speed.put("record", "0");
            speed.put("channel", "0");

            if (null != jobSettings.get("exchangis.datax.setting.speed.bytes")) {
                JobParam<?> _byteSpeed = jobSettings.get("exchangis.datax.setting.speed.bytes");
                if (null != _byteSpeed && null != _byteSpeed.getValue()) {
                    String value = _byteSpeed.getValue().toString();
                    if (StringUtils.isNotBlank(value)) {
                        speed.put("byte", String.valueOf(Integer.parseInt(value) * 1024));
                    }
                }
            }

            if (null != jobSettings.get("exchangis.datax.setting.speed.records")) {
                JobParam<?> _recordSpeed = jobSettings.get("exchangis.datax.setting.speed.records");
                if (null != _recordSpeed && null != _recordSpeed.getValue()) {
                    String value = _recordSpeed.getValue().toString();
                    if (StringUtils.isNotBlank(value)) {
                        speed.put("record", value);
                    }
                }
            }

            if (null != jobSettings.get("exchangis.datax.setting.max.parallelism")) {
                JobParam<?> _channel = jobSettings.get("exchangis.datax.setting.max.parallelism");
                if (null != _channel && null != _channel.getValue()) {
                    String value = _channel.getValue().toString();
                    if (StringUtils.isNotBlank(value)) {
                        speed.put("channel", value);
                    }
                }
            }

            settings.put("speed", speed);

        }

        return settings;
    }

    public static class DataxCode {
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

    public static class Content {
        /**
         * Reader
         */
        private Map<String, Object> reader = new HashMap<>();
        /**
         * Writer
         */
        private Map<String, Object> writer = new HashMap<>();

        private List<Transformer> transformer = new ArrayList<>();

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


        public List<Transformer> getTransformer() {
            return transformer;
        }

        public void setTransformer(List<Transformer> transformer) {
            this.transformer = transformer;
        }
    }

    public static class Transformer {

        private TransformerParameter parameter;
        private String name;

        public TransformerParameter getParameter() {
            return parameter;
        }

        public void setParameter(TransformerParameter parameter) {
            this.parameter = parameter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class TransformerParameter {

        private int columnIndex;
        private String[] paras;

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public String[] getParas() {
            return paras;
        }

        public void setParas(String[] paras) {
            this.paras = paras;
        }
    }

}
