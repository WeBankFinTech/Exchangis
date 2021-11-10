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
    String getStrKey();

    /**
     * Value of parameter
     * @return nullable
     */
    T getValue();

    /**
     * Get value of parameter form source obj
     * @param source
     * @return
     */
    T getValue(Object source);
    /**
     * Set string key
     * @param key nullable
     */
    void setKey(String key);

    /**
     * Set value
     * @param value nullable
     */
    void setValue(T value);

    JobParam<T> loadValue(Object source);
    /**
     * Value loader
     * @param valueLoader
     * @param <U>
     */
    <U>void setValueLoader(BiFunction<String, U, T> valueLoader);

    /**
     * Is temporary
     * @return
     */
    default boolean isTemp(){
        return false;
    }

    default void setTemp(boolean isTemp){
        //Empty
    }
}
