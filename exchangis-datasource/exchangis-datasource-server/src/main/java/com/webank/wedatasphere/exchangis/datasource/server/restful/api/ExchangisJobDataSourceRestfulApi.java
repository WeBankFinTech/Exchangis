package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.service.ExchangisDataSourceService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// TODO 这里仅仅为了测试，JOB的接口在另外的 Controller 中
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis")
public class ExchangisJobDataSourceRestfulApi {

    private final ExchangisDataSourceService exchangisDataSourceService;

    @Autowired
    public ExchangisJobDataSourceRestfulApi(ExchangisDataSourceService exchangisDataSourceService) {
        this.exchangisDataSourceService = exchangisDataSourceService;
    }

    // 根据 任务ID 获取该任务的数据源所有配置项 UI 数据
    @GET
    @Path("jobs/{jobId}/datasource/ui")
    public Response getJobDataSourcesUI(@Context HttpServletRequest request, @PathParam("jobId")Long jobId) {
//        ExchangisDataSourceUIViewer jobDataSourceUI = this.exchangisDataSourceService.getJobDataSourceUIs(jobId);
        List<ExchangisDataSourceUIViewer> ui = this.exchangisDataSourceService.getJobDataSourceUIs(jobId);
        Message message = Message.ok().data("ui", ui);
        return Message.messageToResponse(message);
    }

    // 根据 任务引擎类型 获取该引擎的配置项 UI 数据
    @GET
    @Path("jobs/engine/{engineType}/settings/ui")
    public Response getJobEngineSettingsUI(@Context HttpServletRequest request, @PathParam("engineType")String engineType) {
        List<ElementUI> jobSettingsUI = this.exchangisDataSourceService.getJobEngineSettingsUI(engineType);
        Message message = Message.ok().data("ui", jobSettingsUI);
        return Message.messageToResponse(message);
    }

    // 根据 任务ID 获取该任务的数据源配置项 UI 数据
    @GET
    @Path("jobs/{jobId}/datasource/params/ui")
    public Response getJobDataSourceParamsUI(@Context HttpServletRequest request, @PathParam("jobId")Long jobId) {
        Message message = this.exchangisDataSourceService.getJobDataSourceParamsUI(jobId);
        return Message.messageToResponse(message);
    }

    // 根据 任务ID 获取该任务的数据源字段映射 UI 数据
    @GET
    @Path("jobs/{jobId}/datasource/transforms/ui")
    public Response getJobTransformsUI(@Context HttpServletRequest request, @PathParam("jobId")Long jobId) {
        Message message = this.exchangisDataSourceService.getJobDataSourceTransformsUI(jobId);
        return Message.messageToResponse(message);
    }

    // 根据 任务ID 获取该任务的数据源引擎配置项 UI 数据
    @GET
    @Path("jobs/{jobId}/{jobName}/datasource/settings/ui")
    public Response getJobSettingsUI(@Context HttpServletRequest request, @PathParam("jobId")Long jobId, @PathParam("jobName")String jobName) throws Exception {
        Message message = this.exchangisDataSourceService.getJobDataSourceSettingsUI(jobId, jobName);
        return Message.messageToResponse(message);
    }
}
