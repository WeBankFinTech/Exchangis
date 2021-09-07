package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis")
public class ExchangisDataSourceRestfulApi {

    private final ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    public ExchangisDataSourceRestfulApi(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    // 获取所有数据源类型
    @GET
    @Path("datasources/type")
    public Response listDataSourceTypes(@Context HttpServletRequest request) {
        Message message = this.exchangisDataSourceService.listDataSources(request);
        return Message.messageToResponse(message);
    }

    // 分页查询数据源列表
    @POST
    @Path("datasources/query")
    public Response create(@Context HttpServletRequest request, @RequestBody DataSourceQueryVO vo) throws Exception {
        Message message = this.exchangisDataSourceService.queryDataSources(request, vo);
        return Message.messageToResponse(message);
    }

    // 获取所有数据源
    @GET
    @Path("datasources")
    public Response listAllDataSources(@Context HttpServletRequest request) throws Exception {
        Message message = this.exchangisDataSourceService.listAllDataSources(request);
        return Message.messageToResponse(message);
    }

    // 获取数据源版本号列表
    @GET
    @Path("datasources/{id}/versions")
    public Response getDataSourceVersionsById(@Context HttpServletRequest request, @PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.getDataSourceVersionsById(request, id);
        return Message.messageToResponse(message);
    }

    // 创建数据源
    @POST
    @Path("datasources")
    public Response create(@Context HttpServletRequest request, /*@PathParam("type") String type, */@RequestBody Map<String, Object> json) throws Exception {
        Message message = this.exchangisDataSourceService.create(request,/* type, */json);
        return Message.messageToResponse(message);
    }

    // 获取某个数据源详情
    @GET
    @Path("datasources/{id}")
    public Response getDataSourceInfoById(@Context HttpServletRequest request, @PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.getDataSource(request, id);
        return Message.messageToResponse(message);
    }

    // 更新数据源
    @PUT
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}")
    public Response update(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id, Map<String, Object> json) throws Exception {
        Message message = this.exchangisDataSourceService.updateDataSource(request, /*type, */id, json);
        return Message.messageToResponse(message);
    }

    // 发布数据源
    @PUT
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}/{version}/publish")
    public Response publishDataSource(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id, @PathParam("version") Long version) throws Exception {
        Message message = this.exchangisDataSourceService.publishDataSource(request, /*type, */id, version);
        return Message.messageToResponse(message);
    }

    // 测试连接
    @PUT
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}/{version}/connect")
    public Response testConnect(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id, @PathParam("version") Long version) throws Exception {
        Message message = this.exchangisDataSourceService.testConnect(request, /*type, */id, version);
        return Message.messageToResponse(message);
    }

    // 删除数据源
    @DELETE
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}")
    public Response delete(@Context HttpServletRequest request, /*@PathParam("type") String type, */@PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.deleteDataSource(request, /*type, */id);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{type}/{id}/dbs")
    public Response queryDataSourceDBs(@Context HttpServletRequest request, @PathParam("type") String type, @PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.queryDataSourceDBs(request, type, id);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{type}/{id}/dbs/{dbName}/tables")
    public Response queryDataSourceDBTables(@Context HttpServletRequest request, @PathParam("type") String type, @PathParam("id") Long id, @PathParam("dbName") String dbName) throws Exception {
        Message message = this.exchangisDataSourceService.queryDataSourceDBTables(request, type, id, dbName);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields")
    public Response queryDataSourceDBTableFields(@Context HttpServletRequest request, @PathParam("type") String type, @PathParam("id") Long id, @PathParam("dbName") String dbName, @PathParam("tableName") String tableName) throws Exception {
        Message message = this.exchangisDataSourceService.queryDataSourceDBTableFields(request, type, id, dbName, tableName);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{type}/params/ui")
    public Response getParamsUI(@Context HttpServletRequest request, @PathParam("type")String type) {
        List<ElementUI> uis = this.exchangisDataSourceService.getDataSourceParamsUI(type);
        Message message = Message.ok().data("uis", uis);
        return Message.messageToResponse(message);
    }

}
