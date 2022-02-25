package com.webank.wedatasphere.exchangis.job.server.restful;
import java.util.List;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;
import org.springframework.web.bind.annotation.*;

/**
 * The type Exchangis task controller.
 *
 * @author ruiyang.qin
 * @date 2021/10/13
 */
@RestController
@RequestMapping(value = "exchangis/tasks", produces = {"application/json;charset=utf-8"})
public class ExchangisTaskRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisTaskRestfulApi.class);

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Message listTasks(@RequestParam(value = "taskId", required = false) Long taskId,
                             @RequestParam(value = "taskName", required = false) String taskName,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "launchStartTime", required = false) Long launchStartTime,
                             @RequestParam(value = "launchEndTime", required = false) Long launchEndTime,
                             @RequestParam(value = "current", required = false) int current,
                             @RequestParam(value = "size", required = false) int size) {
        Message message = Message.ok("获取所有任务的列表");
        try {
            List<ExchangisTaskInfoVO> taskList = exchangisLaunchTaskService.listTasks(taskId, taskName, status,
                    launchStartTime, launchEndTime, current, size);
            int total = exchangisLaunchTaskService.count(taskId, taskName, status, launchStartTime, launchEndTime);
            message.data("result", taskList);
            message.data("total", total);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while getting taskList: [taskId: " + taskId + "taskName" + taskName + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        return message;
        //return Message.ok().data("result", taskList).data("total", total);
    }

    @RequestMapping( value = "{taskId}", method = RequestMethod.DELETE)
    public Message deleteTask(@PathVariable("taskId") Long taskId) throws Exception {
        Message message = Message.ok("删除任务");
        try {
            this.exchangisLaunchTaskService.delete(taskId);
        } catch (ExchangisJobServerException e) {
            String errorMessage = "Error occur while delete task: [taskId: " + taskId + "]";
            LOG.error(errorMessage, e);
            message = Message.error(message + ", reason: " + e.getMessage());
        }
        return message;
        //return Message.ok();
    }

}
