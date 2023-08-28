package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;

import java.util.function.BiFunction;

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
                JobParams.define("version", "source.version" ),
                JobParams.define("version", () -> "1.4.7"),
                JobParams.define("tab", (BiFunction<String, JobParamSet, String>)(key, paramSet)->{
                    JobParams.define("version").newParam(paramSet).getValue();
                    return null;
                })
        };
    }

    @Override
    public JobParamDefine<?>[] sinkMappings() {
        return new JobParamDefine[0];
    }
}
