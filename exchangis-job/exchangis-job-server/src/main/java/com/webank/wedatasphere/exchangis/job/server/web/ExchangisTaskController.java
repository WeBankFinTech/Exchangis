package com.webank.wedatasphere.exchangis.job.server.web;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;
import com.webank.wedatasphere.linkis.server.Message;

/**
 * The type Exchangis task controller.
 *
 * @author ruiyang.qin
 * @date 2021/10/13
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis/tasks")
public class ExchangisTaskController {

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;

    @GET
    public Message listTasks(@QueryParam(value = "taskId") Long taskId,
                             @QueryParam(value = "taskName") String taskName, @QueryParam(value = "status") String status,
                             @QueryParam(value = "launchStartTime") Long launchStartTime,
                             @QueryParam(value = "launchEndTime") Long launchEndTime, @QueryParam(value = "current") int current,
                             @QueryParam(value = "size") int size) {
        List<ExchangisTaskInfoVO> taskList = exchangisLaunchTaskService.listTasks(taskId, taskName, status,
                launchStartTime, launchEndTime, current, size);
        int total = exchangisLaunchTaskService.count(taskId, taskName, status, launchStartTime, launchEndTime);
        return Message.ok().data("result", taskList).data("total", total);
    }

    @DELETE
    @Path("{taskId}")
    public Message deleteTask(@PathParam("taskId") Long taskId) throws Exception {
        this.exchangisLaunchTaskService.delete(taskId);
        return Message.ok();
    }

}
