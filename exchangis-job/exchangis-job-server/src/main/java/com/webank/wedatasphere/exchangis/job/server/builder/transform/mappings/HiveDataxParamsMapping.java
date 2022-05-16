package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;

public class HiveDataxParamsMapping extends AbstractExchangisJobParamsMapping{
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
        return "hive";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "datax".equalsIgnoreCase(engineType);
    }
}
