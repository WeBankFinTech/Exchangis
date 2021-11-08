package com.webank.wedatasphere.exchangis.job.domain.params;

import java.util.Objects;
import java.util.function.BiFunction;

public class DefaultJobParam<T> implements JobParam<T> {
    private String key;

    /**
     * Map the key in source
     */
    private String mappingKey;

    private T value;

    private BiFunction<String, Object, T> valueLoader;

    private Object sourceReference = null;

    private Class<?> sourceType = Object.class;

    DefaultJobParam(){

    }

    <U>DefaultJobParam(String key, String mappingKey, BiFunction<String, U, T> valueLoader){
        this.key = key;
        this.mappingKey = mappingKey;
        setValueLoader(valueLoader);
    }
    @Override
    public String getParamStrKey() {
        return key;
    }

    @Override
    public T getParamValue() {
        return value;
    }

    @Override
    public T getParamValue(Object source) {
        if(Objects.nonNull(source)) {
            if (!Objects.equals(sourceReference, source) &&
                    Objects.nonNull(valueLoader) &&
                    sourceType.isAssignableFrom(source.getClass())) {
                this.value = this.valueLoader.apply(key, source);
                this.sourceReference = source;
            }
        }
        return value;
    }

    public T reloadParamValue(Object source){
        if(Objects.nonNull(source) &&
                Objects.nonNull(valueLoader) &&
                sourceType.isAssignableFrom(source.getClass())){
            this.value = this.valueLoader.apply(key, source);
            this.sourceReference = source;
        }
        return value;
    }
    @Override
    public void setParamKey(String key) {
        this.key = key;
    }

    @Override
    public void setParamValue(T value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> void setValueLoader(BiFunction<String, U, T> valueLoader) {
        Class<U> obj = null;
        this.valueLoader = (BiFunction<String, Object, T>) valueLoader;
    }
}
