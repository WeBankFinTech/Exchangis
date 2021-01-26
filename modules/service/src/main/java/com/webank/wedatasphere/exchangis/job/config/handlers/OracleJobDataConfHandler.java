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

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.service.OracleMetaDbService;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author ronaldyang
 */
@Service(JobDataConfHandler.PREFIX + "oracle")
public class OracleJobDataConfHandler extends AbstractJobDataConfHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OracleJobDataConfHandler.class);
    private static final String DEFAULT_COLUMN_SEPARATOR = ",";
    private static final String DEFAULT_TABLE_COLUMN_SEPARATOR = ".";

    private static final String ORACLE_DB_NAME = "database";
    private static final String ORACLE_TABLE_NAME = "table";
    private static final String ORACLE_WHERE = "where";
    private static final String ORACLE_WRITE_MODE = "writeMode";
    private static final String ORACLE_BATCH_SIZE = "batchSize";
    private static final String ORACLE_COLUMN_NAME = "sqlColumn";
    private static final String ORACLE_COLUMN_NAME_ORDER = "sqlOrderColumn";

    private static final String ORACLE_WHERE_CONDITION = " WHERE ";
    private static final String ORACLE_SELECT_CONDITION = " SELECT ";
    private static final String ORACLE_FROM_CONDITION = " FROM ";
    private static final String ORACLE_AND_CONDITION = " AND ";
    private static final String ORACLE_QUERY_SQL = "querySql";
    private static final String ORACLE_PRIMARY_KEYS = "primaryKeys";

    private static final int MAX_BATCH_SIZE = 100000;

    @Resource
    private OracleMetaDbService oracleMetaDbService;

    @Override
    protected String[] connParamNames() {
        return new String[]{Constants.PARAM_DEFAULT_PASSWORD,
                Constants.PARAM_DEFAULT_USERNAME,
                Constants.PARAM_ORACLE_HOST,
                Constants.PARAM_ORACLE_PORT,Constants.PARAM_ORACLE_SERVICE_NAME,
                Constants.PARAM_ORACLE_SID};
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        if(dataFormParams.get(ORACLE_DB_NAME) == null ||
                StringUtils.isBlank(String.valueOf(dataFormParams.get(ORACLE_DB_NAME)))){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.db.notNull");
        }
        if(dataFormParams.get(ORACLE_TABLE_NAME) == null ||
                StringUtils.isBlank(String.valueOf(dataFormParams.get(ORACLE_TABLE_NAME)))){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.table.notNull");
        }
        if(StringUtils.isNotBlank(String.valueOf(dataFormParams
                .getOrDefault(ORACLE_BATCH_SIZE, "")))){
            int batchSize = Integer.parseInt(String.valueOf(dataFormParams.get(ORACLE_BATCH_SIZE)));
            if(batchSize > MAX_BATCH_SIZE){
                throw new JobDataParamsInValidException("exchange.job.handler.jdbc.batchSize", MAX_BATCH_SIZE);
            }
        }
    }

    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {

    }

    @Override
    @SuppressWarnings({"rawtypes"})
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        if (dataFormParams.get(ORACLE_TABLE_NAME) instanceof String) {
            dataFormParams.put(ORACLE_TABLE_NAME, Collections.singletonList((String) dataFormParams.get(ORACLE_TABLE_NAME)));
        }
        List<?> tables= (List<?>)dataFormParams.get(ORACLE_TABLE_NAME);
        List columns = (List) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        //Concat querySql
        String querySql = contactSql( tables, columns,
                String.valueOf(dataFormParams.getOrDefault(ORACLE_WHERE, "")));
        dataFormParams.put(ORACLE_QUERY_SQL, querySql);
        //Change columns to array
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        List columns = (List) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        //Change columns to array
        if(null != columns && !columns.isEmpty()) {
            List sqlColumns = (List) columns.stream().map(value -> {
                if (value instanceof DataColumn) {
                    return ((DataColumn)value).getName();
                }
                return null;
            }).collect(Collectors.toList());
            List sqlOrderColumns = (List)columns.stream().sorted(Comparator.comparing(DataColumn::getIndex)).map(value ->{
                if (value instanceof DataColumn) {
                    return ((DataColumn)value).getName();
                }
                return null;
            }).collect(Collectors.toList());
            dataFormParams.put(ORACLE_COLUMN_NAME_ORDER, sqlOrderColumns);
            dataFormParams.put(ORACLE_COLUMN_NAME, sqlColumns);       }
        Object tableNameParam = dataFormParams.getOrDefault(ORACLE_TABLE_NAME, "");
        String tableName;
        if(tableNameParam instanceof List){
            tableName = String.valueOf(((List)tableNameParam).get(0));
        }else{
            tableName = String.valueOf(tableNameParam);
        }
        //get primary keys
        dataFormParams.put(ORACLE_PRIMARY_KEYS, oracleMetaDbService.getPrimaryKeys(dataSource,
                String.valueOf(dataFormParams.getOrDefault(ORACLE_DB_NAME, "")),
                tableName));
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
        return super.postGet0(dataConfParams);
    }

    @Override
    protected Map<String, Object> postGetReader(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(5);
        result.put(ORACLE_DB_NAME, dataConfParams.getOrDefault(ORACLE_DB_NAME, ""));
        result.put(ORACLE_TABLE_NAME, Json.fromJson(
                String.valueOf(dataConfParams.getOrDefault(ORACLE_TABLE_NAME, "[]")), String.class));
        result.put(ORACLE_WHERE, dataConfParams.getOrDefault(ORACLE_WHERE, ""));
        return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(2);
        result.put(ORACLE_TABLE_NAME, dataConfParams.getOrDefault(ORACLE_TABLE_NAME, ""));
        result.put(ORACLE_DB_NAME, dataConfParams.getOrDefault(ORACLE_DB_NAME, ""));
        result.put(ORACLE_WRITE_MODE,dataConfParams.getOrDefault(ORACLE_WRITE_MODE,""));
        result.put(ORACLE_BATCH_SIZE,dataConfParams.getOrDefault(ORACLE_BATCH_SIZE,""));
        return result;
    }

    @Override
    protected boolean isColumnAutoFill() {
        return true;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    protected void autoFillColumn(List<DataColumn> columns, DataSource dataSource, Map<String, Object> dataFormParams, DataConfType type) {
        LOG.info("Fill column list automatically, type: " + type.name() + ", datasourceId: " + dataSource.getId() +
                ",  database: " +  String.valueOf(dataFormParams.get(ORACLE_DB_NAME)) +", table:" + String.valueOf(dataFormParams.get(ORACLE_TABLE_NAME)));
        Object tableName = dataFormParams.get(ORACLE_TABLE_NAME);
        List tables = new ArrayList();
        if(tableName instanceof String){
            tables = Collections.singletonList(tableName);
        }else if (tableName instanceof List){
            tables = (List)dataFormParams.get(ORACLE_TABLE_NAME);
        }
        for (Object table : tables) {
            List<MetaColumnInfo> metaColumns = oracleMetaDbService.
                    getColumns(dataSource, String.valueOf(dataFormParams.get(ORACLE_DB_NAME)),
                            String.valueOf(table));
            String finalNamePrefix = "";
            metaColumns.forEach(metaColumn -> {
                DataColumn dataColumn = new DataColumn(finalNamePrefix + metaColumn.getName(),
                        metaColumn.getType(), metaColumn.getIndex());
                if (DataConfType.READER.equals(type)) {
                    dataColumn.setIndex(metaColumn.getIndex());
                }
                columns.add(dataColumn);
            });
        }
    }

    @SuppressWarnings({"rawtypes"})
    private String contactSql(List tables,
                              List columns, String whereClause){
        StringBuilder builder = new StringBuilder(ORACLE_SELECT_CONDITION)
                .append(columnListSql(columns))
                .append(ORACLE_FROM_CONDITION)
                .append(tableOnSql(tables, null, null));
        if(StringUtils.isNotBlank(whereClause)){
            builder.append(ORACLE_WHERE_CONDITION).append(whereClause);
        }
        return builder.toString();
    }

    @SuppressWarnings({"rawtypes"})
    private String columnListSql(List columns){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < columns.size(); i++){
            Object rawColumn = columns.get(i);
            if(rawColumn instanceof  DataColumn){
                builder.append("\"");
                builder.append(((DataColumn)rawColumn).getName());
                builder.append("\"");
                if(i < columns.size() - 1){
                    builder.append(DEFAULT_COLUMN_SEPARATOR);
                }
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    @SuppressWarnings({"rawtypes"})
    private String tableOnSql(List tables, List alias, List joinInfo){
        StringBuilder builder = new StringBuilder();
        for (Object table : tables) {
            builder.append(table);
        }
        return builder.toString();
    }

}
