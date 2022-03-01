package com.webank.wedatasphere.exchangis.datasource.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Json {
    public static final String PREFIX = "[";
    public static final String SUFFIX = "]";

    private static final ObjectMapper mapper;

    public static Logger logger = LoggerFactory.getLogger(Json.class);
    static {
        mapper = new ObjectMapper();
        //Custom the feature of serialization and deserialization
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //Enum
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
//            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        //Empty beans allowed
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //Ignore unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Cancel to scape no ascii
//            mapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), false);
    }

    public static ObjectMapper getMapper(){
        return mapper;
    }
    /**
     * Generate json string
     *
     * @param simpleObj object
     * @param viewModel model
     * @return string
     */
    public static String toJson(Object simpleObj, Class<?> viewModel) {
        ObjectWriter writer = mapper.writer();
        if (null != simpleObj) {
            try {
                if (null != viewModel) {
                    writer = writer.withView(viewModel);
                }
                return writer.writeValueAsString(simpleObj);
            } catch (JsonProcessingException e) {
                logger.warn("Fail to process method 'toJson(" + simpleObj + ": " + simpleObj.getClass() +
                        ", " + (viewModel != null ? viewModel.getSimpleName() : null) + ")'", e);
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<?> tClass, Class<?>... parameters) {
        if (StringUtils.isNotBlank(json)) {
            try {
                if (parameters.length > 0) {
                    return (T) mapper.readValue(json, mapper.getTypeFactory().constructParametricType(tClass, parameters));
                }
                return (T) mapper.readValue(json, tClass);
            } catch (Exception e) {
                logger.warn("Fail to process method 'fromJson(" +
                        (json.length() > 5 ? json.substring(0, 5) + "..." : json) + ": " + json.getClass() +
                        ", " + tClass.getSimpleName() + ": "+ Class.class + ", ...: " + Class.class + ")", e);
                return null;
            }
        }
        return null;
    }

    public static <T> T fromJson(String json, JavaType javaType) {
        if(StringUtils.isNotBlank(json)){
            try{
                return mapper.readValue(json, javaType);
            }catch (Exception e){
                logger.warn("Fail to process method 'fromJson(" +
                        (json.length() > 5 ? json.substring(0, 5) + "..." : json) + ": " + json.getClass() +
                        ", " + javaType.getTypeName() + ": "+ JavaType.class + ")", e);
                return null;
            }
        }
        return null;
    }
    /**
     * Convert object using serialization and deserialization
     *
     * @param simpleObj simpleObj
     * @param tClass    type class
     * @param <T>       T
     * @return result
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object simpleObj, Class<?> tClass, Class<?>... parameters) {
        try {
            if (parameters.length > 0) {
                return mapper.convertValue(simpleObj, mapper.getTypeFactory().constructParametricType(tClass, parameters));
            }
            return (T) mapper.convertValue(simpleObj, tClass);
        } catch (Exception e) {
            logger.warn("Fail to process method 'convert(" + simpleObj + ": " + simpleObj.getClass().getSimpleName() +
                    ", " + tClass.getSimpleName() + ": "+ Class.class + ", ...: " + Class.class + ")", e);
            return null;
        }
    }

    public static <T> T convert(Object simpleObj, JavaType javaType){
        try {
            return mapper.convertValue(simpleObj, javaType);
        } catch (Exception e) {
            logger.warn("Fail to process method 'convert(" + simpleObj + ": " + simpleObj.getClass().getSimpleName() +
                    ", " + javaType.getTypeName() + ": "+ JavaType.class + ")", e);
            return null;
        }
    }
}
