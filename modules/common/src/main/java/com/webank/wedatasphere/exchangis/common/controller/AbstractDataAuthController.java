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

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Data authority controller
 * @author davidhua
 * 2020/4/3
 */
public class AbstractDataAuthController extends ExceptionResolverContext {

    private static final String USER_DATA_AUTH_ATTRIBUTE_KEY = "_USER_DATA_AUTH_";

    @Resource
    protected SecurityUtil security;

    /**
     * If has the data authority
     * @param dataClass data class
     * @param dataEntities data entities
     * @param authScope authority scope
     * @param request request
     * @param <T> entity type
     * @return boolean
     */
    @SafeVarargs
    protected final <T>boolean hasDataAuth(Class<T> dataClass, DataAuthScope authScope, HttpServletRequest request,
                                           T... dataEntities){
        return hasDataAuth(dataClass, authScope, request, null, dataEntities);
    }

    @SafeVarargs
    protected final <T>boolean hasDataAuth(Class<T> dataClass, DataAuthScope authScope, String operator, T... dataEntities){
        return hasDataAuth(dataClass, authScope, null, operator, dataEntities);
    }

    @SafeVarargs
    protected final <T>boolean hasDataAuth(Class<T> dataClass, DataAuthScope authScope, HttpServletRequest request,
                                           String operator, T... dataEntities){
        try {
            String userName = null != request ? security.getUserName(request) : operator;
            List<String> userDataAuthSet = new ArrayList<>();
            boolean userDataInit = false;
            if (StringUtils.isNotBlank(userName)) {
                for (int i = 0; i < dataEntities.length; i++) {
                    T dataEntity = dataEntities[i];
                    //If is data's owner
                    if (userName.equals(security.getUserName(dataEntity))) {
                        continue;
                    }
                    if (!userDataInit) {
                        Object authStoredInReq = null;
                        if(null != request){
                            authStoredInReq = request.getAttribute(userName + USER_DATA_AUTH_ATTRIBUTE_KEY + dataClass.getSimpleName());
                        }
                        if(null != authStoredInReq){
                            try {
                                userDataAuthSet = (List<String>) authStoredInReq;
                            }catch(Exception e){
                                //Ignore
                            }
                        }else {
                            userDataAuthSet = security.userExternalDataAuthGetter(dataClass).get(userName);
                            if(null != request) {
                                request.setAttribute(userName + USER_DATA_AUTH_ATTRIBUTE_KEY + dataClass.getSimpleName(), userDataAuthSet);
                            }
                        }
                        userDataInit = true;
                    }
                    if (null == userDataAuthSet) {
                        //Means that the user has all authorities
                        return true;
                    } else if (userDataAuthSet.size() > 0) {
                        if(null == dataEntity){
                            continue;
                        }
                        List<String> dataAuthSet = security.externalDataAuthGetter(dataClass).get(dataEntity);
                        if (null == dataAuthSet || dataAuthSet.size() <= 0) {
                            continue;
                        }
                        if (userDataAuthSet.containsAll(dataAuthSet)) {
                            List<DataAuthScope> authScopes = security.externalDataAuthScopeGetter(dataClass).get(dataEntity);
                            boolean hasAuth = false;
                            if (!authScopes.isEmpty()) {
                                for (DataAuthScope scope : authScopes) {
                                    if (scope.equals(DataAuthScope.ALL) || scope.equals(authScope)) {
                                        hasAuth = true;
                                        break;
                                    }
                                }
                            }
                            if (hasAuth) {
                                continue;
                            }
                        }
                    }
                    //Means that the user has no authority
                    return false;
                }
                return true;
            }
            //UnLogin
        }catch(Exception e){
            //Ignore
        }
        return false;
    }
}
