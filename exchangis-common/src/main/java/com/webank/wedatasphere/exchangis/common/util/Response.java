package com.webank.wedatasphere.exchangis.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> implements Serializable {
    private static final long serialVersionUID = -5615777667797486302L;
    /**
     * Operation code
     */
    private int code = 0;
    /**
     * Return data
     */
    private T data;
    /**
     * Payload message
     */
    private String message;

    /**
     * Success response
     * @param t data
     * @return
     */
    public Response<T> successResponse(T t) {
        this.setCode(0);
        this.setMessage("success");
        this.setData(t);
        return this;
    }

    /**
     * Error response
     * @param code code
     * @param t data
     * @param message message
     * @return
     */
    public Response<T> errorResponse(int code, T t, String message, Object... args) {
        this.setCode(code);
        if(args != null && args.length > 0){
            this.setMessage(PatternInjectUtils.inject(message, args, false, false, true));
        }else{
            this.setMessage(message);
        }
        this.setData(t);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
