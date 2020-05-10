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

import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author davidhua
 * 2019/4/4
 */
@Component
public class SecurityUtil {

    public static final Logger LOG = LoggerFactory.getLogger(SecurityUtil.class);

    @Resource
    private AuthTokenHelper authTokenHelper;

    /**
     * Store processes of getting authority scope
     */
    private Map<Class<?>, Getter<DataAuthScope, Object>> dataAuthScopeGetters = new ConcurrentHashMap<>();

    /**
     * Store processes of getting authority information
     */
    private Map<Class<?>, Getter<String, Object>> dataAuthGetters = new ConcurrentHashMap<>();

    /**
     * Store processes of getting user's authority information
     */
    private Map<Class<?>, Getter<String, String>> userDataAuthGetters = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T>void registerExternalDataAuthScopeGetter(Class<T> dataClass, Getter<DataAuthScope, T> getter){
        dataAuthScopeGetters.put(dataClass, (Getter<DataAuthScope, Object>) getter);
    }

    @SuppressWarnings("unchecked")
    public <T>void registerExternalDataAuthGetter(Class<T> dataClass, Getter<String, T> getter){
        dataAuthGetters.put(dataClass, (Getter<String, Object>) getter);
    }

    public void registerUserExternalDataAuthGetter(Class<?> dataClass, Getter<String, String> getter){
        userDataAuthGetters.put(dataClass, getter);
    }

    @SuppressWarnings("unchecked")
    public <T>Getter<DataAuthScope, T> externalDataAuthScopeGetter(Class<T> dataClass){
        Getter<DataAuthScope, Object> getter = dataAuthScopeGetters.get(dataClass);
        return null != getter? (Getter<DataAuthScope, T>) getter : data -> Collections.singletonList(DataAuthScope.ALL);
    }

    @SuppressWarnings("unchecked")
    public <T>Getter<String, T> externalDataAuthGetter(Class<T> dataClass){
        Getter<String, Object> getter = dataAuthGetters.get(dataClass);
        return null != getter? (Getter<String, T>) getter : data -> null;
    }

    public Getter<String, String> userExternalDataAuthGetter(Class<?> dataClass){
        Getter<String, String> getter = userDataAuthGetters.get(dataClass);
        return null != getter? getter : userName -> Collections.emptyList();
    }

    public <R> void bindAuthScope(R data, List<DataAuthScope> authScopes){
        if(null != authScopes && authScopes.size() > 0) {
            Class<?> clazz = data.getClass();
            try {
                Method method = clazz.getMethod("setAuthScopes", List.class);
                List<String> strAuthScopes = new ArrayList<>(authScopes.size());
                authScopes.forEach(item -> strAuthScopes.add(item.name()));
                method.invoke(data, strAuthScopes);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                //Ignore
            }
        }
    }
    /**
     * Set createUser,modifyUser
     * @param t
     * @param request
     */
    public <R>void bindUserInfoAndDataAuth(R t, HttpServletRequest request, List<String> dataAuth) {
        try {
            Class clazz = t.getClass();
            Method[] methods = clazz.getMethods();
            for(Method m : methods){
                if("setCreateUser".equals(m.getName())){
                    setLoginId(request, clazz,"setCreateUser",t);
                }
                if("setModifyUser".equals(m.getName())){
                    setLoginId(request, clazz,"setModifyUser",t);
                }
                if("setUserDataAuth".equals(m.getName())){
                    if(null == dataAuth){
                        setDataAuth(clazz,"setUserDataAuth", t, null);
                    }else {
                        setDataAuth(clazz, "setUserDataAuth", t, new HashSet<>(dataAuth));
                    }
                }
            }
        } catch(Exception ne){
            //Ignore
        }
    }

    /**
     * Set createUser,modifyUser
     * @param t
     * @param request
     */
    public <R>void bindUserInfo(R t, HttpServletRequest request){
        bindUserInfoAndDataAuth(t,request,null);
    }
    /**
     * Fill username in model
     * @param request request
     * @param clazz class
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setLoginId(HttpServletRequest request, Class clazz,String methodName,Object t) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = clazz.getDeclaredMethod(methodName, String.class);
        method.invoke(t, getUserName(request));
    }

    /**
     * Fill data auth
     * @param clazz class
     * @param methodName method name
     * @param t data
     * @param dataAuthSet authority set
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDataAuth(Class clazz, String methodName, Object t, Set<String> dataAuthSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = clazz.getDeclaredMethod(methodName, Set.class);
        method.invoke(t, dataAuthSet);
    }

    /**
     * Get username
     * @param request request
     * @return
     */
    public String getUserName(HttpServletRequest request){
        String token = AppUtil.getCookieValue(request, AuthConstraints.DEFAULT_SSO_COOKIE);
        return StringUtils.isNotBlank(token)?
                authTokenHelper.getAuthHeader(token).get(AuthConstraints.X_AUTH_ID): null;
    }

    public <R>String getUserName(R data){
        String userName = null;
        if(null != data) {
            Class<?> clazz = data.getClass();
            try {
                Method method = clazz.getMethod("getCreateUser");
                userName = String.valueOf(method.invoke(data));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                //Ignore
            }
        }
        return userName;
    }
    /**
     * Getter
     * @param <R>
     */
    public interface Getter<R, T>{

        List<R> get(T data);
    }

}
