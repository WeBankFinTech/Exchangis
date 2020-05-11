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

package com.webank.wedatasphere.exchangis.datasource.controller;

import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.checks.AbstractDataSourceConnCheck;
import com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck;
import com.webank.wedatasphere.exchangis.datasource.dao.DataSourceDao;
import com.webank.wedatasphere.exchangis.datasource.domain.AuthType;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.domain.ModelTemplateStructure;
import com.webank.wedatasphere.exchangis.datasource.exception.ConnParamsInValidException;
import com.webank.wedatasphere.exchangis.datasource.query.DataSourceQuery;
import com.webank.wedatasphere.exchangis.datasource.query.DataSourceModelQuery;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Model configuration management
 */
@RestController
@RequestMapping("/api/v1/model")
public class DataSourceModelController extends AbstractGenericController<DataSourceModel, DataSourceModelQuery> {
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceModelController.class);

    @Resource
    private DataSourceModelService dataSourceModelService;

    @Resource
    private DataSourceDao dataSourceDao;

    @Resource
    private UserInfoService userInfoService;

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(DataSourceModel.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //No limit
                return null;
            }
            return Arrays.asList("", null, userName);
        });
        security.registerExternalDataAuthGetter(DataSourceModel.class, dataSourceModel -> {
            if(null == dataSourceModel){
                return Collections.emptyList();
            }
            return Collections.singletonList(dataSourceModel.getCreateOwner());
        });
        security.registerExternalDataAuthScopeGetter(DataSourceModel.class, dataSourceModel -> Arrays.asList(DataAuthScope.READ, DataAuthScope.EXECUTE));
    }
    @Override
    public IBaseService<DataSourceModel> getBaseService() {
        return dataSourceModelService;
    }


    @Override
    public Response<DataSourceModel> delete(@PathVariable Long id, HttpServletRequest request) {
        if(!hasDataAuth(DataSourceModel.class, DataAuthScope.DELETE, request, getBaseService().get(id))){
            return new Response<DataSourceModel>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
        }
        DataSourceQuery query = new DataSourceQuery();
        query.setModelId(id.intValue());
        List<DataSource> dataSources = dataSourceDao.selectAllList(query);
        if(dataSources != null && dataSources.size() > 0){
            return new Response<DataSourceModel>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source_model.in.used"));
        }
        boolean result = getBaseService().delete(String.valueOf(id));
        return result ? new Response<DataSourceModel>().successResponse(null) :
                new Response<DataSourceModel>().errorResponse(1, null, "删除失败(Delete failed)");
    }

    @Override
    public Response<DataSourceModel> add(@Valid  @RequestBody DataSourceModel model, HttpServletRequest request){
        DataSourceConnCheck connCheck = AbstractDataSourceConnCheck.getConnCheck(model.getSourceType(),null);
        if (null == connCheck) {
            return null;
        }
        security.bindUserInfo(model, request);
        if(!StringUtils.isNotBlank(model.getCreateUser()) && isDuplicate(model.getModelName(), request)){
            return new Response<DataSourceModel>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source_model.with.same.name"));
        }
        //Set create owner
        UserInfo userInfo = userInfoService.selectByUsername(model.getCreateUser());
        if(null != userInfo && userInfo.getUserType() >= UserRole.MANGER.getValue()){
            //Global visible
            model.setCreateOwner("");
        }else{
            model.setCreateOwner(model.getCreateUser());
        }
        try {
            connCheck.validate(model);
        } catch (Exception e) {
            LOG.error("Validate connection parameters failed, message: [" + e.getMessage() + "]");
            throw new ConnParamsInValidException(super.informationSwitch("exchange.data_source_model.connect.format.error"));
        }
        model.setModelName(StringEscapeUtils.escapeHtml3(model.getModelName()));
        boolean ok = getBaseService().add(model);
        return ok ? new Response<DataSourceModel>().successResponse(null) :
                new Response<DataSourceModel>().errorResponse(CodeConstant.MODELASSEMBLIY_ADD_ERROR, null, super.informationSwitch("udes.modelassembly.add.template.failed"));
    }

    @Override
    public Response<DataSourceModel> update(@Valid @RequestBody DataSourceModel model, HttpServletRequest request){
        if(model.getId()  <= 0){
            return new Response<DataSourceModel>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source_model.tempid.not.empty"));
        }
        DataSourceModel osMA = dataSourceModelService.get(model.getId());
        if(!hasDataAuth(DataSourceModel.class, DataAuthScope.WRITE, request, osMA)){
            return new Response<DataSourceModel>().errorResponse(CodeConstant.AUTH_ERROR, null, "没有操作权限(Unauthorized)");
        }
        DataSourceConnCheck connCheck = AbstractDataSourceConnCheck.getConnCheck(model.getSourceType(), null);
        if( null == connCheck){
            return null;
        }
        security.bindUserInfo(model, request);
        try {
            connCheck.validate(model);
        } catch (Exception e) {
            LOG.error("Validate connection parameters failed, message: " + e.getMessage());
            throw new ConnParamsInValidException(super.informationSwitch("exchange.data_source_model.connect.format.error"));
        }
        if(!osMA.getModelName().equals(model.getModelName())
                && StringUtils.isNotBlank(model.getCreateUser())
                && isDuplicate(model.getModelName(), model.getCreateUser())){
            return new Response<DataSourceModel>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.data_source_model.with.same.name"));
        }
        model.setModelName(StringEscapeUtils.escapeHtml3(model.getModelName()));
        boolean ok = getBaseService().update(model);
        return ok ? new Response<DataSourceModel>().successResponse(null) :
                new Response<DataSourceModel>().errorResponse(CodeConstant.MODELASSEMBLIY_ADD_ERROR, null, super.informationSwitch("udes.modelassembly.add.template.failed"));
    }

    @RequestMapping(value = "/{modelType:\\w+}/list", method = RequestMethod.GET)
    public Response<Object> listByType(@PathVariable("modelType")String modelType,
                                       HttpServletRequest request){
        String userName = security.getUserName(request);
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setSourceType(modelType);
        if(StringUtils.isNotBlank(userName)) {
            security.bindUserInfoAndDataAuth(query, request,
                    security.userExternalDataAuthGetter(DataSourceModel.class).get(userName));
        }
        List<DataSourceModel> list = dataSourceModelService.selectAllList(query);
        List<ModelTemplateStructure> structureList = list.stream().map(modelAssembly -> {
            ModelTemplateStructure structure = new ModelTemplateStructure();
            structure.setModelId(modelAssembly.getId());
            structure.setModelName(modelAssembly.getModelName());
            Map<String, Object> params = modelAssembly.resolveParams();
            structure.setAuthType(
                    String.valueOf(params.getOrDefault(Constants.PARAM_AUTH_TYPE, "")));
            return structure;
        }).collect(Collectors.toList());
        return new Response<>().successResponse(structureList);
    }

    @Override
    public Response<Object> pageList(DataSourceModelQuery pageQuery, HttpServletRequest request) {
        PageList<DataSourceModel> list = null;
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
            if(StringUtils.isNotBlank(element.getCreateOwner()) && !element.getCreateOwner().equals(username)){
                //Remove sensitive data
                Map<String, Object> params = element.resolveParams();
                Map<String, Object> newParams = new HashMap<>();
                newParams.put(Constants.PARAM_AUTH_TYPE, params.getOrDefault(Constants.PARAM_AUTH_TYPE, AuthType.NONE));
                element.setParameter(Json.toJson(newParams, null));
            }
        });
        return new Response<>().successResponse(list);
    }

    @Override
    public Response<List<DataSourceModel>> selectAll(DataSourceModelQuery pageQuery, HttpServletRequest request) {
        String username = security.getUserName(request);
        if(StringUtils.isNotBlank(username)) {
            security.bindUserInfoAndDataAuth(pageQuery, request,
                    security.userExternalDataAuthGetter(getActualType()).get(username));
        }
        List<DataSourceModel> data = getBaseService().selectAllList(pageQuery);
        data.forEach(element -> {
            //Bind authority scopes
            security.bindAuthScope(data, security.externalDataAuthScopeGetter(getActualType()).get(element));
            if(StringUtils.isNotBlank(element.getCreateOwner()) && !element.getCreateOwner().equals(username)){
                //Remove sensitive data
                Map<String, Object> params = element.resolveParams();
                Map<String, Object> newParams = new HashMap<>();
                newParams.put(Constants.PARAM_AUTH_TYPE, params.getOrDefault(Constants.PARAM_AUTH_TYPE, AuthType.NONE));
                element.setParameter(Json.toJson(newParams, null));
            }
        });
        return new Response<List<DataSourceModel>>().successResponse(data);
    }

    private boolean isDuplicate(String tsName, HttpServletRequest request){
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setModelExactName(tsName);
        String userName = security.getUserName(request);
        if(StringUtils.isNotBlank(userName)){
            security.bindUserInfoAndDataAuth(query, request,
                    security.userExternalDataAuthGetter(DataSourceModel.class).get(userName));
        }
        return !dataSourceModelService.selectAllList(query).isEmpty();
    }

    private boolean isDuplicate(String tsName, String createUser){
        DataSourceModelQuery query = new DataSourceModelQuery();
        query.setModelExactName(tsName);
        query.setCreateUser(createUser);
        return !dataSourceModelService.selectAllList(query).isEmpty();
    }
}
