package com.webank.wedatasphere.exchangis.job.domain.params;


import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Util class
 */
public class JobParams {

    public static <T, U>JobParam<T> define(String key, BiFunction<String, U, T> valueLoader){
        return new DefaultJobParam<>(key, key, valueLoader);
    }

    /**
     * Define default job param
     * @param key key
     * @param valueLoader value loader
     * @param mappingKey mapping key
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U>JobParam<T> define(String key, String mappingKey, BiFunction<String, U, T> valueLoader){
        return new DefaultJobParam<>(key, mappingKey, valueLoader);
    }

    public static JobParam<Object> define(String key){
        return define(key, key);
    }

    public static <T>JobParam<T> defineWithValue(String key, T value){
        JobParam<T> result =  define(key, () -> value);
        result.setParamValue(value);
        return result;
    }
    /**
     * Use default value loader: (string, JobParamSet) -> Object
     * @param key key
     * @param mappingKey mapping key
     * @return
     */
    public static JobParam<Object> define(String key, String mappingKey){
        return new DefaultJobParam<>(key, mappingKey, (BiFunction<String, JobParamSet, Object>) (paramKey, source) -> {
            if(Objects.nonNull(source)){
                return source.get(mappingKey);
            }
            return null;
        });
    }

    /**
     * Use operator instead of value loader
     * @param key
     * @param operator
     * @param <T>
     * @return
     */
    public static <T>JobParam<T> define(String key, Supplier<T> operator){
        return new DefaultJobParam<>(key, key, (paramKey, source) -> operator.get());
    }

}
