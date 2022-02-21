package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
import com.webank.wedatasphere.exchangis.datasource.vo.FieldMappingVO;
import org.apache.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "exchangis/datasources", produces = {"application/json;charset=utf-8"})
public class ExchangisDataSourceRestfulApi {

    private final ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    public ExchangisDataSourceRestfulApi(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    // list all datasource types
    @RequestMapping( value = "/type", method = RequestMethod.GET)
    public Message listDataSourceTypes(HttpServletRequest request) throws Exception {
        return this.exchangisDataSourceService.listDataSources(request);
    }

    // query paged datasource
    @RequestMapping( value = "/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Message create(HttpServletRequest request, @RequestBody DataSourceQueryVO vo) throws Exception {
        return this.exchangisDataSourceService.queryDataSources(request, vo);
    }

    // list all datasources
    @RequestMapping( value = "", method = RequestMethod.GET)
    @Deprecated
    public Message listAllDataSources(
            HttpServletRequest request,
            @RequestParam(value = "typeId", required = false) Long typeId,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) throws Exception {
        return this.exchangisDataSourceService.listAllDataSources(request, typeName, typeId, page, size);
    }

    // get datasource key define
    @RequestMapping( value = "/types/{dataSourceTypeId}/keydefines", method = RequestMethod.GET)
    public Message getDataSourceKeyDefine(
            HttpServletRequest request,
            @PathVariable("dataSourceTypeId") Long dataSourceTypeId
    ) throws Exception {
        return this.exchangisDataSourceService.getDataSourceKeyDefine(request, dataSourceTypeId);
    }


    // get datasource version list
    @RequestMapping( value = "/{id}/versions", method = RequestMethod.GET)
    public Message getDataSourceVersionsById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        return this.exchangisDataSourceService.getDataSourceVersionsById(request, id);
    }

    // create datasource
    @RequestMapping( value = "", method = RequestMethod.POST)
    public Message create(HttpServletRequest request, /*@PathParam("type") String type, */@RequestBody Map<String, Object> json) throws Exception {
        return this.exchangisDataSourceService.create(request,/* type, */json);
    }

    // get datasource details
    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getDataSourceInfoById(HttpServletRequest request, @PathVariable("id") Long id, @QueryParam(value = "versionId") String versionId) throws Exception {
        return this.exchangisDataSourceService.getDataSource(request, id, versionId);
    }

    @RequestMapping( value = "/{id}/connect_params", method = RequestMethod.GET)
    public Message getDataSourceConnectParamsById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        return this.exchangisDataSourceService.getDataSourceConnectParamsById(request, id);
    }

    // update datasource and parameters (insert new record in datasource_version table)
    @RequestMapping( value = "/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id, @RequestBody Map<String, Object> json) throws Exception {
        return this.exchangisDataSourceService.updateDataSource(request, /*type, */id, json);
    }

    // publish datasource
    @RequestMapping( value = "/{id}/{version}/publish", method = RequestMethod.PUT)
    public Message publishDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                                     @PathVariable("version") Long version) throws Exception {
        return this.exchangisDataSourceService.publishDataSource(request, /*type, */id, version);
    }

    // expire datasource
    @RequestMapping( value = "/{id}/expire", method = RequestMethod.PUT)
    public Message expireDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id) throws Exception {
        return this.exchangisDataSourceService.expireDataSource(request, /*type, */id);
    }

    // test datasource connect
    @RequestMapping( value = "/{id}/{version}/connect", method = RequestMethod.PUT)
    public Message testConnect(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                               @PathVariable("version") Long version) throws Exception {
       return this.exchangisDataSourceService.testConnect(request, /*type, */id, version);
    }

    // delete datasource (physical)
    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest request, /*@PathParam("type") String type, */@PathVariable("id") Long id) throws Exception {
        return this.exchangisDataSourceService.deleteDataSource(request, /*type, */id);
    }

    @RequestMapping( value = "/{type}/{id}/dbs", method = RequestMethod.GET)
    public Message queryDataSourceDBs(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id) throws Exception {
        return this.exchangisDataSourceService.queryDataSourceDBs(request, type, id);
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables", method = RequestMethod.GET)
    public Message queryDataSourceDBTables(HttpServletRequest request, @PathVariable("type") String type,
                                           @PathVariable("id") Long id, @PathVariable("dbName") String dbName) throws Exception {
        return this.exchangisDataSourceService.queryDataSourceDBTables(request, type, id, dbName);
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields", method = RequestMethod.GET)
    public Message queryDataSourceDBTableFields(HttpServletRequest request, @PathVariable("type") String type,
                                                @PathVariable("id") Long id, @PathVariable("dbName") String dbName,
                                                @PathVariable("tableName") String tableName) throws Exception {
        return this.exchangisDataSourceService.queryDataSourceDBTableFields(request, type, id, dbName, tableName);
    }

    @RequestMapping( value = "/fieldsmapping", method = RequestMethod.POST)
    public Message queryDataSourceDBTableFieldsMapping(HttpServletRequest request, @RequestBody FieldMappingVO vo) throws Exception {
        return this.exchangisDataSourceService.queryDataSourceDBTableFieldsMapping(request, vo);
    }

    @RequestMapping( value = "/{engine}/{type}/params/ui", method = RequestMethod.GET)
    public Message getParamsUI(
            HttpServletRequest request,
            @PathVariable("engine") String engine,
            @PathVariable("type") String type,
            @RequestParam(value = "dir", required = false) String dir
    ) {
        List<ElementUI<?>> uis = this.exchangisDataSourceService.getDataSourceParamsUI(type, String.format("%s-%s", engine, dir));
        return Message.ok().data("uis", uis);
    }

}
