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
import com.webank.wedatasphere.exchangis.datasource.service.MySQLMetaDbService;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.DataConfType;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.dto.SqlJoinCondition;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 */
@Service(JobDataConfHandler.PREFIX + "mysql")
public class MySQLJobDataConfHandler extends AbstractJobDataConfHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLJobDataConfHandler.class);
    private static final String DEFAULT_COLUMN_SEPARATOR = ",";
    private static final String DEFAULT_TABLE_COLUMN_SEPARATOR = ".";

    private static final String TDSQL_DB_NAME = "database";
    private static final String TDSQL_TABLE_NAME = "table";
    private static final String TDSQL_TABLE_ALIAS = "alias";
    private static final String TDSQL_LINK_CONDITION = "join";
    private static final String TDSQL_WHERE = "where";
    private static final String TDSQL_WRITE_MODE = "writeMode";
    private static final String TDSQL_BATCH_SIZE = "batchSize";
    private static final String TDSQL_COLUMN_NAME = "sqlColumn";
    private static final String TDSLQ_COLUMN_NAME_ORDER = "sqlOrderColumn";

    private static final String TDSQL_WHERE_CONDITION = " WHERE ";
    private static final String TDSQL_INNER_JOIN = " INNER JOIN ";
    private static final String TDSQL_SELECT_CONDITION = " SELECT ";
    private static final String TDSQL_FROM_CONDITION = " FROM ";
    private static final String TDSQL_ON_CODITION = " ON ";
    private static final String TDSQL_AND_CONDITION = " AND ";
    private static final String TDSQL_QUERY_SQL = "querySql";
    private static final String TDSQL_PRIMARY_KEYS = "primaryKeys";

    private static final int MAX_BATCH_SIZE = 100000;
    @Resource
    private MySQLMetaDbService mysqlMetaDbService;

    @Override
    protected String[] connParamNames() {
        return new String[]{Constants.PARAM_DEFAULT_PASSWORD,
                Constants.PARAM_DEFAULT_USERNAME,
                Constants.PARAM_SFTP_HOST,
                Constants.PARAM_SFTP_PORT,Constants.PARAM_KEY_TDSQL_CONFIG};
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        if(dataFormParams.get(TDSQL_DB_NAME) == null ||
                StringUtils.isBlank(String.valueOf(dataFormParams.get(TDSQL_DB_NAME)))){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.db.notNull");
        }
        if(dataFormParams.get(TDSQL_TABLE_NAME) == null ||
                StringUtils.isBlank(String.valueOf(dataFormParams.get(TDSQL_TABLE_NAME)))){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.table.notNull");
        }
        if(StringUtils.isNotBlank(String.valueOf(dataFormParams
                .getOrDefault(TDSQL_BATCH_SIZE, "")))){
            int batchSize = Integer.parseInt(String.valueOf(dataFormParams.get(TDSQL_BATCH_SIZE)));
            if(batchSize > MAX_BATCH_SIZE){
                throw new JobDataParamsInValidException("exchange.job.handler.jdbc.batchSize", MAX_BATCH_SIZE);
            }
        }
    }

    @Override
    protected void prePersist0(DataSource dataSource, Map<String, Object> dataFormParams) {

    }

    @Override
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        List<?> tables= (List<?>)dataFormParams.get(TDSQL_TABLE_NAME);
        List<?> alias = (List<?>)dataFormParams.get(TDSQL_TABLE_ALIAS);
        List<?> joinInfo = (List<?>) dataFormParams.get(TDSQL_LINK_CONDITION);
        if(alias.size() <= 0){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.table.alias.notNull");
        }
        if(tables.size() != alias.size()){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.table.alias.notMatch");
        }
        if(joinInfo.size() < tables.size() - 1){
            throw new JobDataParamsInValidException("exchange.job.handler.jdbc.table.condition");
        }
        List<?> columns = (List<?>) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        //concat querySql
        String querySql = contactSql( tables, alias,columns, joinInfo,
                String.valueOf(dataFormParams.getOrDefault(TDSQL_WHERE, "")));
        dataFormParams.put(TDSQL_QUERY_SQL, querySql);
        //change columns to array
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        List columns = (List) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        //change columns to array
        if(null != columns && !columns.isEmpty()) {
            List<?> sqlColumns = (List<?>) columns.stream().map(value -> {
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
            dataFormParams.put(TDSLQ_COLUMN_NAME_ORDER, sqlOrderColumns);
            dataFormParams.put(TDSQL_COLUMN_NAME, sqlColumns);
        }
        Object tableNameParam = dataFormParams.getOrDefault(TDSQL_TABLE_NAME, "");
        String tableName;
        if(tableNameParam instanceof List){
            tableName = String.valueOf(((List)tableNameParam).get(0));
        }else{
            tableName = String.valueOf(tableNameParam);
        }
        //get primary keys
        dataFormParams.put(TDSQL_PRIMARY_KEYS, mysqlMetaDbService.getPrimaryKeys(dataSource,
                String.valueOf(dataFormParams.getOrDefault(TDSQL_DB_NAME, "")),
                tableName));
    }

    @Override
    protected Map<String, Object> postGet0(Map<String, Object> dataConfParams) {
        return super.postGet0(dataConfParams);
    }

    @Override
    protected Map<String, Object> postGetReader(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(5);
        result.put(TDSQL_DB_NAME, dataConfParams.getOrDefault(TDSQL_DB_NAME, ""));
        result.put(TDSQL_TABLE_NAME, Json.fromJson(
                String.valueOf(dataConfParams.getOrDefault(TDSQL_TABLE_NAME, "[]")), String.class));
        result.put(TDSQL_TABLE_ALIAS, Json.fromJson(
                String.valueOf(dataConfParams.getOrDefault(TDSQL_TABLE_ALIAS, "[]")), String.class
        ));
        result.put(TDSQL_WHERE, dataConfParams.getOrDefault(TDSQL_WHERE, ""));
        result.put(TDSQL_LINK_CONDITION, Json.fromJson(
                String.valueOf(dataConfParams.getOrDefault(TDSQL_LINK_CONDITION, "[]")), Object.class
        ));
        return result;
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(2);
        result.put(TDSQL_TABLE_NAME, dataConfParams.getOrDefault(TDSQL_TABLE_NAME, ""));
        result.put(TDSQL_DB_NAME, dataConfParams.getOrDefault(TDSQL_DB_NAME, ""));
        result.put(TDSQL_WRITE_MODE,dataConfParams.getOrDefault(TDSQL_WRITE_MODE,""));
        result.put(TDSQL_BATCH_SIZE,dataConfParams.getOrDefault(TDSQL_BATCH_SIZE,""));
        return result;
    }

    @Override
    protected boolean isColumnAutoFill() {
        return true;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    protected void autoFillColumn(List<DataColumn> columns, DataSource dataSource, Map<String, Object> dataFormParams, DataConfType type) {
        LOG.info("Fill column list automatically, type: " + type.name() + ", data source Id: " + dataSource.getId() +
                ",  database: " +  String.valueOf(dataFormParams.get(TDSQL_DB_NAME)) +", table:" + String.valueOf(dataFormParams.get(TDSQL_TABLE_NAME)));
        Object tableName = dataFormParams.get(TDSQL_TABLE_NAME);
        List alias = (List)dataFormParams.get(TDSQL_TABLE_ALIAS);
        List tables = new ArrayList();
        if(tableName instanceof String){
            tables = Collections.singletonList(tableName);
        }else if (tableName instanceof List){
            tables = (List)dataFormParams.get(TDSQL_TABLE_NAME);
            if(alias != null && alias.size() != tables.size()){
                alias = null;
            }
        }
        for(int i = 0; i < tables.size(); i++){
            List<MetaColumnInfo> metaColumns = mysqlMetaDbService.
                    getColumns(dataSource, String.valueOf(dataFormParams.get(TDSQL_DB_NAME)),
                            String.valueOf(tables.get(i)));
            String namePrefix = "";
            if(alias != null){
                namePrefix = alias.get(i) + DEFAULT_TABLE_COLUMN_SEPARATOR;
            }
            String finalNamePrefix = namePrefix;
            metaColumns.forEach(metaColumn ->{
                DataColumn dataColumn = new DataColumn(finalNamePrefix + metaColumn.getName(),
                        metaColumn.getType(), metaColumn.getIndex());
                if(DataConfType.READER.equals(type)){
                    dataColumn.setIndex(metaColumn.getIndex());
                }
                columns.add(dataColumn);
            });
        }
    }

    @SuppressWarnings({"rawtypes"})
    private String contactSql(List tables, List alias,
                              List columns, List joinInfo, String whereClause){
        StringBuilder builder = new StringBuilder(TDSQL_SELECT_CONDITION)
                .append(columnListSql(columns))
                .append(TDSQL_FROM_CONDITION)
                .append(tableOnSql(tables, alias, joinInfo));
        if(StringUtils.isNotBlank(whereClause)){
            builder.append(TDSQL_WHERE_CONDITION).append(whereClause);
        }
        return builder.toString();
    }

    @SuppressWarnings({"rawtypes"})
    private String columnListSql(List columns){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < columns.size(); i++){
            Object rawColumn = columns.get(i);
            if(rawColumn instanceof  DataColumn){
                builder.append(((DataColumn) rawColumn).getName());
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
        boolean onJoin = false;
        for(int i = 0; i < tables.size(); i ++){
            builder.append(tables.get(i))
                   .append(" ").append(alias.get(i));
            if(onJoin){
                Object joinConditions = joinInfo.get(i - 1);
                builder.append(TDSQL_ON_CODITION).append(joinCondition(
                        //alias left, alias right
                        String.valueOf(alias.get(i - 1)), String.valueOf(alias.get(i)),
                        Objects.requireNonNull(Json.fromJson(Json.toJson(joinConditions, null), SqlJoinCondition.class))));
                onJoin = false;
            }
            if(i + 1 < tables.size()){
                builder.append(TDSQL_INNER_JOIN);
                onJoin = true;
            }
        }
        return builder.toString();
    }

    private String joinCondition(String aliasLeft, String aliasRight,
                                 List<SqlJoinCondition> joinConditions){
        //For example: t1.column1
        joinConditions.forEach( joinCondition ->{
            String left = joinCondition.getLeft();
            if(!left.contains(DEFAULT_TABLE_COLUMN_SEPARATOR)){
                joinCondition.setLeft(aliasLeft + DEFAULT_TABLE_COLUMN_SEPARATOR + joinCondition.getLeft());
            }
            String right = joinCondition.getRight();
            if(!right.contains(DEFAULT_TABLE_COLUMN_SEPARATOR)){
                joinCondition.setRight(aliasRight + DEFAULT_TABLE_COLUMN_SEPARATOR + joinCondition.getRight());
            }
        });
        SqlJoinCondition[] conditions = new SqlJoinCondition[joinConditions.size()];
        joinConditions.toArray(conditions);
        return StringUtils.join(conditions, TDSQL_AND_CONDITION);
    }

}
