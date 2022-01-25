package com.webank.wedatasphere.exchangis.job.server.web;

import com.webank.wedatasphere.exchangis.job.server.service.JobExecuteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "exchangis/execution", produces = {"application/json;charset=utf-8"})
public class ExchangisJobLogController {

    @Resource
    private JobExecuteService jobExecuteService;


   /* @RequestMapping(value = "/tasks/{taskId}/logs", method = RequestMethod.GET)
    public Message getTaskExecutionLogs(@PathVariable(value = "taskId") String taskId,
                                        @RequestParam(value = "fromLine", required = false) Integer fromLine,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return this.exchangisExecutionService.getTaskLogInfo(taskId, fromLine, pageSize);
    }*/

}
