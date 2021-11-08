package com.webank.wedatasphere.exchangis.job.domain.params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of JobParam<Type>
 */
public class JobParamSet {

    /**
     * Param set
     */
    private Map<String, JobParam<?>> jobParamStore = new ConcurrentHashMap<>();


    public JobParamSet append(JobParam<?> jobParam){
        jobParamStore.put(jobParam.getParamStrKey(),  jobParam);
        return this;
    }

    /**
     * Append
     * @param key custom key
     * @param jobParam job parameter
     */
    public JobParamSet append(String key, JobParam<?> jobParam){
        jobParamStore.put(key, jobParam);
        return this;
    }

    public JobParamSet combine(JobParamSet paramSet){
        Map<String, JobParam<?>> other = paramSet.jobParamStore;
        this.jobParamStore.putAll(other);
        return this;
    }
    /**
     * Get
     * @param key param key
     * @return param entity
     */
    public JobParam<?> get(String key){

        return jobParamStore.get(key);
    }

    /**
     * Remove
     * @param key param key
     * @return removed param entity
     */
    @SuppressWarnings("unchecked")
    public <T>JobParam<T> remove(String key){
        return (JobParam<T>) jobParamStore.remove(key);
    }

    /**
     * To List
     * @return param list
     */
    public List<JobParam<?>> toList(){
        return new ArrayList<>(jobParamStore.values());
    }

}
