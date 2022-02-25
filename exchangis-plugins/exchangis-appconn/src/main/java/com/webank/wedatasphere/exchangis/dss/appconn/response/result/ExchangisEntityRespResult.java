package com.webank.wedatasphere.exchangis.dss.appconn.response.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.utils.JsonExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.httpclient.response.HttpResult;

import java.util.Objects;

/**
 * Convert the response body of http result to entity
 */

public class ExchangisEntityRespResult implements HttpResult {

    /**
     * Result held inner
     */
    private HttpResult result;

    public ExchangisEntityRespResult(HttpResult result) throws ExternalOperationFailedException {
        this.result = result;
        int statusCode = result.getStatusCode();
        if (statusCode != 200){
            throw new ExternalOperationFailedException(-1, "The response result has wrong status code: ["
                    + result.getStatusCode() + "], response body: [" + result.getResponseBody() + "]", null);
        }
    }

    @Override
    public String getContentType() {
        return result.getContentType();
    }

    @Override
    public String getUri() {
        return result.getUri();
    }

    @Override
    public int getStatusCode() {
        return result.getStatusCode();
    }

    @Override
    public void set(String responseBody, int statusCode, String url, String contentType) {
        this.set(responseBody, statusCode, url, contentType);
    }

    @Override
    public String getResponseBody() {
        return this.result.getResponseBody();
    }

    /**
     * Get the entity from the response body
     * @return entity
     */
    public <T>BasicMessageEntity<T> getEntity(Class<?> mainClass, Class<?>... parameters) throws ExternalOperationFailedException {
        String responseBody = this.result.getResponseBody();
        ObjectMapper mapper = JsonExtension.getMapper();
        Class<?>[] parametricClass = new Class<?>[parameters.length + 1];
        parametricClass[0] = mainClass;
        if (parameters.length > 0){
            System.arraycopy(parameters, 0, parametricClass, 1, parameters.length);
        }
        if (StringUtils.isNotBlank(responseBody)){
            try {
                BasicMessageEntity<T> messageEntity = mapper.readValue(responseBody, mapper.getTypeFactory().constructParametricType(BasicMessageEntity.class, parametricClass));
                if (messageEntity.getStatus() != 0){
                    throw new ExternalOperationFailedException(-1, "The status in Response message entity is " +
                            "" + messageEntity.getStatus() + ", message: [" + messageEntity.getMessage() +"]", null);
                }
                messageEntity.result = this;
                return messageEntity;
            } catch (JsonProcessingException e) {
                throw new ExternalOperationFailedException(3130, "Fail to convert the response body: [" + responseBody +
                        "] to message entity, entity: [" + mainClass.getSimpleName() + "]", e);
            }
        }
        return null;

    }

    public <T>T getEntityValue(Class<T> mainClass, Class<?>... parameters) throws ExternalOperationFailedException{
        BasicMessageEntity<T> entity = getEntity(mainClass, parameters);
        return Objects.nonNull(entity)? entity.getData() : null;
    }
    public static class BasicMessageEntity<T>{
        /**
         * Result status
         */
        private Integer status = 0;

        /**
         * Refer request method(uri)
         */
        private String method;

        /**
         * Error message
         */
        private String message;

        /**
         * Actual entity
         */
        private T data;

        private ExchangisEntityRespResult result;

        public ExchangisEntityRespResult getResult() {
            return result;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

}
