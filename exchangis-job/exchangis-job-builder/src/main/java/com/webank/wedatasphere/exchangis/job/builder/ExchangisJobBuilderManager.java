package com.webank.wedatasphere.exchangis.job.builder;

import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;

public class ExchangisJobBuilderManager {

    public static ExchangisJobBuilder getJobBuilder(EngineTypeEnum engineType) throws Exception {
        switch (engineType) {
            case DATAX:
                return new DataXJobBuilder();
            case SQOOP:
                return new SqoopJobBuilder();
            default:
                throw new Exception("Engine type not supported");
        }
    }
}
