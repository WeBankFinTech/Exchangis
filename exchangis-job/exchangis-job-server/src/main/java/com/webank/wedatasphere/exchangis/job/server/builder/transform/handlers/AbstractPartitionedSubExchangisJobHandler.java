package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;

import org.apache.commons.lang3.StringUtils;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Job handler of partition
 */
public abstract class AbstractPartitionedSubExchangisJobHandler extends AuthEnabledSubExchangisJobHandler{
    /**
     * Database
     */
    private static final JobParamDefine<String>  HIVE_DATABASE = JobParams.define("hiveDatabase", JobParamConstraints.DATABASE);
    private static final JobParamDefine<String>  DATABASE = JobParams.define("database", JobParamConstraints.DATABASE);

    /**
     * Table
     */
    private static final JobParamDefine<String> HIVE_TABLE = JobParams.define("hiveTable", JobParamConstraints.TABLE);
    private static final JobParamDefine<String> TABLE = JobParams.define("table", JobParamConstraints.TABLE);

    /**
     * Table partition
     */
    public static final JobParamDefine<Map<String, String>> TABLE_PARTITION = JobParams.define(JobParamConstraints.PARTITION);

    /**
     * Partition keys
     */
    protected static final JobParamDefine<List<String>> PARTITION_KEYS = JobParams.define("partitionKeys", paramSet -> {
        JobParam<String> dataSourceId = paramSet.get(JobParamConstraints.DATA_SOURCE_ID);
        List<String> partitionKeys = new ArrayList<>();
        String database = DATABASE.getValue(paramSet);
        if (StringUtils.isBlank(database)) {
            database = HIVE_DATABASE.getValue(paramSet);
        }
        String table = TABLE.getValue(paramSet);
        if (StringUtils.isBlank(table)) {
            table = HIVE_TABLE.getValue(paramSet);
        }
        JobParam<String> dsCreator = paramSet.get(JobParamConstraints.DATA_SOURCE_CREATOR);
        String dsOwner = Objects.nonNull(dsCreator) ? dsCreator.getValue() : GlobalConfiguration.getAdminUser();
        try {
            partitionKeys = Objects.requireNonNull(getBean(MetadataInfoService.class)).getPartitionKeys(
                    Optional.ofNullable(dsOwner).orElse(getJobBuilderContext().getOriginalJob().getCreateUser()),
                    Long.parseLong(dataSourceId.getValue()), database, table);
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(e.getErrCode(), e.getMessage(), e.getCause());
        }
        return partitionKeys;
    });

    /**
     * Partition values
     */
    protected static final JobParamDefine<String> PARTITION_VALUES = JobParams.define("partitionValues", paramSet -> {
        Map<String, String> partitions = Optional.ofNullable(TABLE_PARTITION.getValue(paramSet)).orElse(new HashMap<>());
        //Try to find actual partition from table properties
        List<String> partitionKeys = PARTITION_KEYS.getValue(paramSet);
        String[] partitionColumns = Objects.isNull(partitionKeys)? new String[0]: partitionKeys.toArray(new String[0]);
        if (partitionColumns.length > 0 && partitions.size() != partitionColumns.length){
            throw new ExchangisJobException.Runtime(-1, "Unmatched partition list: [" +
                    org.apache.commons.lang3.StringUtils.join(partitionColumns, ",") + "]", null);
        }
        if (partitionColumns.length > 0){
            return Arrays.stream(partitionColumns).map(partitions::get).collect(Collectors.joining(","));
        }
        return null;
    });
}
