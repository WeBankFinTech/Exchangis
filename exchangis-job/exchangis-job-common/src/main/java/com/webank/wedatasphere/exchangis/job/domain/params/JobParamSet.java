package com.webank.wedatasphere.exchangis.job.domain.params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of JobParam<Type>
 */
public class JobParamSet {

    /**
     * Param set
     */
    private Map<String, JobParam<?>> jobParamStore = new ConcurrentHashMap<>();


    public JobParamSet add(JobParam<?> jobParam){
        jobParamStore.put(jobParam.getStrKey(),  jobParam);
        return this;
    }


    public JobParamSet add(JobParamDefine<?> jobParamDefine){
        return add(prepare(jobParamDefine));
    }


    /**
     * Append
     * @param key custom key
     * @param jobParam job parameter
     */
    public JobParamSet add(String key, JobParam<?> jobParam){
        jobParamStore.put(key, jobParam);
        return this;
    }

    public JobParamSet add(String key, JobParamDefine<?> jobParamDefine){
        return add(key, prepare(jobParamDefine));
    }

    @SuppressWarnings("unchecked")
    public <T>JobParam<T> load(JobParamDefine<T> jobParamDefine){
        return (JobParam<T>) jobParamStore.compute(jobParamDefine.getKey(), (key, value) -> {
            if (Objects.isNull(value)) {
                value = prepare(jobParamDefine);
            }
            return value;
        });
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

    /**
     * New param from definition
     * @param jobParam
     */
    private <T>JobParam<T> prepare(JobParamDefine<T> jobParam){
         return jobParam.newParam(this);
    }

}
