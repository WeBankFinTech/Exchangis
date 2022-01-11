package com.webank.wedatasphere.exchangis.job.server.web;

import java.util.List;

import org.apache.linkis.server.Message;
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
public class ExchangisTaskController {

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
        List<ExchangisTaskInfoVO> taskList = exchangisLaunchTaskService.listTasks(taskId, taskName, status,
                launchStartTime, launchEndTime, current, size);
        int total = exchangisLaunchTaskService.count(taskId, taskName, status, launchStartTime, launchEndTime);
        return Message.ok().data("result", taskList).data("total", total);
    }

    @RequestMapping( value = "{taskId}", method = RequestMethod.DELETE)
    public Message deleteTask(@PathVariable("taskId") Long taskId) throws Exception {
        this.exchangisLaunchTaskService.delete(taskId);
        return Message.ok();
    }

}
