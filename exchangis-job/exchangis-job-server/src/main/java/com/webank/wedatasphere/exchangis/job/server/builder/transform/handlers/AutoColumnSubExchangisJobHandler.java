package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import com.webank.wedatasphere.exchangis.datasource.core.domain.MetaColumn;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.utils.ColumnDefineUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provide method to autofill columns
 */
public abstract class AutoColumnSubExchangisJobHandler extends AbstractLoggingSubExchangisJobHandler{
    /**
     * Auto type name
     */
    private static final String AUTO_TYPE = "[Auto]";

    /**
     * Handle source columns
     * @param columns columns
     */
    protected void handleSrcColumns(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx,
                                    List<SubExchangisJob.ColumnDefine> columns) {
        if (autoColumn()){
            boolean complete = columns.stream().noneMatch(column -> StringUtils.isBlank(column.getType()) || column.getType().equals(AUTO_TYPE) || null == column.getIndex());
            if (!complete){
                JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
                doFillColumns(paramSet, columns);
            }
        }
    }

    /**
     * Handle sink columns
     * @param columns columns
     */
    protected void handleSinkColumns(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx,
                                     List<SubExchangisJob.ColumnDefine> columns){
        if (autoColumn()){
            boolean complete = columns.stream().noneMatch(column -> StringUtils.isBlank(column.getType()) || column.getType().equals(AUTO_TYPE));
            if (!complete){
                JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
                doFillColumns(paramSet, columns);
            }
        }
    }

    /**
     * If auto fill column
     * @return bool
     */
    protected boolean autoColumn(){
        return true;
    }

    /**
     * Do fill column
     * @param columns columns
     */
    protected void doFillColumns(JobParamSet paramSet, List<SubExchangisJob.ColumnDefine> columns){
        List<MetaColumn> metaColumns = getMetaColumns(paramSet);
        if (Objects.nonNull(metaColumns) && !metaColumns.isEmpty()){
            if (columns.size() <= 0){
                for(MetaColumn metaColumn : metaColumns){
                    SubExchangisJob.ColumnDefine columnDefine = ColumnDefineUtils
                            .getColumn(metaColumn.getName(), metaColumn.getType());
                    columnDefine.setIndex(metaColumn.getIndex());
                    columns.add(columnDefine);
                }
            } else {
                completeColumns(columns, metaColumns);
            }
        }
    }

    /**
     * Get columns for metadata server
     * @param paramSet param set
     * @return
     */
    protected List<MetaColumn> getMetaColumns(JobParamSet paramSet){
        return Collections.emptyList();
    }
    /**
     *
     * @param columns columns
     * @param metaColumns meta columns
     */
    protected final void completeColumns(List<SubExchangisJob.ColumnDefine> columns, List<MetaColumn> metaColumns){
        Map<String, MetaColumn> metaColumnMap = metaColumns.stream().collect(Collectors.toMap(
                MetaColumn::getName, metaColumn -> metaColumn, (left, right) -> left
        ));
        for (int i = 0; i < columns.size(); i ++){
            SubExchangisJob.ColumnDefine column = columns.get(i);
            String name = column.getName();
            MetaColumn metaColumn = metaColumnMap.get(name);
            if (Objects.isNull(metaColumn)){
                throw new ExchangisJobException.Runtime(-1, "Unable to find match column: [" + name + "] (表中找不到对应的字段)", null);
            }
            columns.set(i, ColumnDefineUtils.getColumn(name, metaColumn.getType(), metaColumn.getIndex()));
        }
    }

}
