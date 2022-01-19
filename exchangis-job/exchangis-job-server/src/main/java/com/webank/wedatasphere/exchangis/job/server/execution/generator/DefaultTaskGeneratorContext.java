package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;

/**
 * Default generator context
 */
public class DefaultTaskGeneratorContext implements TaskGeneratorContext {

    private JobLogListener jobLogListener;

    public DefaultTaskGeneratorContext(){

    }
    public DefaultTaskGeneratorContext(JobLogListener jobLogListener){
        this.jobLogListener = jobLogListener;
    }

    @Override
    public JobLogListener gtJobLogListener() {
        return this.jobLogListener;
    }
}
