/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.common.controller;

import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by devendeng on 2018/8/22.
 */
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
            this.setMessage(PatternInjectUtils.inject(message, args));
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
