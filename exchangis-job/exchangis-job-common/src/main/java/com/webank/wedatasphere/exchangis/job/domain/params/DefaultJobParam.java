package com.webank.wedatasphere.exchangis.job.domain.params;

import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;

import java.util.Objects;
import java.util.function.BiFunction;

public class DefaultJobParam<T> implements JobParam<T> {
    private String key;

    private String mappingKey;

    private T value;

    private BiFunction<String, Object, T> valueLoader;

    private Object sourceReference = null;

    private boolean isTemp = false;

    private Class<?> sourceType = Object.class;

    /**
     * If computed param
     */
    private boolean computed = false;

    public DefaultJobParam(){

    }

    <U>DefaultJobParam(String key, BiFunction<String, U, T> valueLoader){
        this(key, null, valueLoader);
    }
    <U>DefaultJobParam(String key, String mappingKey, BiFunction<String, U, T> valueLoader){
        this.key = key;
        this.mappingKey = mappingKey;
        setValueLoader(valueLoader);
    }
    @Override
    public String getStrKey() {
        return key;
    }

    @Override
    public String getMappingKey() {
        return mappingKey;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public T getValue(Object source) {
        if(Objects.nonNull(source)) {
            if (!Objects.equals(sourceReference, source) &&
                    Objects.nonNull(valueLoader) &&
                    sourceType.isAssignableFrom(source.getClass())) {
                try {
                    this.value = this.valueLoader.apply(key, source);
                } catch (Exception e){
                    throw new ExchangisJobException.Runtime(ExchangisJobExceptionCode.TASK_PARM_ERROR.getCode(),
                            "Exception in loading param: [" + key + "]", e);
                }
                this.sourceReference = source;
            }
        }
        return value;
    }

    @Override
    public JobParam<T> loadValue(Object source){
        if(Objects.nonNull(source) &&
                Objects.nonNull(valueLoader) &&
                sourceType.isAssignableFrom(source.getClass())){
            try {
                this.value = this.valueLoader.apply(key, source);
            } catch (Exception e){
                throw new ExchangisJobException.Runtime(ExchangisJobExceptionCode.TASK_PARM_ERROR.getCode(),
                        "Exception in loading param: [" + key + "]", e);
            }
            this.sourceReference = source;
        }
        return this;
    }
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> void setValueLoader(BiFunction<String, U, T> valueLoader) {
        Class<U> obj = null;
        this.valueLoader = (BiFunction<String, Object, T>) valueLoader;
    }

    public BiFunction<String, Object, T> getValueLoader() {
        return valueLoader;
    }

    public void setComputed(boolean computed) {
        this.computed = computed;
    }

    @Override
    public boolean isComputed() {
        return computed;
    }

    @Override
    public boolean isTemp() {
        return isTemp;
    }

    @Override
    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }
}
