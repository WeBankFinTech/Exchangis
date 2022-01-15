package com.webank.wedatasphere.exchangis.job.server.service;


import com.webank.wedatasphere.exchangis.job.server.entity.ExchangisLaunchedJobEntity;
import com.webank.wedatasphere.exchangis.job.server.entity.ExchangisLaunchedTaskEntity;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobAndTaskStatusVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobTaskListVO;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisLaunchedTaskMetricsVO;
import org.apache.linkis.server.Message;

import java.util.List;

public interface ExchangisExecutionService {
    Message getJobLogInfo(String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows);

    Message getTaskLogInfo(String taskId, String jobExecutionId, Integer fromLine, Integer pageSize, String ignoreKeywords, String onlyKeywords, Integer lastRows);

    /**
     * Gets task Metrics
     *
     * @param taskid the task id
     * @param jobExecutionId      the job ExecutionId
     * @return the task launched metrics
     */
    ExchangisLaunchedTaskMetricsVO getLaunchedTaskMetrics(String taskid, String jobExecutionId);

    /**
     * Gets job And tasks status
     * @param jobExecutionId      the job ExecutionId
     * @return the job tasks status
     */
    ExchangisJobAndTaskStatusVO getExecutedJobAndTaskStatus(String jobExecutionId);

    /**
     * Gets Executed task list
     * @param jobExecutionId      the job ExecutionId
     * @return the launched taskList
     */
    List<ExchangisJobTaskListVO> getExecutedJobTaskList(String jobExecutionId);
}
