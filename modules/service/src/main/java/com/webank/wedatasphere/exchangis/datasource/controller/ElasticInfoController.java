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

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractDataAuthController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import com.webank.wedatasphere.exchangis.datasource.service.impl.ElasticMetaDbServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author davidhua
 * 2019/8/20
 */
@RestController
@RequestMapping("/api/v1/datasource/meta")
public class ElasticInfoController extends AbstractDataAuthController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticInfoController.class);

    @Resource
    private DataSourceServiceImpl dataSourceService;


    @Resource
    private ElasticMetaDbServiceImpl elasticMetaDbService;
    /**
     * Get all indices information
     * @return
     */
    @RequestMapping(value = "/elastic/{ds_id}/indices", method = RequestMethod.GET)
    public Response<Object> indices(@PathVariable("ds_id") String dsId, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        return new Response<>().successResponse(elasticMetaDbService.getDatabases(dsId));
    }


    /**
     * Get index type
     * @param dsId data source id
     * @param index index name
     * @param request request
     * @return
     */
    @RequestMapping(value = "/elastic/{ds_id}/{index}/types", method = RequestMethod.GET)
    public Response<Object> types(@PathVariable("ds_id") String dsId,
                                  @PathVariable("index")String index, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        return new Response<>().successResponse(elasticMetaDbService.getTables(dsId, index));
    }

    /**
     * Get columns
     * @param dsId data source id
     * @param index index name
     * @param type type name
     * @param request request
     * @return
     */
    @RequestMapping(value = "/elastic/{ds_id}/{index}/{type}/columns", method = RequestMethod.GET)
    public Response<Object> columns(@PathVariable("ds_id") String dsId,
                                    @PathVariable("index")String index,
                                    @PathVariable("type")String type, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        return new Response<>().successResponse(elasticMetaDbService.getColumns(dsId, index,  type));
    }

}
