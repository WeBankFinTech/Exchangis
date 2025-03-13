package com.webank.wedatasphere.exchangis.job.server.validator;

/**
 * Result set of validator
 * @param <T>
 */
public class JobValidateResult<T> {
    private boolean result;

    /**
     * Result message
     */
    private String message;

    /**
     * Payload data
     */
    private T payload;
    public JobValidateResult(boolean result, T payload){
        this.result = result;
        this.payload = payload;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
