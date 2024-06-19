package com.webank.wedatasphere.exchangis.job.server.execution.parallel;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Tenancy parallel manager
 */
public class TenancyTaskParallelManager implements TaskParallelManager{
    private static final CommonVars<Integer> test = null;
    public TenancyTaskParallelManager(){

    }
    @Override
    public TaskParallelRule getOrCreateRule(String name, Operation operation) {
        return null;
    }
}
