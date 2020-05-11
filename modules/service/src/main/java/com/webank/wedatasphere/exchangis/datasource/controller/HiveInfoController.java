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
import com.webank.wedatasphere.exchangis.datasource.domain.MetaPartitionInfo;
import com.webank.wedatasphere.exchangis.datasource.service.impl.DataSourceServiceImpl;
import com.webank.wedatasphere.exchangis.datasource.service.impl.HiveMetaDbServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author davidhua
 * 2018/9/19
 */
@RestController
@RequestMapping("/api/v1/datasource/meta")
public class HiveInfoController extends AbstractDataAuthController {
    private static final Logger LOG = LoggerFactory.getLogger(HiveInfoController.class);


    @Resource
    private HiveMetaDbServiceImpl hiveMetaDBService;

    @Resource
    private DataSourceServiceImpl dataSourceService;

    /**
     * Get all hive databases
     * @param dsId data source id
     * @param request request
     * @return
     */
    @RequestMapping(value="/hive/{ds_id}/dbs", method=RequestMethod.GET)
    public Response<Object> hiveDBInfo(@PathVariable("ds_id") String dsId, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<String> dbList = hiveMetaDBService.getDatabases(dsId);
        return new Response<>().successResponse(dbList);
    }

    /**
     * Get hive tables
     * @param dsId data source id
     * @param db database name
     * @param request request
     * @return
     */
    @RequestMapping(value="/hive/{ds_id}/{db}/tables", method=RequestMethod.GET)
    public Response<Object> hiveTableInfo(@PathVariable("ds_id") String dsId,
                                          @PathVariable("db")String db, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<String>  tables = hiveMetaDBService.getTables(dsId, db);
        return new Response<>().successResponse(tables);
    }

    /**
     * Get hive partitions
     * @param dsId data source id
     * @param db database name
     * @param table table
     * @param request request
     * @return
     */
    @RequestMapping(value="/hive/{ds_id}/{db}/{table}/partitions", method=RequestMethod.GET)
    public Response<Object> hivePartitionInfo(@PathVariable("ds_id") String dsId,
                                              @PathVariable("db")String db,
                                              @PathVariable("table") String table, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        MetaPartitionInfo info = hiveMetaDBService.getPartitionKeys(dsId, db, table);
        return new Response<>().successResponse(info);
    }


    /**
     * Get hive fields information
     * @param dsId data source id
     * @param db database name
     * @param table table name
     * @param request request
     * @return
     */
    @RequestMapping(value="/hive/{ds_id}/{db}/{table}/fields", method=RequestMethod.GET)
    public Response<Object> hiveFieldInfo(@PathVariable("ds_id") String dsId,
                                          @PathVariable("db")String db,
                                          @PathVariable("table") String table, HttpServletRequest request){
        if(!hasDataAuth(DataSource.class, DataAuthScope.EXECUTE, request, dataSourceService.get(dsId))){
            return new Response<>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.data_source.not.access.rights"));
        }
        List<MetaColumnInfo> information = hiveMetaDBService.getColumns(dsId, db, table);
        return new Response<>().successResponse(information);
    }
}
