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
import com.webank.wedatasphere.exchangis.datasource.service.impl.MySQLMetaDbServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author v_fwu
 * 2018/9/19
 */
@RestController
@RequestMapping("/api/v1/datasource/meta")
public class MySQLInfoController extends AbstractDataAuthController {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLInfoController.class);

    @Resource
    private MySQLMetaDbServiceImpl mysqlMetaDbService;

    @Resource
    private DataSourceServiceImpl dataSourceService;


    /**
     * Get all sql databases
     * @param dsId data source id
     * @param request request
     * @return
     */
    // 列出所有该mysql连接下的数据库
    @GetMapping(value = "/mysql/{ds_id}/dbs")
    public Response<Object> dbInfo(@PathVariable("ds_id") String dsId, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<String> dbList = mysqlMetaDbService.getDatabases(dsId);
        return new Response<>().successResponse(dbList);
    }

    /**
     * Get sql tables
     * @param dsId data source id
     * @param db database name
     * @param request request
     * @return
     */
    // 列出某个数据库下的所有表
    @GetMapping(value = "/mysql/{ds_id}/{db}/tables")
    public Response<Object> tableInfo( @PathVariable("ds_id") String dsId,
                                        @PathVariable("db")String db, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<String>  tables = mysqlMetaDbService.getTables(dsId, db);
        return new Response<>().successResponse(tables);
    }

    /**
     * Get sql column fields
     * @param dsId
     * @param db
     * @param table
     * @param request
     * @return
     */
    // 列出某张表下的所有字段
    @GetMapping(value = "/mysql/{ds_id}/{db}/{table}/fields")
    public Response<Object> fieldInfo(@PathVariable("ds_id") String dsId,
                                          @PathVariable("db")String db,
                                          @PathVariable("table") String table, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<MetaColumnInfo> information = mysqlMetaDbService.getColumns(dsId, db, table);
        return new Response<>().successResponse(information);
    }

}
