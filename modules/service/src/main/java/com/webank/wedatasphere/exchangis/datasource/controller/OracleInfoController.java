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
import com.webank.wedatasphere.exchangis.datasource.domain.MetaColumnInfo;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import com.webank.wedatasphere.exchangis.datasource.service.impl.OracleMetaDbServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ronaldyang
 * 2020/7/22
 */
@RestController
@RequestMapping("/api/v1/datasource/meta")
public class OracleInfoController extends AbstractDataAuthController {
    private static final Logger LOG = LoggerFactory.getLogger(OracleInfoController.class);

    @Resource
    private OracleMetaDbServiceImpl oracleMetaDbService;

    @Resource
    private DataSourceServiceImpl dataSourceService;


    /**
     * Get all sql databases
     * @param dsId data source id
     * @param request request
     * @return
     */
    @GetMapping(value = "/oracle/{ds_id}/dbs")
    public Response<Object> dbInfo(@PathVariable("ds_id") String dsId, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<String> dbList = oracleMetaDbService.getDatabases(dsId);
        return new Response<>().successResponse(dbList);
    }

    /**
     * Get sql tables
     * @param dsId data source id
     * @param db database name
     * @param request request
     * @return
     */
    @GetMapping(value = "/oracle/{ds_id}/{db}/tables")
    public Response<Object> tableInfo( @PathVariable("ds_id") String dsId,
                                       @PathVariable("db")String db, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<String>  tables = oracleMetaDbService.getTables(dsId, db);
        return new Response<>().successResponse(tables);
    }

    /**
     * Get sql column fields
     *
     * @param dsId
     * @param db
     * @param table
     * @param request
     * @return
     */
    @GetMapping(value = "/oracle/{ds_id}/{db}/{table}/fields")
    public Response<Object> fieldInfo(@PathVariable("ds_id") String dsId,
                                      @PathVariable("db")String db,
                                      @PathVariable("table") String table, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<MetaColumnInfo> information = oracleMetaDbService.getColumns(dsId, db, table);
        return new Response<>().successResponse(information);
    }

}
