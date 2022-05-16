package com.webank.wedatasphere.exchangis.job.server.execution.generator;

import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.job.listener.JobLogListener;

/**
 * Default generator context
 */
public class DefaultTaskGeneratorContext implements TaskGeneratorContext {

    private JobLogListener jobLogListener;

    private MetadataInfoService metadataInfoService;

    public DefaultTaskGeneratorContext(){

    }
    public DefaultTaskGeneratorContext(JobLogListener jobLogListener,
                                       MetadataInfoService metadataInfoService){
        this.jobLogListener = jobLogListener;
        this.metadataInfoService = metadataInfoService;
    }

    @Override
    public JobLogListener getJobLogListener() {
        return this.jobLogListener;
    }

    @Override
    public MetadataInfoService getMetadataInfoService() {
        return metadataInfoService;
    }
}
