package com.webank.wedatasphere.exchangis.dss.appconn.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefQueryOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the function from the mapper "BDPJettyServerHelper.jacksonJson()"
 */
public class JsonExtension {

    private final static Logger LOG = LoggerFactory.getLogger(JsonExtension.class);

    /**
     * Convert object using serialization and deserialization
     *
     * @param simpleObj simpleObj
     * @param tClass    type class
     * @param <T>       T
     * @return result
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object simpleObj, Class<?> tClass, Class<?>... parameters) throws ExternalOperationFailedException{
        ObjectMapper mapper = getMapper();
        try {
            if (parameters.length > 0) {
                return mapper.convertValue(simpleObj, mapper.getTypeFactory().constructParametricType(tClass, parameters));
            }
            return (T) mapper.convertValue(simpleObj, tClass);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(3130, "Fail to process method 'convert(" + simpleObj + ": " + simpleObj.getClass().getSimpleName() +
                    ", " + tClass.getSimpleName() + ": "+ Class.class + ", ...: " + Class.class + ")", e);
        }
    }

    public static ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
