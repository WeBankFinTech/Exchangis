package com.webank.wedatasphere.exchangis.job.domain.params;


import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Definition of job params
 * @param <T>
 */
public class JobParamDefine<T>{

    public static ThreadLocal<JobParamSet> defaultParam = new ThreadLocal<>();

    private String key;

    /**
     * Mapping key if exists
     */
    private String mappingKey;

    /**
     * If computed param
     */
    private boolean computed = false;

    private BiFunction<String, Object, T> valueLoader;

    <U>JobParamDefine(String key, BiFunction<String, U, T> valueLoader){
        this(key, null, valueLoader);
    }

    @SuppressWarnings("unchecked")
    <U>JobParamDefine(String key, String mappingKey, BiFunction<String, U, T> valueLoader){
        this.key = key;
        this.mappingKey = mappingKey;
        this.valueLoader = (BiFunction<String, Object, T>)valueLoader;
    }
    JobParamDefine(String key, Function<JobParamSet, T> valueLoader){
        this.key = key;
        this.valueLoader = (s, paramSet) -> valueLoader.apply((JobParamSet) paramSet);
    }

    @SuppressWarnings("unchecked")
    <U>JobParamDefine(String key, Function<U, T> valueLoader, Class<U> clazz){
        this.key = key;
        this.valueLoader = (s, paramSet) -> valueLoader.apply((U) paramSet);
    }

    public String getKey() {
        return key;
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public BiFunction<String, Object, T> getValueLoader() {
        return valueLoader;
    }

    /**
     * New one param
     * @param source source
     * @return
     */
    public JobParam<T> newParam(Object source){
        DefaultJobParam<T> jobParam = new DefaultJobParam<>(key, mappingKey, valueLoader);
        jobParam.setComputed(this.computed);
        return jobParam.loadValue(source);
    }

    public T newValue(Object source){
        return newParam(source).getValue();
    }
    /**
     * Get param from source (if param has been exist,it will not invoke the loadValue method)
     * @param source source
     * @return
     */
    public JobParam<T> get(Object source){
        JobParamSet paramSet = defaultParam.get();
        if (Objects.nonNull(paramSet)){
           return paramSet.load(this, source);
        }
        return newParam(source);
    }

    public T getValue(Object source){
        return get(source).getValue();
    }

    public boolean isComputed() {
        return computed;
    }

    public void setComputed(boolean computed) {
        this.computed = computed;
    }
}
