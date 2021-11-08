package com.webank.wedatasphere.exchangis.job.domain.params;


import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Util class
 */
public class JobParams {

    /**
     * Define default job param
     * @param key key
     * @param valueLoader value loader
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U>JobParamDefine<T> define(String key, BiFunction<String, U, T> valueLoader){
        return new JobParamDefine<>(key, valueLoader);
    }

    public static JobParamDefine<?> define(String key){
        return new JobParamDefine<>(key, (BiFunction<String, JobParamSet, ?>) (paramKey, source) -> {
            if(Objects.nonNull(source)){
                return source.get(key);
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static <T>JobParamDefine<T> define(String key, Class<T> valueType){
        return new JobParamDefine<T>(key, (BiFunction<String, JobParamSet, T>) (paramKey, source) -> {
            if(Objects.nonNull(source)){
                return (T)source.get(key);
            }
            return null;
        });
    }
    /**
     * Use default value loader: (string, JobParamSet) -> ?
     * @param key key
     * @param mappingKey mapping key
     * @return
     */
    public static JobParamDefine<?> define(String key, String mappingKey){
        return new JobParamDefine<>(key, (BiFunction<String, JobParamSet, ?>) (paramKey, source) -> {
            if(Objects.nonNull(source)){
                return source.remove(mappingKey);
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static <T>JobParamDefine<T> define(String key, String mappingKey, Class<T> valueType){
        return new JobParamDefine<T>(key, (BiFunction<String, JobParamSet, T>) (paramKey, source) -> {
            if(Objects.nonNull(source)){
                return (T)source.remove(mappingKey);
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
    public static <T>JobParamDefine<T> define(String key, Supplier<T> operator){
        return new JobParamDefine<>(key, (paramKey, source) -> operator.get());
    }

    public static <T>JobParam<T> newOne(String key, T value){
        JobParamDefine<T> result =  define(key, () -> value);
        return result.newParam(value);
    }

}
