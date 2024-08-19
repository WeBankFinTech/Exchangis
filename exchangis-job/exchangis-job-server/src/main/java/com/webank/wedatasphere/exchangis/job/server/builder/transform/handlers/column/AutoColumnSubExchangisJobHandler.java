package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.datasource.core.domain.MetaColumn;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.AbstractLoggingSubExchangisJobHandler;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import com.webank.wedatasphere.exchangis.job.utils.ColumnDefineUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.ErrorException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provide method to autofill columns
 */
public abstract class AutoColumnSubExchangisJobHandler extends AbstractLoggingSubExchangisJobHandler {
    /**
     * Auto type name
     */
    public static final String AUTO_TYPE = "[Auto]";

    /**
     * Database
     */
    private static final JobParamDefine<String> DATABASE = JobParams.define(JobParamConstraints.DATABASE);

    /**
     * Table
     */
    private static final JobParamDefine<String> TABLE = JobParams.define(JobParamConstraints.TABLE);

    @Override
    public void handleJobSource(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        // Ignore
    }

    @Override
    public void handleJobSink(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx) throws ErrorException {
        // Ignore
    }

    /**
     * Handle source columns
     * @param columns columns
     */
    protected void handleSrcColumns(SubExchangisJob subExchangisJob, ExchangisJobBuilderContext ctx,
                                    List<SubExchangisJob.ColumnDefine> columns) {
        if (autoColumn()){
            boolean complete = Objects.nonNull(columns) && columns.size() > 0 &&
                    columns.stream().noneMatch(column -> StringUtils.isBlank(column.getType()) || column.getType().equals(AUTO_TYPE) || null == column.getIndex());
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
            boolean complete = Objects.nonNull(columns) && columns.size() > 0 &&
                    columns.stream().noneMatch(column -> StringUtils.isBlank(column.getType()) || column.getType().equals(AUTO_TYPE));
            if (!complete){
                JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK);
                doFillColumns(paramSet, columns);
            }
        }
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
     * @return columns
     */
    protected List<MetaColumn> getMetaColumns(JobParamSet paramSet){
        String database = DATABASE.getValue(paramSet);
        String table = TABLE.getValue(paramSet);
        JobParam<String> dataSourceId = paramSet.get(JobParamConstraints.DATA_SOURCE_ID);
        JobParam<String> dsCreator = paramSet.get(JobParamConstraints.DATA_SOURCE_CREATOR);
        String dsOwner = Objects.nonNull(dsCreator) ? dsCreator.getValue() : GlobalConfiguration.getAdminUser();
        try {
            return Objects.requireNonNull(getBean(MetadataInfoService.class)).getColumns(
                    Optional.ofNullable(dsOwner).orElse(getJobBuilderContext().getOriginalJob().getCreateUser()),
                    Long.valueOf(dataSourceId.getValue()), database, table);
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(e.getErrCode(), e.getMessage(), e.getCause());
        }
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

    /**
     * If auto fill column
     * @return bool
     */
    protected abstract boolean autoColumn();
}
