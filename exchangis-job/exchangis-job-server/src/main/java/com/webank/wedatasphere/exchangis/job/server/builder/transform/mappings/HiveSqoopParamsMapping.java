package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;

/**
 * Mapping of Hive in Sqoop
 */
public class HiveSqoopParamsMapping extends AbstractExchangisJobParamsMapping {


    @Override
    public String dataSourceType() {
        return "hive";
    }

    @Override
    public boolean acceptEngine(String engineType) {
        return "sqoop".equalsIgnoreCase(engineType);
    }

    @Override
    public JobParamDefine<?>[] sourceMappings() {
        return new JobParamDefine[]{
                //Unit test
                JobParams.define("version", "source.version"),
                JobParams.define("version", () -> "1.4.6")
        };
    }

    @Override
    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[0];
    }
}
