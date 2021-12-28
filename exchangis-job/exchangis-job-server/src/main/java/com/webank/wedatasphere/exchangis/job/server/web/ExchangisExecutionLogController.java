package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisExecutionService;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis/execution")
public class ExchangisExecutionLogController {

    @Resource
    private ExchangisExecutionService exchangisExecutionService;


    @GET
    @Path(value = "/tasks/{taskId}/logs")
    public Message getTaskExecutionLogs(@PathParam(value = "taskId") String taskId,
                             @QueryParam(value = "fromLine") Integer fromLine,
                             @QueryParam(value = "pageSize") Integer pageSize) {

        return this.exchangisExecutionService.getTaskLogInfo(taskId, fromLine, pageSize);
    }

}
