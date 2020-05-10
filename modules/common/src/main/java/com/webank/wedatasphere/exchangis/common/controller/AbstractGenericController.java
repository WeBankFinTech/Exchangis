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
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * @author devendeng on 2018/8/21.
 */
public abstract class AbstractGenericController<T, E extends PageQuery> extends AbstractDataAuthController{
    public static final Logger LOG = LoggerFactory.getLogger(AbstractGenericController.class);

    public abstract IBaseService<T> getBaseService();

    private Class<T> actualType;

    @SuppressWarnings("unchecked")
    protected Class<T> getActualType(){
        if(actualType == null){
            synchronized (this){
                ParameterizedType ptClass = (ParameterizedType) this.getClass().getGenericSuperclass();
                actualType = (Class<T>) ptClass.getActualTypeArguments()[0];
            }
        }
        return actualType;
    }
    /**
     * View details
     */
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public Response<T> show(@PathVariable java.lang.Long id, HttpServletRequest request) throws Exception {
        if(!hasDataAuth(getActualType(), DataAuthScope.READ, request, getBaseService().get(id))){
            return new Response<T>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
        }
        T t = getBaseService().get(id);
        return new Response<T>().successResponse(t);
    }

    /**
     * Delete single one
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Response<T> delete(@PathVariable java.lang.Long id, HttpServletRequest request) {
        if(!hasDataAuth(getActualType(), DataAuthScope.DELETE, request, getBaseService().get(id))){
            return new Response<T>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
        }
        boolean result = getBaseService().delete(String.valueOf(id));
        return result ? new Response<T>().successResponse(null) : new Response<T>().errorResponse(1, null, "删除失败(Delete failed)");
    }

    /**
     * Delete batch
     */
    @RequestMapping(value = "/delBatch", method = RequestMethod.POST)
    public Response<T> delBatch(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String ids = map.get("ids");
        boolean result = false;
        if(StringUtils.isNotBlank(ids)){
            String[] idArray = ids.split(",");
            //How can be more faster?
            T[] dataArray = (T[])new Object[idArray.length];
            for(int i = 0 ; i < idArray.length; i ++){
                dataArray[i] = getBaseService().get(idArray[i]);
            }
            if(!hasDataAuth(getActualType(), DataAuthScope.DELETE, request, dataArray)){
                return new Response<T>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
            }
            result = getBaseService().delete(ids);
        }
        return result ? new Response<T>().successResponse(null) : new Response<T>().errorResponse(1, null, "删除失败(Delete failed)");
    }

    @RequestMapping(value = "/pageList", method = {RequestMethod.POST,RequestMethod.GET})
    public Response<Object> pageList(E pageQuery, HttpServletRequest request) {
        PageList<T> list = null;
        int pageSize  = pageQuery.getPageSize();
        if (pageSize == 0){
            pageQuery.setPageSize(10);
        }
        String username = security.getUserName(request);
        if(StringUtils.isNotBlank(username)) {
            security.bindUserInfoAndDataAuth(pageQuery, request,
                    security.userExternalDataAuthGetter(getActualType()).get(username));
        }
        list = getBaseService().findPage(pageQuery);
        list.getData().forEach(element -> {
            //Bind authority scopes
            security.bindAuthScope(element, security.externalDataAuthScopeGetter(getActualType()).get(element));
        });
        return new Response<>().successResponse(list);
    }

    @RequestMapping(value = "/selectAll", method = {RequestMethod.POST,RequestMethod.GET})
    public Response<List<T>> selectAll(E pageQuery, HttpServletRequest request) {
        String username = security.getUserName(request);
        if(StringUtils.isNotBlank(username)) {
            security.bindUserInfoAndDataAuth(pageQuery, request,
                    security.userExternalDataAuthGetter(getActualType()).get(username));
        }
        List<T> data = getBaseService().selectAllList(pageQuery);
        data.forEach(element -> {
            //Bind authority scopes
            security.bindAuthScope(data, security.externalDataAuthScopeGetter(getActualType()).get(element));
        });
        return new Response<List<T>>().successResponse(data);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<T> add(@Valid @RequestBody T t,HttpServletRequest request) {
        security.bindUserInfo(t, request);
        boolean result = getBaseService().add(t);
        return result ? new Response<T>().successResponse(null) : new Response<T>().errorResponse(1, null, "添加失败(Add failed)");
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public Response<T> update(@Valid @RequestBody T t,HttpServletRequest request) {
        //TODO Cannot get id
        if(!hasDataAuth(getActualType(), DataAuthScope.WRITE, request, t)){
            return new Response<T>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
        }
        security.bindUserInfo(t, request);
        boolean result = getBaseService().update(t);
        return result ? new Response<T>().successResponse(null) : new Response<T>().errorResponse(1, null, "更新失败(Update failed)");
    }

}
