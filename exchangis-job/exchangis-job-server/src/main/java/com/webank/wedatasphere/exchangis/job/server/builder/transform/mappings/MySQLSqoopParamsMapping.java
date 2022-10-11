package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;

public class MySQLSqoopParamsMapping extends AbstractExchangisJobParamsMapping{
    @Override
    public JobParamDefine<?>[] sourceMappings() {
        return new JobParamDefine[0];
    }

    @Override
    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[0];
    }

    @Override
    public String dataSourceType() {
        return "mysql";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "sqoop".equalsIgnoreCase(engineType);
    }
}
