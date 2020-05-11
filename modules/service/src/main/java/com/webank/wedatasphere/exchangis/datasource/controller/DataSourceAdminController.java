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

import com.webank.wedatasphere.exchangis.common.auth.annotations.ContainerAPI;
import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author davidhua
 * 2019/4/17
 */
@RestController
@RequestMapping(value = "/api/v1/admin/datasource")
public class DataSourceAdminController extends ExceptionResolverContext {
    @Resource
    private DataSourceServiceImpl dataSourceService;
    @ContainerAPI
    @RequestMapping("/view/{id}")
    public Response<DataSource> show(@PathVariable("id")Long id){
        DataSource dataSource = dataSourceService.getDetail(id);
        return new Response<DataSource>().successResponse(dataSource);
    }
}
