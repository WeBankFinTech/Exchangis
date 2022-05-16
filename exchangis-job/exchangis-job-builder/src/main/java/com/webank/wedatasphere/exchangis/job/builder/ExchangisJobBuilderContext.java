package com.webank.wedatasphere.exchangis.job.builder;


import com.webank.wedatasphere.exchangis.job.builder.api.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder context
 */
public class ExchangisJobBuilderContext {

    /**
     * Origin job
     */
    protected ExchangisJobInfo originalJob;

    /**
     * Current builder
     */
    protected ExchangisJobBuilder<?, ?> currentBuilder;

    private Map<String, Object> env = new HashMap<>();

    private Map<String, Map<String, Object>> datasourceParams = new HashMap<>();

    public ExchangisJobBuilderContext() {

    }

    public ExchangisJobBuilderContext(ExchangisJobInfo originalJob){
        this.originalJob = originalJob;
    }


    public ExchangisJobInfo getOriginalJob() {
        return originalJob;
    }

    public void setOriginalJob(ExchangisJobInfo originalJob) {
        this.originalJob = originalJob;
    }

    public void putDatasourceParam(String datasourceId, Map<String, Object> datasourceParams) {
        this.datasourceParams.put(datasourceId, datasourceParams);
    }
    public Map<String, Object> getDatasourceParam(String datasourceId) {
        return this.datasourceParams.get(datasourceId);
    }

    public void putEnv(String name, Object value) {
        this.env.put(name, value);
    }

    public Object getEnv (String name) {
        return this.env.get(name);
    }

    public boolean containsEnv(String name) {
        return this.env.containsKey(name);
    }

    public ExchangisJobBuilder<?, ?> getCurrentBuilder() {
        return currentBuilder;
    }

    public void setCurrentBuilder(ExchangisJobBuilder<?, ?> currentBuilder) {
        this.currentBuilder = currentBuilder;
    }
}
