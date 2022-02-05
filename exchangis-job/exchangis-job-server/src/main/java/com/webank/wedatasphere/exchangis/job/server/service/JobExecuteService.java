package com.webank.wedatasphere.exchangis.job.server.service;


import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.vo.*;
import org.apache.linkis.server.Message;

import java.util.List;

public interface JobExecuteService {

    ExchangisCategoryLogVo getJobLogInfo(String jobExecutionId, Integer fromLine, Integer pageSize,
                                         String ignoreKeywords, String onlyKeywords, Integer lastRows) throws ExchangisJobServerException;

    ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, Integer fromLine, Integer pageSize,
                                          String ignoreKeywords, String onlyKeywords, Integer lastRows) throws ExchangisJobServerException;

    /**
     * Gets task Metrics
     *
     * @param taskid the task id
     * @param jobExecutionId      the job ExecutionId
     * @return the task launched metrics
     */
    ExchangisLaunchedTaskMetricsVO getLaunchedTaskMetrics(String taskid, String jobExecutionId) throws ExchangisJobServerException;

    /**
     * Gets job progress info
     * @param jobExecutionId      the job ExecutionId
     * @return the job tasks status
     */
    ExchangisJobProgressVo getExecutedJobProgressInfo(String jobExecutionId) throws ExchangisJobServerException;

    /**
     * Gets job status info
     * @param jobExecutionId      the job ExecutionId
     * @return the job status
     */
    ExchangisJobProgressVo getJobStatus(String jobExecutionId);

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

    /**
     * Execute job
     * @param jobInfo job info
     * @return job execution id
     * @throws ExchangisJobServerException
     */
    String executeJob(ExchangisJobInfo jobInfo, String execUser) throws ExchangisJobServerException;
}
