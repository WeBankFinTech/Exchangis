package com.webank.wedatasphere.exchangis.job.builder;


import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;

/**
 * Builder context
 */
public class ExchangisJobBuilderContext{

    /**
     * Origin job
     */
    private ExchangisJob originalJob;

    public ExchangisJobBuilderContext(){

    }

    public ExchangisJobBuilderContext(ExchangisJob originalJob){
        this.originalJob = originalJob;
    }

    public ExchangisJob getOriginalJob() {
        return originalJob;
    }

    public void setOriginalJob(ExchangisJob originalJob) {
        this.originalJob = originalJob;
    }
}
