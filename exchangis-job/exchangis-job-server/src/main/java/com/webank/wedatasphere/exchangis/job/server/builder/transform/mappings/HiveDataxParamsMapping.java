package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Hive datax mapping
 */
public class HiveDataxParamsMapping extends AbstractExchangisJobParamsMapping{

    private static final Map<String, Type> FIELD_MAP = new HashMap<>();

    private static final BitSet CHAR_TO_ESCAPE = new BitSet(128);

    private static final String[] SOURCE_SUPPORT_FILETYPE = new String[]{"TEXT", "ORC","RC","SEQ","CSV"};

    private static final String[] SINK_SUPPORT_FILETYPE = new String[]{"ORC", "TEXT"};

    private enum Type {
        /**
         * types that supported by <em>DataX</em>
         */
        STRING, LONG, BOOLEAN, DOUBLE, DATE, BINARY, OBJECT
    }
    //hive type => dataX type
    static{
        FIELD_MAP.put("TINYINT", Type.LONG);
        FIELD_MAP.put("SMALLINT", Type.LONG);
        FIELD_MAP.put("INT", Type.LONG);
        FIELD_MAP.put("BIGINT", Type.LONG);
        FIELD_MAP.put("FLOAT", Type.DOUBLE);
        FIELD_MAP.put("DOUBLE", Type.DOUBLE);
        FIELD_MAP.put("DECIMAL", Type.DOUBLE);
        FIELD_MAP.put("STRING", Type.STRING);
        FIELD_MAP.put("CHAR", Type.STRING);
        FIELD_MAP.put("VARCHAR", Type.STRING);
        FIELD_MAP.put("STRUCT", Type.STRING);
        FIELD_MAP.put("MAP", Type.OBJECT);
        FIELD_MAP.put("ARRAY", Type.OBJECT);
        FIELD_MAP.put("UNION", Type.STRING);
        FIELD_MAP.put("BINARY", Type.BINARY);
        FIELD_MAP.put("BOOLEAN", Type.BOOLEAN);
        FIELD_MAP.put("DATE", Type.DATE);
        FIELD_MAP.put("TIMESTAMP", Type.DATE);
    }

    /**
     * Hive database
     */
    private static final JobParamDefine<String>  HIVE_DATABASE = JobParams.define("hiveDatabase", JobParamConstraints.DATABASE);

    /**
     * Hive table
     */
    private static final JobParamDefine<String> HIVE_TABLE = JobParams.define("hiveTable", JobParamConstraints.TABLE);

    /**
     * Hive uris
     */
    private static final JobParamDefine<String> HIVE_URIS = JobParams.define("hiveMetastoreUris", "uris");

    /**
     * Data file name (prefix)
     */
    private static final JobParamDefine<String> DATA_FILE_NAME = JobParams.define("fileName", () -> "exch_hive_");
    /**
     * Encoding
     */
    private static final JobParamDefine<String> ENCODING  = JobParams.define("encoding", paramSet -> {
        JobParam<String> encodingParam = paramSet.get(JobParamConstraints.ENCODING);
        if (Objects.nonNull(encodingParam)){
            return encodingParam.getValue();
        }
        return "utf-8";
    });

    /**
     * Null format
     */
    private static final JobParamDefine<String> NULL_FORMAT = JobParams.define("nullFormat", paramSet -> {
        JobParam<String> nullFormatParam = paramSet.get(JobParamConstraints.NULL_FORMAT);
        if (Objects.nonNull(nullFormatParam)){
            return nullFormatParam.getValue();
        }
        return "\\N";
    });
    /**
     * Table partition
     */
    private static final JobParamDefine<Map<String, String>> TABLE_PARTITION = JobParams.define(JobParamConstraints.PARTITION);

    /**
     * Table properties
     */
    private static final JobParamDefine<Map<String, String>> HIVE_TABLE_PROPS = JobParams.define("tableProps", paramSet -> {
        String database = HIVE_DATABASE.getValue(paramSet);
        String table = HIVE_TABLE.getValue(paramSet);
        JobParam<String> dataSourceId = paramSet.get(JobParamConstraints.DATA_SOURCE_ID);
        try {
           return Objects.requireNonNull(getBean(MetadataInfoService.class)).getTableProps(getJobBuilderContext().getOriginalJob().getCreateUser(),
                    Long.valueOf(dataSourceId.getValue()), database, table);
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(e.getErrCode(), e.getMessage(), e.getCause());
        }
    });

    /**
     * Partition keys
     */
    private static final JobParamDefine<List<String>> PARTITION_KEYS = JobParams.define("partitionKeys", paramSet -> {
        JobParam<String> dataSourceId = paramSet.get(JobParamConstraints.DATA_SOURCE_ID);
        List<String> partitionKeys = new ArrayList<>();
        String database = HIVE_DATABASE.getValue(paramSet);
        String table = HIVE_TABLE.getValue(paramSet);
        try {
            partitionKeys = Objects.requireNonNull(getBean(MetadataInfoService.class)).getPartitionKeys(getJobBuilderContext().getOriginalJob().getCreateUser(),
                    Long.parseLong(dataSourceId.getValue()), database, table);
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(e.getErrCode(), e.getMessage(), e.getCause());
        }
        return partitionKeys;
    });
    /**
     * Partition values
     */
    private static final JobParamDefine<String> PARTITION_VALUES = JobParams.define("partitionValues", paramSet -> {
        Map<String, String> partitions = Optional.ofNullable(TABLE_PARTITION.getValue(paramSet)).orElse(new HashMap<>());
        //Try to find actual partition from table properties
        List<String> partitionKeys = PARTITION_KEYS.getValue(paramSet);
        String[] partitionColumns = Objects.isNull(partitionKeys)? new String[0]: partitionKeys.toArray(new String[0]);
        if (partitionColumns.length > 0 && partitions.size() != partitionColumns.length){
            throw new ExchangisJobException.Runtime(-1, "Unmatched partition list: [" +
                    StringUtils.join(partitionColumns, ",") + "]", null);
        }
        if (partitionColumns.length > 0){
            return Arrays.stream(partitionColumns).map(partitions::get).collect(Collectors.joining(","));
        }
        return null;
    });

    /**
     * Field delimiter
     */
    private static final JobParamDefine<String> FIELD_DELIMITER = JobParams.define("fieldDelimiter", paramSet ->
            HIVE_TABLE_PROPS.getValue(paramSet).getOrDefault("field.delim", "\u0001"));

    /**
     * File type
     */
    private static final JobParamDefine<HiveV2FileType> FILE_TYPE = JobParams.define("fileType", paramSet -> {
        Map<String, String> tableProps = HIVE_TABLE_PROPS.getValue(paramSet);
        AtomicReference<HiveV2FileType> fileType = new AtomicReference<>();
        Optional.ofNullable(tableProps.get("serialization.lib")).ifPresent(serLib -> fileType
                .set(HiveV2FileType.serde(serLib)));
        if (Objects.nonNull(fileType.get())){
            Optional.ofNullable(tableProps.get("file.inputformat")).ifPresent(inputFormat -> fileType
                    .set(HiveV2FileType.input(inputFormat)));
        }
        if (Objects.nonNull(fileType.get())){
            Optional.ofNullable(tableProps.get("file.outputformat")).ifPresent(outputFormat -> fileType
                    .set(HiveV2FileType.output(outputFormat)));
        }
        return Objects.nonNull(fileType.get())? fileType.get() : HiveV2FileType.TEXT;
    });

    /**
     * Data location
     */
    private static final JobParamDefine<String> DATA_LOCATION = JobParams.define("location", paramSet -> {
        Map<String, String> tableProps = HIVE_TABLE_PROPS.getValue(paramSet);
        String path = tableProps.getOrDefault("location", "");
        String partitionValues = PARTITION_VALUES.getValue(paramSet);
        if (StringUtils.isNotBlank(partitionValues)){
            String[] values = partitionValues.split(",");
            String[] keys = PARTITION_KEYS.getValue(paramSet).toArray(new String[0]);
            // Escape the path and value of partition
            StringBuilder pathBuilder = new StringBuilder(path).append("/");
            for(int i = 0; i < keys.length; i++){
                if (i > 0){
                    pathBuilder.append("/");
                }
                pathBuilder.append(escapeHivePathName(keys[i]));
                pathBuilder.append("=");
                pathBuilder.append(escapeHivePathName(values[i]));
            }
            path = pathBuilder.toString();
        }
        return path.replaceAll(" ", "%20");
    });

    /**
     * Compress name
     */
    private static final JobParamDefine<String> COMPRESS_NAME = JobParams.define("compress", paramSet -> {
        HiveV2FileType fileType = FILE_TYPE.getValue(paramSet);
        if (HiveV2FileType.TEXT.equals(fileType)){
            return "GZIP";
        } else if (HiveV2FileType.ORC.equals(fileType)){
            return "SNAPPY";
        }
        return null;
    });

    /**
     * Data path
     */
    private static final JobParamDefine<String> DATA_PATH = JobParams.define("path", paramSet -> {
        String location = DATA_LOCATION.getValue(paramSet);
        if (StringUtils.isNotBlank(location)){
            try {
                return new URI(location).getPath();
            } catch (URISyntaxException e) {
                warn("Unrecognized location: [{}]", location,  e);
            }
        }
        return null;
    });

    /**
     * Hadoop config
     */
    private static final JobParamDefine<Map<String, String>> HADOOP_CONF = JobParams.define("hadoopConfig", paramSet -> {
        String uri = DATA_LOCATION.getValue(paramSet);
        try {
            // TODO get the other hdfs cluster with tab
            return Objects.requireNonNull(getBean(MetadataInfoService.class)).getLocalHdfsInfo(uri);
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(e.getErrCode(), e.getDesc(), e.getCause());
        }
    });

    /**
     * To "defaultFS"
     */
    private static final JobParamDefine<String> DEFAULT_FS = JobParams.define("defaultFS", paramSet ->
            HADOOP_CONF.getValue(paramSet).get("fs.defaultFS"));

    private static final JobParamDefine<String> IS_SINK_FILETYPE_SUPPORT = JobParams.define("sink.fileType.support", paramSet -> {
        if (!isSupport(FILE_TYPE.getValue(paramSet).name(), SINK_SUPPORT_FILETYPE)){
            throw new ExchangisJobException.Runtime(-1, "Unsupported sink file type [" + FILE_TYPE.getValue(paramSet).name() + "] of hive", null);
        }
        return null;
    });

    private static final JobParamDefine<String> IS_SOURCE_FILETYPE_SUPPORT = JobParams.define("sink.fileType.support", paramSet -> {
        if (!isSupport(FILE_TYPE.getValue(paramSet).name(), SOURCE_SUPPORT_FILETYPE)){
            throw new ExchangisJobException.Runtime(-1, "Unsupported source file type [" + FILE_TYPE.getValue(paramSet).name() + "] of hive", null);
        }
        return null;
    });
    // TODO kerberos params

    /**
     * Escape hive path name
     * @param path path name
     * @return path
     */
    protected static String escapeHivePathName(String path) {
        if (path != null && path.length() != 0) {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < path.length(); ++i) {
                char c = path.charAt(i);
                if (c < CHAR_TO_ESCAPE.size() && CHAR_TO_ESCAPE.get(c)) {
                    sb.append('%');
                    sb.append(String.format("%1$02X", (int) c));
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        } else {
            return "__HIVE_DEFAULT_PARTITION__";
        }
    }

    protected static boolean isSupport(String value, String[] array){
        boolean isSupport = false;
        for(String item: array){
            if(item.equalsIgnoreCase(value)){
                isSupport = true;
                break;
            }
        }
        return isSupport;
    }

    @Override
    public JobParamDefine<?>[] sourceMappings() {
        return new JobParamDefine[]{HIVE_DATABASE, HIVE_TABLE, ENCODING,
        NULL_FORMAT, PARTITION_VALUES, FIELD_DELIMITER, FILE_TYPE, DATA_PATH, HADOOP_CONF, DEFAULT_FS,
                IS_SOURCE_FILETYPE_SUPPORT};
    }

    @Override
    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[]{HIVE_DATABASE, HIVE_TABLE, ENCODING,
                NULL_FORMAT, PARTITION_VALUES, FIELD_DELIMITER, FILE_TYPE, DATA_PATH, HADOOP_CONF, DEFAULT_FS,
                COMPRESS_NAME, IS_SINK_FILETYPE_SUPPORT, HIVE_URIS, DATA_FILE_NAME};
    }

    @Override
    protected Consumer<SubExchangisJob.ColumnDefine> srcColumnMappingFunc() {
        return columnDefine -> {
            String type = columnDefine.getType();
            Type t = FIELD_MAP.get(type.toUpperCase().replaceAll("[(<（][\\s\\S]+", ""));
            if (null != t){
                columnDefine.setType(t.toString());
                if (t == Type.OBJECT){
                    // Set the raw column type
                    columnDefine.setRawType(type);
                }
            } else {
                columnDefine.setType(Type.STRING.toString());
            }
        };
    }

    @Override
    protected Consumer<SubExchangisJob.ColumnDefine> sinkColumnMappingFunc() {
        return columnDefine -> columnDefine.setType(columnDefine.getType().replaceAll("[(<（][\\s\\S]+", ""));
    }

    @Override
    public String dataSourceType() {
        return "hive";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "datax".equalsIgnoreCase(engineType);
    }

}
