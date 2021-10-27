package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
import com.webank.wedatasphere.exchangis.datasource.vo.FieldMappingVO;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
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

    // list all datasource types
    @GET
    @Path("datasources/type")
    public Response listDataSourceTypes(@Context HttpServletRequest request) throws Exception {
        Message message = this.exchangisDataSourceService.listDataSources(request);
        return Message.messageToResponse(message);
    }

    // query paged datasource
    @POST
    @Path("datasources/query")
    public Response create(@Context HttpServletRequest request, @RequestBody DataSourceQueryVO vo) throws Exception {
        Message message = this.exchangisDataSourceService.queryDataSources(request, vo);
        return Message.messageToResponse(message);
    }

    // list all datasources
    @GET
    @Path("datasources")
    public Response listAllDataSources(
            @Context HttpServletRequest request,
            @QueryParam("typeId") Long typeId,
            @QueryParam("typeName") String typeName,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size
    ) throws Exception {
        Message message = this.exchangisDataSourceService.listAllDataSources(request, typeName, typeId, page, size);
        return Message.messageToResponse(message);
    }

    // get datasource key define
    @GET
    @Path("datasources/types/{dataSourceTypeId}/keydefines")
    public Response getDataSourceKeyDefine(
            @Context HttpServletRequest request,
            @PathParam("dataSourceTypeId") Long dataSourceTypeId
    ) throws Exception {
        Message message = this.exchangisDataSourceService.getDataSourceKeyDefine(request, dataSourceTypeId);
        return Message.messageToResponse(message);
    }


    // get datasource version list
    @GET
    @Path("datasources/{id}/versions")
    public Response getDataSourceVersionsById(@Context HttpServletRequest request, @PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.getDataSourceVersionsById(request, id);
        return Message.messageToResponse(message);
    }

    // create datasource
    @POST
    @Path("datasources")
    public Response create(@Context HttpServletRequest request, /*@PathParam("type") String type, */@RequestBody Map<String, Object> json) throws Exception {
        Message message = this.exchangisDataSourceService.create(request,/* type, */json);
        return Message.messageToResponse(message);
    }

    // get datasource details
    @GET
    @Path("datasources/{id}")
    public Response getDataSourceInfoById(@Context HttpServletRequest request, @PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.getDataSource(request, id);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{id}/connect_params")
    public Response getDataSourceConnectParamsById(@Context HttpServletRequest request, @PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.getDataSourceConnectParamsById(request, id);
        return Message.messageToResponse(message);
    }

    // update datasource and parameters (insert new record in datasource_version table)
    @PUT
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}")
    public Response update(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id, Map<String, Object> json) throws Exception {
        Message message = this.exchangisDataSourceService.updateDataSource(request, /*type, */id, json);
        return Message.messageToResponse(message);
    }

    // publish datasource
    @PUT
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}/{version}/publish")
    public Response publishDataSource(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id, @PathParam("version") Long version) throws Exception {
        Message message = this.exchangisDataSourceService.publishDataSource(request, /*type, */id, version);
        return Message.messageToResponse(message);
    }

    // expire datasource
    @PUT
    @Path("datasources/{id}/expire")
    public Response expireDataSource(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id) throws Exception {
        Message message = this.exchangisDataSourceService.expireDataSource(request, /*type, */id);
        return Message.messageToResponse(message);
    }

    // test datasource connect
    @PUT
//    @Path("datasources/{type}/{id}")
    @Path("datasources/{id}/{version}/connect")
    public Response testConnect(@Context HttpServletRequest request,/* @PathParam("type") String type, */@PathParam("id") Long id, @PathParam("version") Long version) throws Exception {
        Message message = this.exchangisDataSourceService.testConnect(request, /*type, */id, version);
        return Message.messageToResponse(message);
    }

    // delete datasource (physical)
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

    @POST
    @Path("datasources/fieldsmapping")
    public Response queryDataSourceDBTableFieldsMapping(@Context HttpServletRequest request, @RequestBody FieldMappingVO vo) throws Exception {
        Message message = this.exchangisDataSourceService.queryDataSourceDBTableFieldsMapping(request, vo);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("datasources/{type}/params/ui")
    public Response getParamsUI(
            @Context HttpServletRequest request,
            @PathParam("type") String type,
            @QueryParam(value = "dir") String dir
    ) {
        List<ElementUI> uis = this.exchangisDataSourceService.getDataSourceParamsUI(type, dir);
        Message message = Message.ok().data("uis", uis);
        return Message.messageToResponse(message);
    }

}
