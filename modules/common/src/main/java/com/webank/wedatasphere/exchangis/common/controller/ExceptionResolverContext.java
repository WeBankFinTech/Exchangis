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

import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import org.apache.ibatis.binding.BindingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;

/**
 * @author davidhua
 * 2019/1/18
 */
public class ExceptionResolverContext {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionResolverContext.class);

    @Resource
    private MessageSource messageSource;

    /**
     * 参数检查异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Response<Object> errorParameterHandler(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> c : e.getConstraintViolations()) {
            message.append(c.getMessage());
        }
        Response<Object> response = new Response<>();
        response.setCode(CodeConstant.PARAMETER_ERROR);
        response.setMessage(message.toString());
        return response;
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response<Object> errorParameterHandler(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null , message);
    }

    @ExceptionHandler(value = BindingException.class)
    public Response<Object> errorParameterHandler(BindingException e){
        String message = e.getMessage();
        return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null , message);
    }
    @ExceptionHandler(value = BindException.class)
    public Response<Object> errorParameterHandler(BindException e){
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return new Response<>().errorResponse(CodeConstant.PARAMETER_ERROR, null , message);
    }
    @ExceptionHandler(value = EndPointException.class)
    public Response<Object> endPointErrorHandler(EndPointException e){
        String uiMessage = e.getUiMessage();
        String message = this.informationSwitch(uiMessage);
        if(e.getArgs() != null && e.getArgs().length > 0){
            message = PatternInjectUtils.inject(message,e.getArgs());
        }
        return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, message);
    }
    /**
     * Caught exception
     * @param e exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Response<Object> errorHandler(Exception e) {
        LOG.error("SYSTEM_EXCEPTION, message: {}", e.getMessage(), e);
        return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null,
                "系统处理异常(System Exception)[" + e.getMessage() + "]");
    }

    public String informationSwitch(String str){
        Locale locale = LocaleContextHolder.getLocale();
        try{
            return messageSource.getMessage(str,null,locale);
        }catch(NoSuchMessageException e){
            return "未定义异常(Undefined Exception)[" + str + "]";
        }
    }
}
