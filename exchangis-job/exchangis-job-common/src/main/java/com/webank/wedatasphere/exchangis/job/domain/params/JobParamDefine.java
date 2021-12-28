package com.webank.wedatasphere.exchangis.job.domain.params;


import java.util.function.BiFunction;

/**
 * Definition of job params
 * @param <T>
 */
public class JobParamDefine<T>{
    private String key;

    private BiFunction<String, Object, T> valueLoader;

    @SuppressWarnings("unchecked")
    <U>JobParamDefine(String key, BiFunction<String, U, T> valueLoader){
        this.key = key;
        this.valueLoader = (BiFunction<String, Object, T>)valueLoader;
    }

    public String getKey() {
        return key;
    }

    public BiFunction<String, Object, T> getValueLoader() {
        return valueLoader;
    }

    public JobParam<T> newParam(Object source){
        JobParam<T> jobParam = new DefaultJobParam<>(key, valueLoader);
        return jobParam.loadValue(source);
    }
}
