package com.webank.wedatasphere.exchangis.job.domain.params;


import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    public static <T>JobParamDefine<T> define(String key, Function<JobParamSet, T> valueLoader){
        return new JobParamDefine<>(key, valueLoader);
    }

    public static <U, T>JobParamDefine<T> define(String key, Function<U, T> valueLoader, Class<U> type){
        return new JobParamDefine<>(key, valueLoader, type);
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


    /**
     * Use default value loader: (string, JobParamSet) -> ?
     * @param key key
     * @param mappingKey mapping key
     * @return
     */
    public static <T>JobParamDefine<T> define(String key, String mappingKey){
        return define(key, new String[]{mappingKey}, result-> result, (Class<T>)null);
    }

    public static <T, U>JobParamDefine<T> define(String key, String mappingKey, Function<U, T> transform, Class<U> inputType){
        return define(key, new String[]{mappingKey}, transform, inputType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T, U>JobParamDefine<T> define(String key, String[] mappingKeys, Function<U, T> transform, Class<U> inputType){
        return new JobParamDefine<>(key, (paramKey, source) -> {
            if (Objects.nonNull(source)) {
                if (source instanceof JobParamSet) {
                    for (String mappingKey : mappingKeys) {
                        JobParam<?> result = ((JobParamSet) source).remove(mappingKey);
                        if (Objects.nonNull(result)) {
                            return transform.apply((U)result.getValue());
                        }
                    }
                    return null;
                } else if (source instanceof Map) {
                    for (String mappingKey : mappingKeys) {
                        Object result = ((Map) source).remove(mappingKey);
                        if (Objects.nonNull(result)) {
                            return transform.apply((U)result);
                        }
                    }
                    return null;
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T>JobParamDefine<T> define(String key, Supplier<T> operator){
        return new JobParamDefine<>(key, (paramKey, source) -> {
            T finalValue = null;
            if (Objects.nonNull(source)) {
                if (source instanceof JobParamSet) {
                    JobParam<?> result = ((JobParamSet) source).get(key);
                    if (Objects.nonNull(result)){
                        finalValue = (T)result.getValue();
                    }
                } else if (source instanceof Map) {
                    Object result = ((Map) source).get(key);
                    if (Objects.nonNull(result)){
                        return (T)result;
                    }
                }
            }
            return Objects.nonNull(finalValue) ? finalValue : operator.get();
        });
    }

    public static <T>JobParam<T> newOne(String key, T value){
        return newOne(key, value, false);
    }

    public static <T>JobParam<T> newOne(String key, T value, boolean isTemp){
        JobParamDefine<T> result =  define(key, () -> value);
        JobParam<T> param = result.newParam(value);
        param.setTemp(isTemp);
        return param;
    }
}
