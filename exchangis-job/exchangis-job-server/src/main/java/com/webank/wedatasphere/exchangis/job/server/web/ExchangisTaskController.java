package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * The type Exchangis task controller.
 *
 * @author ruiyang.qin
 * @date 2021/10/13
 */
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exchangis/task")
public class ExchangisTaskController {

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;

    @GET
    public Message getTaskList(@QueryParam(value = "taskId") long taskId,
                               @QueryParam(value = "taskName") String taskName) {
        List<ExchangisTaskInfoVO> taskList = exchangisLaunchTaskService.getTaskList(taskId, taskName);
        return Message.ok().data("result", taskList);
    }
}
