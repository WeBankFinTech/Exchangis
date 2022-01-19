package com.webank.wedatasphere.exchangis.job.server.service;


import com.webank.wedatasphere.exchangis.job.server.vo.*;
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
     * Gets job progress info
     * @param jobExecutionId      the job ExecutionId
     * @return the job tasks status
     */
    ExchangisJobProgressVo getExecutedJobProgressInfo(String jobExecutionId);

    /**
     * Gets Executed task list
     * @param jobExecutionId      the job ExecutionId
     * @return the launched taskList
     */
    List<ExchangisJobTaskVo> getExecutedJobTaskList(String jobExecutionId);

    /**
     * Gets Executed job list
     * @return the launched jobList
     */
    List<ExchangisLaunchedJobListVO> getExecutedJobList(Long jobId, String jobName, String status,
                                                        Long launchStartTime, Long launchEndTime, Integer  current, Integer size);

    /**
     * Count int.
     *
     * @param jobId          the job id
     * @param jobName        the job name
     * @param status          the status
     * @param launchStartTime the launch start time
     * @param launchEndTime   the launch end time
     * @return the int
     */
    int count(Long jobId, String jobName, String status, Long launchStartTime, Long launchEndTime);
}
