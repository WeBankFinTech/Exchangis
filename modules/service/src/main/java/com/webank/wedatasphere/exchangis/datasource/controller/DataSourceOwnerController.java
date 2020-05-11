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

import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceOwner;
import com.webank.wedatasphere.exchangis.datasource.query.DataSourceOwnerQuery;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceOwnerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author davidhua
 * 2019/4/9
 */
@RestController
@RequestMapping("/api/v1/dsOwner")
public class DataSourceOwnerController extends ExceptionResolverContext {
    @Resource
    private DataSourceOwnerService dataSourceOwnerService;
    @RequestMapping(value = "/selectAll", method = {RequestMethod.POST,RequestMethod.GET})
    public Response<Object> selectAll(DataSourceOwnerQuery pageQuery, HttpServletRequest request) {
        List<DataSourceOwner> data = dataSourceOwnerService.selectAllList(pageQuery);
        return new Response<>().successResponse(data);
    }
}
