package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.column;

import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
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
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers.AbstractPartitionedSubExchangisJobHandler;
import com.webank.wedatasphere.exchangis.job.utils.ColumnDefineUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.ErrorException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Provide method to autofill columns
 */
public abstract class AutoColumnSubExchangisJobHandler extends AbstractPartitionedSubExchangisJobHandler {
    /**
     * Auto type name
     */
    public static final String AUTO_TYPE = "[Auto]";

    /**
     * Type for partition columns
     */
    public static final String PART_COLUMN_TYPE = "<partition>";

    /**
     * Database
     */
    private static final JobParamDefine<String> DATABASE = JobParams.define(JobParamConstraints.DATABASE);

    /**
     * Table
     */
    private static final JobParamDefine<String> TABLE = JobParams.define(JobParamConstraints.TABLE);

    /**
     * If add partition columns
     */
    private static final JobParamDefine<Boolean> ADD_PART_COLUMNS = JobParams.define(JobParamConstraints.ADD_PART_COLUMNS, JobParamConstraints.ADD_PART_COLUMNS, rawValue -> {
        if (null == rawValue){
            return false;
        }
        return Boolean.parseBoolean(String.valueOf(rawValue));
    }, Object.class);




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
                    columns.stream().noneMatch(column -> StringUtils.isBlank(column.getType()) || column.getType().equals(AUTO_TYPE) || (null == column.getIndex() && null == column.getValue()));
            if (!complete){
                JobParamSet paramSet = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE);
                doFillColumns(paramSet, subExchangisJob.getSourceType(), columns);
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
                doFillColumns(paramSet, subExchangisJob.getSinkType(), columns);
            }
        }
    }


    /**
     * Do fill column
     * @param columns columns
     */
    protected void doFillColumns(JobParamSet paramSet, String dsType, List<SubExchangisJob.ColumnDefine> columns){
        List<MetaColumn> metaColumns = getMetaColumns(paramSet);
        if (Objects.nonNull(metaColumns) && !metaColumns.isEmpty()){
            if (columns.size() <= 0){
                for(MetaColumn metaColumn : metaColumns){
                    SubExchangisJob.ColumnDefine columnDefine = ColumnDefineUtils
                            .getColumn(metaColumn.getName(), metaColumn.getType());
                    columnDefine.setIndex(metaColumn.getIndex());
                    columns.add(columnDefine);
                }
                if (ADD_PART_COLUMNS.getValue(paramSet)){
                    Map<String, String> getPartColumns = getPartColumns(paramSet, dsType);
                    getPartColumns.forEach((key, value) -> {
                        SubExchangisJob.ColumnDefine columnDefine = ColumnDefineUtils.getColumn(key, "STRING");
                        columnDefine.setValue(value);
                        columns.add(columnDefine);
                    });
                }
            } else {
                Map<String, String> partColumns = new HashMap<>();
                AtomicBoolean partLoad = new AtomicBoolean(false);
                completeColumns(columns, metaColumns, () -> {
                    if (!partLoad.get()){
                        partColumns.putAll(getPartColumns(paramSet, dsType));
                        partLoad.set(true);
                    }
                    return partColumns;
                });
            }
        }
    }

    /**
     * Get partition columns
     * @param paramSet param set
     * @param dsType data source type name
     * @return column map
     */
    private Map<String, String> getPartColumns(JobParamSet paramSet, String dsType){
        List<String> partitionKeys = getPartitionKeys(paramSet, dsType);
        Map<String, String> partColumns = new HashMap<>();
        if (!partitionKeys.isEmpty()){
            Map<String, String> partitions = TABLE_PARTITION.getValue(paramSet);
            if (Objects.nonNull(partitions)){
                partitions.forEach((partKey, partValue) -> {
                    if (partitionKeys.contains(partKey)){
                        partColumns.put(partKey, partValue);
                    }
                });
            }
        }
        return partColumns;
    }
    /**
     * Get columns from metadata server
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
     * Get partition keys
     * 1) First try to get from cache
     * 2) Second get from metadata server
     * @param paramSet param set
     * @return columns
     */
    protected List<String> getPartitionKeys(JobParamSet paramSet, String dsType){
        // Check if the partitioned data source
        ExchangisDataSourceDefinition definition =
                Objects.requireNonNull(getBean(ExchangisDataSourceContext.class)).getExchangisDsDefinition(dsType);
        if  (Objects.isNull(definition) || !definition.isPartitioned()){
            return Collections.emptyList();
        }
        return PARTITION_KEYS.getValue(paramSet);
    }

    protected final void completeColumns(List<SubExchangisJob.ColumnDefine> columns, List<MetaColumn> metaColumns){
        completeColumns(columns, metaColumns, null);
    }
    /**
     *
     * @param columns columns
     * @param metaColumns meta columns
     */
    protected final void completeColumns(List<SubExchangisJob.ColumnDefine> columns, List<MetaColumn> metaColumns,
                                         Supplier<Map<String, String>> getPartColumns){
        Map<String, MetaColumn> metaColumnMap = metaColumns.stream().collect(Collectors.toMap(
                MetaColumn::getName, metaColumn -> metaColumn, (left, right) -> left
        ));
        for (int i = 0; i < columns.size(); i ++){
            SubExchangisJob.ColumnDefine column = columns.get(i);
            String name = column.getName();
            MetaColumn metaColumn = metaColumnMap.get(name);
            if (Objects.nonNull(metaColumn)){
                SubExchangisJob.ColumnDefine completedCol = ColumnDefineUtils.getColumn(name, metaColumn.getType(), metaColumn.getIndex());
                completedCol.setValue(column.getValue());
                columns.set(i, completedCol);
            } else {
                String type = column.getType();
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)){
                    if  ((AUTO_TYPE.equals(type) || PART_COLUMN_TYPE.equals(type)) && null != getPartColumns){
                        Map<String, String> partColumns = getPartColumns.get();
                        String value = partColumns.get(name);
                        if (null != value){
                            SubExchangisJob.ColumnDefine completedCol = ColumnDefineUtils.getColumn(name, type);
                            completedCol.setValue(value);
                            columns.set(i, completedCol);
                            continue;
                        }
                    } else if (Objects.nonNull(column.getValue())){
                        column.setIndex(null);
                        continue;
                    }
                }
                throw new ExchangisJobException.Runtime(-1, "Unable to find match column: [" + name + "] (表中找不到对应的字段)", null);
            }
        }
    }

    /**
     * If auto fill column
     * @return bool
     */
    protected abstract boolean autoColumn();

}
