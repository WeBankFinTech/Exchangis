package com.webank.wedatasphere.exchangis.job.domain.params;


import java.util.HashMap;
import java.util.Map;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <U>JobParamDefine<U> define(String key){
        return new JobParamDefine<>(key, (paramKey, source) -> {
            if(Objects.nonNull(source)){
                if(source instanceof JobParamSet) {
                    JobParam<?> result = ((JobParamSet)source).get(key);
                    return Objects.nonNull(result)? (U)result.getValue() : null;
                }else if (source instanceof Map){
                    return (U) ((Map)source).get(key);
                }
            }
            return null;
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T>JobParamDefine<T> define(String key, Class<T> valueType){
        return new JobParamDefine<>(key,(paramKey, source) -> {
            if(Objects.nonNull(source)){
                if(source instanceof JobParamSet) {
                    JobParam<?> result = ((JobParamSet)source).get(key);
                    return Objects.nonNull(result)? (T)result.getValue() : null;
                }else if (source instanceof Map){
                    return (T) ((Map)source).get(key);
                }
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <U>JobParamDefine<U> define(String key, String mappingKey){
        return new JobParamDefine<>(key, (paramKey, source) -> {
            if(Objects.nonNull(source)){
                if(source instanceof JobParamSet) {
                    JobParam<?> result = ((JobParamSet)source).remove(mappingKey);
                    return Objects.nonNull(result)? (U)result.getValue() : null;
                }else if (source instanceof Map){
                    return (U) ((Map)source).remove(mappingKey);
                }
            }
            return null;
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T>JobParamDefine<T> define(String key, String mappingKey, Class<T> valueType){
        return new JobParamDefine<T>(key, (paramKey, source) -> {
            if(Objects.nonNull(source)){
                if(source instanceof JobParamSet) {
                    JobParam<?> result = ((JobParamSet)source).remove(mappingKey);
                    return Objects.nonNull(result)? (T)result.getValue() : null;
                }else if (source instanceof Map){
                    return (T) ((Map)source).remove(mappingKey);
                }
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
