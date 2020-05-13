/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.job.config.handlers;

import com.webank.wedatasphere.exchangis.common.util.DateTool;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.service.HiveMetaDbService;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.TransportType;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.io.*;
import org.apache.hadoop.hive.ql.metadata.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * Handler of the job configuration(Reader or Writer) of Hive
 * @author enjoyyin
 * 2018/10/29
 */
@Service(JobDataConfHandler.PREFIX + "hive")
public class HiveJobDataConfHandler extends AbstractJobDataConfHandler{
    private static final Logger logger = LoggerFactory.getLogger(HiveJobDataConfHandler.class);

    private static final String HIVE_DB_NAME = "database";
    private static final String HIVE_TABLE_NAME = "table";
    private static final String HIVE_PARTITIONS_NAME = "partitions";
    private static final String HIVE_PARTITION_KEYS_NAME = "partitionKeys";
    private static final String HIVE_PATH_NAME = "hiveDataPath";
    private static final String HIVE_FILE_TYPE_NAME = "fileType";
    private static final String HIVE_COMPRESS_NAME = "compress";
    private static final String HIVE_FIELD_DELIMITER_NAME = "fieldDelimiter";
    private static final String HIVE_FILE_NAME = "fileName";
    private static final String HIVE_COLUMN_NAMES = "columnNames";
    private static final String HIVE_WRITE_MODE = "writeMode";

    private static final String META_SER_LIB = "serialization.lib";
    private static final String META_INPUT_FORMAT = "file.inputformat";
    private static final String META_OUTPUT_FORMAT = "file.outputformat";
    private static final String META_FIELD_DELIMITER= "field.delim";

    private static final String[] READER_SUPPORT_FILETYPE = new String[]{"TEXT", "ORC","RC","SEQ","CSV"};
    private static final String[] WRITER_SUPPORT_FILETYPE = new String[]{"ORC", "TEXT"};
    private static final Map<String, Type> FIELD_MAP = new HashMap<>();


    @Override
    protected String[] connParamNames() {
        return new String[]{
                Constants.PARAM_HDFS_PATH,  Constants.PARAM_HADOOP_CONF_LIST, Constants.PARAM_KERBEROS_BOOLEAN,
                Constants.PARAM_KERBEROS_FILE_PRINCILE, Constants.PARAM_KB_FILE_PATH,
                Constants.PARAM_LADP_USERNAME, Constants.PARAM_LADP_PASSWORD, Constants.PARAM_META_STORE_PATH
        };
    }

    @Resource
    private HiveMetaDbService hiveMetaDbService;
    private enum Type {
        /**
         * types that supported by <em>DataX</em>
         */
        STRING, LONG, BOOLEAN, DOUBLE, DATE
    }
    //Hive type => dataX type
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
        FIELD_MAP.put("MAP", Type.STRING);
        FIELD_MAP.put("ARRAY", Type.STRING);
        FIELD_MAP.put("UNION", Type.STRING);
        FIELD_MAP.put("BINARY", Type.STRING);
        FIELD_MAP.put("BOOLEAN", Type.BOOLEAN);
        FIELD_MAP.put("DATE", Type.DATE);
        FIELD_MAP.put("TIMESTAMP", Type.DATE);
    }
    enum HiveFileType{
        /**
         * TYPE:TEXT
         */
        TEXT(new TextFileStorageFormatDescriptor()),
        /**
         * TYPE:ORC
         */
        ORC(new ORCFileStorageFormatDescriptor()),
        /**
         * TYPE:AVRO
         */
        AVRO(new AvroStorageFormatDescriptor()),
        /**
         * TYPE:PARQUET
         */
        PARQUET(new ParquetFileStorageFormatDescriptor()),
        /**
         * TYPE:RC
         */
        RC(new RCFileStorageFormatDescriptor()),
        /**
         * TYPE:SEQUENCE
         */
        SEQ(new SequenceFileStorageFormatDescriptor());

        static final Map<String, HiveFileType> SERDE = new HashMap<>();
        static final Map<String, HiveFileType> INPUT = new HashMap<>();
        static final Map<String, HiveFileType> OUTPUT = new HashMap<>();
        static{
            SERDE.put(ORC.descriptor.getSerde(), ORC);
            SERDE.put(AVRO.descriptor.getSerde(), AVRO);
            SERDE.put(PARQUET.descriptor.getSerde(), PARQUET);
            INPUT.put(TEXT.descriptor.getInputFormat(), TEXT);
            INPUT.put(ORC.descriptor.getInputFormat(), ORC);
            INPUT.put(AVRO.descriptor.getInputFormat(), AVRO);
            INPUT.put(PARQUET.descriptor.getInputFormat(), PARQUET);
            INPUT.put(RC.descriptor.getInputFormat(), RC);
            INPUT.put(SEQ.descriptor.getInputFormat(), SEQ);
            OUTPUT.put(TEXT.descriptor.getOutputFormat(), TEXT);
            OUTPUT.put(ORC.descriptor.getOutputFormat(), ORC);
            OUTPUT.put(AVRO.descriptor.getOutputFormat(), AVRO);
            OUTPUT.put(PARQUET.descriptor.getOutputFormat(), PARQUET);
            OUTPUT.put(RC.descriptor.getOutputFormat(), RC);
            OUTPUT.put(SEQ.descriptor.getOutputFormat(), SEQ);
        }
        AbstractStorageFormatDescriptor descriptor;
        HiveFileType(AbstractStorageFormatDescriptor descriptor){
            this.descriptor = descriptor;
        }

        static HiveFileType serde(String serializationClz){
            return SERDE.get(serializationClz);
        }

        static HiveFileType input(String inputFormat){
            return INPUT.get(inputFormat);
        }

        static HiveFileType output(String outputFormat){
            return OUTPUT.get(outputFormat);
        }
    }
    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        if(null == dataFormParams.get(HIVE_DB_NAME) ||
                StringUtils.isBlank(String.valueOf(dataFormParams.get(HIVE_DB_NAME)))){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.db.notNull");
        }
        if(null == dataFormParams.get(HIVE_TABLE_NAME) ||
                StringUtils.isBlank(String.valueOf(dataFormParams.get(HIVE_TABLE_NAME)))){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.table.notNull");
        }
    }

    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {
        Properties properties = null;
        Table table = hiveMetaDbService.getRawTable(dataSource,  String.valueOf(dataFormParams.get(HIVE_DB_NAME)),
                String.valueOf(dataFormParams.get(HIVE_TABLE_NAME)));
        if(table.getTableType() == TableType.VIRTUAL_VIEW){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.type.notView",
                    table.getTableName());
        }
        String path =
                table.getPath().toUri().getPath();
        if(!table.getPartitionKeys().isEmpty()){
            if(null == dataFormParams.get(HIVE_PARTITIONS_NAME) || StringUtils.isBlank(String.valueOf(dataFormParams.get(HIVE_PARTITIONS_NAME)))){
                throw new JobDataParamsInValidException("exchange.job.handler.hive.partition.notNull");
            }
            //Get raw partition information
            List<FieldSchema> rawPartitionKeys = table.getPartitionKeys();
            String[] partitions = String.valueOf(dataFormParams.get(HIVE_PARTITIONS_NAME)).split(",");
            //check the length of partition values
            if(partitions.length != rawPartitionKeys.size()){
                throw new JobDataParamsInValidException("exchange.job.handler.hive.partition.length");
            }
            //get partition keys
            String[] partitionKeys = new String[partitions.length];
            for(int i = 0; i < rawPartitionKeys.size(); i ++){
                partitionKeys[i] = rawPartitionKeys.get(i).getName();
            }
            dataFormParams.put(HIVE_PARTITION_KEYS_NAME, StringUtils.join(partitionKeys, ","));
            Map<String, String> partSpec = new HashMap<>(partitions.length);
            StringBuilder partNameBuilder = new StringBuilder();
            try{
                partitions =  Warehouse.makePartName(rawPartitionKeys, Arrays.asList(partitions)).split("/");
            } catch (MetaException e) {
                throw new JobDataParamsInValidException("exchange.job.handler.hive.partition.error", e.getMessage());
            }
            for(String val : partitions){
                String[] kv = val.split("=");
                if(kv.length != 2){
                    throw new JobDataParamsInValidException("exchange.job.handler.hive.partition.error.structure");
                }
                partNameBuilder.append("/").append(kv[0]).append("=");
                String partVal = org.apache.hadoop.hive.common.FileUtils.unescapePathName(kv[1]);
                if(isSupport(partVal, DateTool.TIME_PLACEHOLDER) || DateTool.TIME_REGULAR_PATTERN.matcher(partVal).find()) {
                    partNameBuilder.append(partVal);
                }else{
                    partNameBuilder.append(kv[1]);
                }
                partSpec.put(kv[0], kv[1]);
            }
            path += partNameBuilder.toString();
            properties = hiveMetaDbService.getMetaStore(dataSource,String.valueOf(dataFormParams.get(HIVE_DB_NAME)),
                    String.valueOf(dataFormParams.get(HIVE_TABLE_NAME)), partSpec);
        }
        if(null == properties) {
            properties = table.getMetadata();
        }
        dataFormParams.put(HIVE_PATH_NAME, path);
        String serLib = properties.getProperty(META_SER_LIB);
        String inputFormat = properties.getProperty(META_INPUT_FORMAT);
        String outputFormat = properties.getProperty(META_OUTPUT_FORMAT);
        String delimiter = properties.getProperty(META_FIELD_DELIMITER);
        if(null != delimiter){
            dataFormParams.put(HIVE_FIELD_DELIMITER_NAME, delimiter);
        }
        HiveFileType fileType = getHiveFileType(serLib, inputFormat, outputFormat);
        if(null != fileType){
            dataFormParams.put(HIVE_FILE_TYPE_NAME, fileType);
        }
        TransportType transportType = TransportType.type(
                String.valueOf(dataFormParams.getOrDefault(JobConstants.CONFIG_TRANSPORT_TYPE, "")));
        if(null == transportType){
            return;
        }
        if(transportType == TransportType.STREAM) {
            //Empty prefix file name
            dataFormParams.put(HIVE_FILE_NAME, "");
        }else if(null != fileType){
            if(fileType.equals(HiveFileType.TEXT)){
                dataFormParams.put(HIVE_COMPRESS_NAME, "GZIP");
            }else if(fileType.equals(HiveFileType.ORC)){
                dataFormParams.put(HIVE_COMPRESS_NAME, "SNAPPY");
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        String fileType = String.valueOf(dataFormParams.getOrDefault(HIVE_FILE_TYPE_NAME, ""));
        if(StringUtils.isEmpty(fileType)){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.table.type");
        }
        boolean isSupport = isSupport(fileType, READER_SUPPORT_FILETYPE);
        if(!isSupport){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.table.type.notSupport", fileType);
        }
        dataFormParams.remove(HIVE_COMPRESS_NAME);
        //Columns mapping
        List<DataColumn> columns = (List<DataColumn>) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        List<String> columnNames = new ArrayList<>();
        for(DataColumn column : columns){
            String type = column.getType();
            Type t = FIELD_MAP.get(type.toUpperCase().replaceAll("[(<（][\\s\\S]+", ""));
            if(null != t){
                column.setType(t.toString());
            }else{
                column.setType(Type.STRING.toString());
            }
            columnNames.add(column.getName());
        }
        dataFormParams.put(HIVE_COLUMN_NAMES, columnNames);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        String fileType = String.valueOf(dataFormParams.getOrDefault(HIVE_FILE_TYPE_NAME, ""));
        if(StringUtils.isEmpty(fileType)){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.table.type");
        }
        boolean isSupport = isSupport(fileType, WRITER_SUPPORT_FILETYPE);
        if(!isSupport){
            throw new JobDataParamsInValidException("exchange.job.handler.hive.table.type.notSupport", fileType);
        }
        //Check if transport type is "STREAM" or in sync metadata mode
        if(!dataFormParams.getOrDefault(JobConstants.CONFIG_TRANSPORT_TYPE, TransportType.RECORD.v())
                .equals(TransportType.STREAM.v()) && !Boolean.
                parseBoolean(String.valueOf(dataFormParams.getOrDefault(JobConstants.CONFIG_SYNC_METADATA, false)))) {
            //Doesn't support to create HIVE table automatically, so the table's field number and field name should be equal to the existed one
            List<MetaColumnInfo> metaColumnInfos = hiveMetaDbService.getColumns(dataSource, String.valueOf(dataFormParams.get(HIVE_DB_NAME)),
                    String.valueOf(dataFormParams.get(HIVE_TABLE_NAME)));
            List<DataColumn> columns = (List<DataColumn>) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
            boolean matched = metaColumnInfos.size() == columns.size();
            if (matched) {
                for (int i = 0; i < metaColumnInfos.size(); i++) {
                    DataColumn dataColumn = columns.get(i);
                    MetaColumnInfo metaColumnInfo = metaColumnInfos.get(i);
                    if (!dataColumn.getName().equals(metaColumnInfo.getName())) {
                        matched = false;
                        break;
                    }
                    dataColumn.setType(dataColumn.getType().replaceAll("[(<（][\\s\\S]+", ""));
                }
            }
            if (!matched) {
                throw new JobDataParamsInValidException("exchange.job.handler.hive.column.illegal");
            }
        }
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(3);
        result.put(HIVE_DB_NAME, dataConfParams.get(HIVE_DB_NAME));
        result.put(HIVE_TABLE_NAME, dataConfParams.get(HIVE_TABLE_NAME));
        result.put(HIVE_PARTITIONS_NAME, dataConfParams.get(HIVE_PARTITIONS_NAME));
        return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(HIVE_WRITE_MODE, dataConfParams.getOrDefault(HIVE_WRITE_MODE, ""));
        return result;
    }

    @Override
    protected boolean isColumnAutoFill() {
        return true;
    }

    @Override
    protected void autoFillColumn(List<DataColumn> columns, DataSource dataSource,  Map<String, Object> dataFormParams, DataConfType type) {
        //Check if transport type is "STREAM"
        if(!dataFormParams.getOrDefault(JobConstants.CONFIG_TRANSPORT_TYPE, TransportType.RECORD.v())
                .equals(TransportType.STREAM.v()) && !Boolean.
                parseBoolean(String.valueOf(dataFormParams.getOrDefault(JobConstants.CONFIG_SYNC_METADATA, false)))){
            logger.info("Fill column list automatically, type: " + type.name() + ", datasourceId: " + dataSource.getId() +
                    ",  database: " +  String.valueOf(dataFormParams.get(HIVE_DB_NAME)) +", table:" + String.valueOf(dataFormParams.get(HIVE_TABLE_NAME)));
            List<MetaColumnInfo> metaColumnInfos = hiveMetaDbService.getColumns(dataSource, String.valueOf(dataFormParams.get(HIVE_DB_NAME)),
                    String.valueOf(dataFormParams.get(HIVE_TABLE_NAME)));
            for (MetaColumnInfo info : metaColumnInfos) {
                DataColumn dataColumn = new DataColumn(info.getName(), info.getType());
                dataColumn.setIndex(info.getIndex());
                columns.add(dataColumn);
            }
        }
    }

    /**
     * Get hive file type
     * @param serLib serLib class
     * @param inputFormat input format class
     * @param outputFormat output format class
     * @return
     */
    private HiveFileType getHiveFileType(String serLib, String inputFormat, String outputFormat){
        HiveFileType fileType = null;
        if(StringUtils.isNotBlank(serLib)){
            fileType = HiveFileType.serde(serLib);
        }
        if(null == fileType && StringUtils.isNotBlank(inputFormat)){
            fileType = HiveFileType.input(inputFormat);
        }
        if(null == fileType && StringUtils.isNotBlank(outputFormat)){
            fileType = HiveFileType.output(outputFormat);
        }
        return fileType;
    }
}
