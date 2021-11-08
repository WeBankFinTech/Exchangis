package com.webank.wedatasphere.exchangis.job.domain.params;

import java.util.function.BiFunction;

/**
 * Define param entity
 * @param <T>
 */
public interface JobParam<T> {

    /**
     * Key of parameter, always string
     * @return nullable
     */
    String getParamStrKey();

    /**
     * Value of parameter
     * @return nullable
     */
    T getParamValue();

    /**
     * Get value of parameter form source obj
     * @param source
     * @return
     */
    T getParamValue(Object source);
    /**
     * Set string key
     * @param key nullable
     */
    void setParamKey(String key);

    /**
     * Set value
     * @param value nullable
     */
    void setParamValue(T value);

    /**
     * Value loader
     * @param valueLoader
     * @param <U>
     */
    <U>void setValueLoader(BiFunction<String, U, T> valueLoader);
}
