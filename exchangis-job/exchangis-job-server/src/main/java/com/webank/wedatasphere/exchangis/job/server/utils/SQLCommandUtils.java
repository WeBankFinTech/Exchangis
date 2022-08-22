package com.webank.wedatasphere.exchangis.job.server.utils;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Command utils for SQL
 */
public class SQLCommandUtils {
    private static final String DEFAULT_COLUMN_SEPARATOR = ",";
    private static final String DEFAULT_TABLE_COLUMN_SEPARATOR = ".";
    /**
     * Command list
     */
    private static final String SQL_WHERE_CONDITION = " WHERE ";
    private static final String SQL_INNER_JOIN = " INNER JOIN ";
    private static final String SQL_SELECT_CONDITION = " SELECT ";
    private static final String SQL_FROM_CONDITION = " FROM ";
    private static final String SQL_ON_CONDITION = " ON ";
    private static final String SQL_AND_CONDITION = " AND ";

    public static String contactSql(List<?> tables, List<?> alias,
                              List<?> columns, List<?> joinInfo, String whereClause){
        StringBuilder builder = new StringBuilder(SQL_SELECT_CONDITION)
                .append(columnListSql(columns))
                .append(SQL_FROM_CONDITION)
                .append(tableOnSql(tables, alias, joinInfo));
        if(StringUtils.isNotBlank(whereClause)){
            builder.append(SQL_WHERE_CONDITION).append(whereClause);
        }
        return builder.toString();
    }

    private static String columnListSql(List<?> columns){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < columns.size(); i++){
            builder.append(columns.get(i));
            if(i < columns.size() - 1){
                builder.append(DEFAULT_COLUMN_SEPARATOR);
            }
            builder.append(" ");
        }
        return builder.toString();
    }

    private static String tableOnSql(List<?> tables, List<?> alias, List<?> joinInfo){
        StringBuilder builder = new StringBuilder();
        boolean onJoin = false;
        for(int i = 0; i < tables.size(); i ++){
            builder.append(tables.get(i));
            if(alias != null) {
                builder.append(" ").append(alias.get(i));
            }
            if(onJoin && null != joinInfo){
                Object joinConditions = joinInfo.get(i - 1);
                builder.append(SQL_ON_CONDITION).append(joinCondition(
                        //alias left, alias right
                        String.valueOf(alias.get(i - 1)), String.valueOf(alias.get(i)),
                        Objects.requireNonNull(Json.fromJson(Json.toJson(joinConditions, null), SqlJoinCondition.class))));
                onJoin = false;
            }
            if(i + 1 < tables.size() && null != alias){
                builder.append(SQL_INNER_JOIN);
                onJoin = true;
            }
        }
        return builder.toString();
    }

    private static String joinCondition(String aliasLeft, String aliasRight,
                                 List<SqlJoinCondition> joinConditions){
        //For example: t1.column1
        joinConditions.forEach( joinCondition ->{
            String left = joinCondition.getLeft();
            if(!left.contains(DEFAULT_TABLE_COLUMN_SEPARATOR)){
                joinCondition.setLeft(StringUtils.isNotBlank(aliasLeft)? aliasLeft + DEFAULT_TABLE_COLUMN_SEPARATOR + joinCondition.getLeft() : joinCondition.getLeft());
            }
            String right = joinCondition.getRight();
            if(!right.contains(DEFAULT_TABLE_COLUMN_SEPARATOR)){
                joinCondition.setRight(StringUtils.isNotBlank(aliasRight)? aliasRight + DEFAULT_TABLE_COLUMN_SEPARATOR + joinCondition.getRight() : joinCondition.getRight());
            }
        });
        SqlJoinCondition[] conditions = new SqlJoinCondition[joinConditions.size()];
        joinConditions.toArray(conditions);
        return StringUtils.join(conditions, SQL_AND_CONDITION);
    }

    /**
     * Sql join condition
     */
    public static class SqlJoinCondition {
        private String left;
        private String right;
        private String condition;
        public String getLeft() {
            return left;
        }

        public void setLeft(String left) {
            this.left = left;
        }

        public String getRight() {
            return right;
        }

        public void setRight(String right) {
            this.right = right;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        @Override
        public String toString(){
            return StringUtils.join(new String[]{left, condition, right}, " ");
        }
    }
}
