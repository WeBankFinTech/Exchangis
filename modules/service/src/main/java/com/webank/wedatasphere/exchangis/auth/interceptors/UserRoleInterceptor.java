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

package com.webank.wedatasphere.exchangis.auth.interceptors;

import com.webank.wedatasphere.exchangis.auth.annotations.RequireRoles;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.controller.SecurityUtil;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author davidhua
 * 2019/7/12
 */
@Component
public class UserRoleInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(UserRoleInterceptor.class);

    @Resource
    private AuthConfiguration conf;

    @Resource
    protected SecurityUtil security;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(conf.enable()){
            boolean check = true;
            if(handler instanceof HandlerMethod){
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                UserInfo userInfo = null;
                if(handlerMethod.getMethod().isAnnotationPresent(RequireRoles.class)){
                    userInfo = userInfoService.selectByUsername(security.getUserName(request));
                    check = checkIfMatchRole(handlerMethod.
                            getMethod().getAnnotation(RequireRoles.class), userInfo);
                }
                if(check && handlerMethod.getBeanType().isAnnotationPresent(RequireRoles.class)){
                    if(null == userInfo){
                        userInfo = userInfoService.selectByUsername(security.getUserName(request));
                    }
                    check = checkIfMatchRole(handlerMethod.getBeanType()
                            .getAnnotation(RequireRoles.class), userInfo);
                }
            }
            if(!check){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
            return check;
        }
        return true;
    }

    private boolean checkIfMatchRole(RequireRoles roles, UserInfo userInfo){
        UserRole[] userRoles = roles.value();
        Integer typeValue = userInfo.getUserType();
        if(null != typeValue){
            for(UserRole userRole : userRoles){
                if(typeValue <  userRole.getValue()){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
