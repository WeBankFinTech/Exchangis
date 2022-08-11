package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;
import org.springframework.context.ApplicationContext;

/**
 * Spring generator context (with application context)
 */
public class SpringTaskGeneratorContext implements TaskGeneratorContext {

    private JobLogListener jobLogListener;

    /**
     * Spring application context
     */
    private ApplicationContext applicationContext;

    public SpringTaskGeneratorContext(){

    }
    public SpringTaskGeneratorContext(JobLogListener jobLogListener,
                                      ApplicationContext applicationContext){
        this.jobLogListener = jobLogListener;
        this.applicationContext = applicationContext;
    }

    @Override
    public JobLogListener getJobLogListener() {
        return this.jobLogListener;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
