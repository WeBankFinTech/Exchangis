package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
import com.webank.wedatasphere.exchangis.datasource.vo.FieldMappingVO;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "exchangis/datasources", produces = {"application/json;charset=utf-8"})
public class ExchangisDataSourceRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceRestfulApi.class);


    private final ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    public ExchangisDataSourceRestfulApi(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    // list all datasource types
    @RequestMapping( value = "/type", method = RequestMethod.GET)
    public Message listDataSourceTypes(HttpServletRequest request) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.listDataSources(request);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while list datasource type";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源类型列表失败");
        }
        return message;

    }

    // query paged datasource
    @RequestMapping( value = "/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Message create(HttpServletRequest request, @RequestBody DataSourceQueryVO vo) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.queryDataSources(request, vo);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while query datasource";
            LOG.error(errorMessage, e);
            message = Message.error("查询数据源失败");
        }
        return message;

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
        Message message = null;
        try{
            message = exchangisDataSourceService.listAllDataSources(request, typeName, typeId, page, size);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource list";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源列表失败");
        }
        return message;

    }

    // get datasource key define
    @RequestMapping( value = "/types/{dataSourceTypeId}/keydefines", method = RequestMethod.GET)
    public Message getDataSourceKeyDefine(
            HttpServletRequest request,
            @PathVariable("dataSourceTypeId") Long dataSourceTypeId
    ) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSourceKeyDefine(request, dataSourceTypeId);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource key define";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源主键定义失败");
        }
        return message;

    }


    // get datasource version list
    @RequestMapping( value = "/{id}/versions", method = RequestMethod.GET)
    public Message getDataSourceVersionsById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSourceVersionsById(request, id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource version";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源版本失败");
        }
        return message;

    }

    // create datasource
    @RequestMapping( value = "", method = RequestMethod.POST)
    public Message create(/*@PathParam("type") String type, */@Valid @RequestBody DataSourceCreateVO dataSourceCreateVO, BindingResult bindingResult, HttpServletRequest request ) throws Exception {
        Message message = new Message();

        LOG.info("dataSourceName:   " + dataSourceCreateVO.getDataSourceName() + "dataSourceDesc:   " + dataSourceCreateVO.getDataSourceDesc() + "label:   " + dataSourceCreateVO.getLabels());
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(int i=0;i<fieldErrors.size();i++){
                message = Message.error(fieldErrors.get(i).getDefaultMessage());
                LOG.error("error field is : {} ,message is : {}", fieldErrors.get(i).getField(), fieldErrors.get(i).getDefaultMessage());
            }
        }
        else {
            try {
                message = exchangisDataSourceService.create(request, dataSourceCreateVO);
            } catch (ExchangisDataSourceException e) {
                String errorMessage = "Error occur while create datasource";
                LOG.error(errorMessage, e);
                message = Message.error("创建数据源失败，存在同名数据源");
            }
        }
        return message;
    }

    // get datasource details
    @RequestMapping( value = "/{id}", method = RequestMethod.GET)
    public Message getDataSourceInfoById(HttpServletRequest request, @PathVariable("id") Long id, @QueryParam(value = "versionId") String versionId) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSource(request, id, versionId);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting datasource information";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据源信息失败");
        }
        return message;

    }

    @RequestMapping( value = "/{id}/connect_params", method = RequestMethod.GET)
    public Message getDataSourceConnectParamsById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.getDataSourceConnectParamsById(request, id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting connect params";
            LOG.error(errorMessage, e);
            message = Message.error("Exit same name dataSource(获取数据源连接参数失败)");
        }
        return message;

    }

    // update datasource and parameters (insert new record in datasource_version table)
    @RequestMapping( value = "/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id, @Valid @RequestBody DataSourceCreateVO dataSourceCreateVO, BindingResult bindingResult) throws Exception {
        Message message = new Message();

        LOG.info("dataSourceName:   " + dataSourceCreateVO.getDataSourceName() + "dataSourceDesc:   " + dataSourceCreateVO.getDataSourceDesc() + "label:   " + dataSourceCreateVO.getLabels());
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(int i=0;i<fieldErrors.size();i++){
                message = Message.error(fieldErrors.get(i).getDefaultMessage());
                LOG.error("error field is : {} ,message is : {}", fieldErrors.get(i).getField(), fieldErrors.get(i).getDefaultMessage());
            }
        }
        else {
            try{
                message = exchangisDataSourceService.updateDataSource(request, /*type, */id, dataSourceCreateVO);
            } catch (ExchangisDataSourceException e) {
                String errorMessage = "Error occur while update datasource";
                LOG.error(errorMessage, e);
                message = Message.error("Exit same name dataSource(更新数据源失败，存在同名数据源)");
            }
        }
        return message;

    }

    // publish datasource
    @RequestMapping( value = "/{id}/{version}/publish", method = RequestMethod.PUT)
    public Message publishDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                                     @PathVariable("version") Long version) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.publishDataSource(request, /*type, */id, version);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while publish datasource";
            LOG.error(errorMessage, e);
            message = Message.error("发布数据源失败");
        }
        return message;

    }

    // expire datasource
    @RequestMapping( value = "/{id}/expire", method = RequestMethod.PUT)
    public Message expireDataSource(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.expireDataSource(request, /*type, */id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while expire datasource";
            LOG.error(errorMessage, e);
            message = Message.error("过期数据源失败");
        }
        return message;

    }

    // test datasource connect
    @RequestMapping( value = "/{id}/{version}/connect", method = RequestMethod.PUT)
    public Message testConnect(HttpServletRequest request,/* @PathParam("type") String type, */@PathVariable("id") Long id,
                               @PathVariable("version") Long version) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.testConnect(request, /*type, */id, version);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while connect datasource";
            LOG.error(errorMessage, e);
            message = Message.error("数据源连接失效，请检查配置");
        }
        return message;
    }

    @RequestMapping( value = "/op/connect", method = RequestMethod.POST)
    public Message testConnectByMap(HttpServletRequest request,/* @PathParam("type") String type, */@Valid @RequestBody DataSourceCreateVO dataSourceCreateVO,
                                    BindingResult bindingResult) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.testConnectByVo(request, /*type, */dataSourceCreateVO);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while connect datasource";
            LOG.error(errorMessage, e);
            message = Message.error("数据源连接失效，请检查配置");
        }
        return message;
    }

    // delete datasource (physical)
    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest request, /*@PathParam("type") String type, */@PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.deleteDataSource(request, /*type, */id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while delete datasource";
            LOG.error(errorMessage, e);
            message = Message.error("删除数据源失败，存在引用依赖");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs", method = RequestMethod.GET)
    public Message queryDataSourceDBs(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.queryDataSourceDBs(request, type, id);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while query datasource";
            LOG.error(errorMessage, e);
            message = Message.error("数据源未发布或参数为空");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables", method = RequestMethod.GET)
    public Message queryDataSourceDBTables(HttpServletRequest request, @PathVariable("type") String type,
                                           @PathVariable("id") Long id, @PathVariable("dbName") String dbName) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.queryDataSourceDBTables(request, type, id, dbName);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting tables";
            LOG.error(errorMessage, e);
            message = Message.error("获取数据表失败");
        }
        return message;
    }

    @RequestMapping( value = "/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields", method = RequestMethod.GET)
    public Message queryDataSourceDBTableFields(HttpServletRequest request, @PathVariable("type") String type,
                                                @PathVariable("id") Long id, @PathVariable("dbName") String dbName,
                                                @PathVariable("tableName") String tableName) throws Exception {

        Message message = null;
        try{
            message = exchangisDataSourceService.queryDataSourceDBTableFields(request, type, id, dbName, tableName);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting table fields";
            LOG.error(errorMessage, e);
            message = Message.error("获取表字段失败");
        }
        return message;

    }

    @RequestMapping( value = "/fieldsmapping", method = RequestMethod.POST)
    public Message queryDataSourceDBTableFieldsMapping(HttpServletRequest request, @RequestBody FieldMappingVO vo) throws Exception {
        Message message = null;
        try{
            message = exchangisDataSourceService.queryDataSourceDBTableFieldsMapping(request, vo);
        } catch (ExchangisDataSourceException e) {
            String errorMessage = "Error occur while getting field List Information";
            LOG.error(errorMessage, e);
            message = Message.error("获取表字段列表信息失败，请检查数据源配置或更换数据源");
        }
        return message;
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
