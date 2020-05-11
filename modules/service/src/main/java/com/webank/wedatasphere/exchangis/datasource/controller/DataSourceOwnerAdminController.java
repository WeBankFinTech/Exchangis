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

import com.webank.wedatasphere.exchangis.auth.annotations.RequireRoles;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceOwner;
import com.webank.wedatasphere.exchangis.datasource.query.DataSourceOwnerQuery;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceOwnerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Data source owner management
 */
@RestController
@RequestMapping("/api/v1/admin/dsOwner")
public class DataSourceOwnerAdminController extends AbstractGenericController<DataSourceOwner,DataSourceOwnerQuery>{
    @Resource
    private DataSourceOwnerService dataSourceOwnerService;

    @Override
    public IBaseService<DataSourceOwner> getBaseService() {
        return dataSourceOwnerService;
    }

    @Override
    @RequireRoles({UserRole.MANGER})
    public Response<DataSourceOwner> add(@Valid @RequestBody DataSourceOwner dataSourceOwner, HttpServletRequest request) {
        DataSourceOwner dsOwner = dataSourceOwnerService.getByName(dataSourceOwner.getOwnerName());
        if(null != dsOwner){
            return new Response<DataSourceOwner>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    super.informationSwitch("exchange.data_source.owner.same"));
        }
        return super.add(dataSourceOwner, request);
    }


    @Override
    @RequireRoles({UserRole.MANGER})
    public Response<DataSourceOwner> update(@Valid @RequestBody DataSourceOwner dataSourceOwner, HttpServletRequest request) {
        if(null == dataSourceOwner.getId()){
            return new Response<DataSourceOwner>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                    super.informationSwitch("exchange.data_source.id.not.empty"));
        }
        DataSourceOwner oldOne = dataSourceOwnerService.get(dataSourceOwner.getId());
        if(!oldOne.getOwnerName().equals(dataSourceOwner.getOwnerName())){
            DataSourceOwner dsOwner = dataSourceOwnerService.getByName(dataSourceOwner.getOwnerName());
            if(null != dsOwner){
                return new Response<DataSourceOwner>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                        super.informationSwitch("exchange.data_source.owner.same"));
            }
        }
        return super.update(dataSourceOwner, request);
    }

    @Override
    @RequireRoles({UserRole.MANGER})
    public Response<DataSourceOwner> delete(@PathVariable Long id, HttpServletRequest request) {
        return super.delete(id, request);
    }
}
