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

package com.webank.wedatasphere.exchangis.common.auth.interceptors;

import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.auth.annotations.ContainerAPI;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.webank.wedatasphere.exchangis.common.auth.AuthConstraints.ALLOWEDURIS;
import static com.webank.wedatasphere.exchangis.common.auth.AuthConstraints.X_AUTH_ID;

/**
 * @author davidhua
 * 2018/10/16
 */
@Component
@ConditionalOnClass(HandlerInterceptor.class)
public class AuthorityInterceptor implements HandlerInterceptor {
    private final Logger LOG = LoggerFactory.getLogger(AuthorityInterceptor.class);
    @Resource
    private AuthConfiguration conf;
    @Resource
    private AuthTokenHelper authTokenHelper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(conf.enable() && !request.getRequestURI().equals(conf.authLoginUrl())
                                && !request.getRequestURI().equals(conf.authRedirectUrl())) {
            String token = AppUtil.getCookieValue(request, AuthConstraints.DEFAULT_SSO_COOKIE);
            Map<String, String> headers = authTokenHelper.getAuthHeader(token);
            String loginId = headers.get(X_AUTH_ID);
            String allowUriJson = headers.get(ALLOWEDURIS);
            boolean status = false;
            response.setContentType("application/json;charset=UTF-8");
            if (loginId != null) {
                response.setStatus(200);
                status = checkUrl(request, loginId);
            } else if (null != allowUriJson) {
                List<String> allowUri = Json.fromJson(allowUriJson, String.class);
                assert allowUri != null;
                String reqURI = request.getRequestURI();
                for(String allow : allowUri){
                    if(reqURI.startsWith(allow)){
                        status = true;
                        break;
                    }
                }
            }
            if(status){
                if(handler instanceof HandlerMethod){
                    HandlerMethod method = (HandlerMethod)handler;
                    if(method.getMethod().isAnnotationPresent(ContainerAPI.class)
                            && loginId != null){
                        status = false;
                    }
                }
            }
            if (!status) {
                Response<String> r = new Response<>();
                String m = "The current user doesn't have permission to access the interface";
                r.errorResponse(CodeConstant.AUTH_ERROR, null, m);
                //illegal request
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(Objects.requireNonNull(Json.toJson(r, null)));
            }
            return status;
        }
        return true;
    }

    private boolean checkUrl(HttpServletRequest request, String loginid){
        //TODO uc to validate
        return true;
    }
}
