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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
                Map<String, Object> column = new HashMap<>();
                columns.add(column);
                column.put(SOURCE_FIELD_NAME.getKey(), SOURCE_FIELD_NAME.newParam(mapping).getValue());
                column.put(SOURCE_FIELD_TYPE.getKey(), SOURCE_FIELD_TYPE.newParam(mapping).getValue());
                column.put(SOURCE_FIELD_INDEX.getKey(), SOURCE_FIELD_INDEX.newParam(mapping).getValue());
            });
        }
        return columns;
    });

    private static final JobParamDefine<List<Map<String, Object>>> SINK_COLUMN = JobParams.define("column", (BiFunction<String, JobParamSet, List<Map<String, Object>>>) (key, paramSet) -> {
        List<Map<String, Object>> columns = new ArrayList<>();
        List<Map<String, Object>> mappings = TRANSFORM_MAPPING.newParam(paramSet).getValue();
        if (Objects.nonNull(mappings)) {
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
        Map<String, Object> parameter = new HashMap<>();

        JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);
        parameter.put("username", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("username"));
        String password = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("password").toString();
        parameter.put("password", Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));

        parameter.put("column", SOURCE_COLUMN.newParam(inputJob.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING)).getValue().stream().map(map -> {
            return map.get("name");
        }).toArray());

        List<Map<String, Object>> connections = new ArrayList<>(1);
        Map<String, Object> connection = new HashMap<>();
        String connectProtocol = "jdbc:mysql://";
        Object host = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("host");
        Object port = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("port");
        Object database = sourceSettings.get("database").getValue();
        connection.put("jdbcUrl", String.format("%s%s:%s/%s", connectProtocol, host, port, database));
        connection.put("table", sourceSettings.get("table").getValue().toString());
        connections.add(connection);
        parameter.put("connection", connections);

        if (null != sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition")
                && null != sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition").getValue()
                && StringUtils.isNotBlank(sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition").getValue().toString())) {
            parameter.put("where", sourceSettings.get("exchangis.job.ds.params.datax.mysql.r.where_condition").getValue().toString());
        }

        reader.put("parameter", parameter);
        return reader;
    }

    public static void main(String[] args) {
        String s = "123";
        String s1 = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
        System.out.println(s1);
    }

    private Map<String, Object> buildMySQLWriter(SubExchangisJob inputJob, ExchangisJobBuilderContext ctx) {
        Map<String, Object> writer = new HashMap<>();
        writer.put("name", "mysqlwriter");
        Map<String, Object> parameter = new HashMap<>();

        JobParamSet sinkSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SINK);
        parameter.put("username", ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("username"));
        String password = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("password").toString();
        parameter.put("password", Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));

        parameter.put("column", SINK_COLUMN.newParam(inputJob.getRealmParams(SubExchangisJob.REALM_JOB_COLUMN_MAPPING)).getValue().stream().map(map -> {
            return map.get("name");
        }).toArray());

        List<Map<String, Object>> connections = new ArrayList<>(1);
        Map<String, Object> connection = new HashMap<>();
        String connectProtocol = "jdbc:mysql://";
        Object host = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("host");
        Object port = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("port");
        Object database = sinkSettings.get("database").getValue();
        connection.put("jdbcUrl", String.format("%s%s:%s/%s", connectProtocol, host, port, database));
        connection.put("table", sinkSettings.get("table").getValue().toString());
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

        JobParamSet sourceSettings = inputJob.getRealmParams(REALM_JOB_CONTENT_SOURCE);
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

        // column
        List<Map<String, Object>> columns = new ArrayList<>();
        String[] columnsType = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("columns-types").toString().split(":");
        for (int i = 0; i < columnsType.length; i++) {
            Map<String, Object> column = new HashMap<>();
            column.put("index", i);
            column.put("type", columnsType[i]);
            columns.add(column);
        }
        parameter.put("column", columns);

        String inputFormat = ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("file-inputformat").toString().toLowerCase(Locale.ROOT);
        // org.apache.hadoop.mapred.TextInputFormat
        if (inputFormat.contains("text")) {
            parameter.put("fileType", "text");
            parameter.put("fieldDelimiter", ctx.getDatasourceParam(sourceSettings.get("datasource").getValue().toString()).get("column-name-delimiter").toString());
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

        // column
        List<Map<String, Object>> columns = new ArrayList<>();
        String[] columnsType = ctx.getDatasourceParam(sinkSettings.get("datasource").getValue().toString()).get("columns-types").toString().split(":");
        for (int i = 0; i < columnsType.length; i++) {
            Map<String, Object> column = new HashMap<>();
            column.put("index", i);
            column.put("type", columnsType[i]);
            columns.add(column);
        }
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
                transport.put("type", "record");
            } else {
                transport.put("type", "stream");
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

}
