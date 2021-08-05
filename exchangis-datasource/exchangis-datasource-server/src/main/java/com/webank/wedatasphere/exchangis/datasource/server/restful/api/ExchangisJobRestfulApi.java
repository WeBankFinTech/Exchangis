package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobInfoMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
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
public class ExchangisJobRestfulApi {

    private final ExchangisDataSourceContext context;
    private final ExchangisJobInfoMapper exchangisJobInfoMapper;
    private final ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;
    private final ExchangisDataSourceService exchangisDataSourceService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ExchangisJobRestfulApi(ExchangisDataSourceService exchangisDataSourceService, ExchangisDataSourceContext context, ExchangisJobInfoMapper exchangisJobInfoMapper, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.exchangisDataSourceService = exchangisDataSourceService;
        this.context = context;
        this.exchangisJobInfoMapper = exchangisJobInfoMapper;
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }

    @GET
    @Path("jobs/{jobId}/datasource/ui")
    public Response getJobDataSourcesUI(@Context HttpServletRequest request, @PathParam("jobId")Long jobId) {
        ExchangisDataSourceUIViewer jobDataSourceUI = this.exchangisDataSourceService.getJobDataSourceUIs(jobId);
        Message message = Message.ok().data("ui", jobDataSourceUI);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("jobs/engine/{engineType}/settings/ui")
    public Response getJobEngineSettingsUI(@Context HttpServletRequest request, @PathParam("engineType")String engineType) {
        List<ElementUI> jobSettingsUI = this.exchangisDataSourceService.getJobEngineSettingsUI(engineType);
        Message message = Message.ok().data("ui", jobSettingsUI);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("jobs/{jobId}/datasource/params/ui")
    public Response getJobParamsUI(@Context HttpServletRequest request) {
        Message message = Message.ok();
        return Message.messageToResponse(message);
    }

    @GET
    @Path("jobs/{jobId}/datasource/transforms/ui")
    public Response getJobTransformsUI(@Context HttpServletRequest request) {
        Message message = Message.ok();
        return Message.messageToResponse(message);
    }

    @GET
    @Path("jobs/{jobId}/datasource/settings/ui")
    public Response getJobSettingsUI(@Context HttpServletRequest request) {
        Message message = Message.ok();
        return Message.messageToResponse(message);
    }
}
