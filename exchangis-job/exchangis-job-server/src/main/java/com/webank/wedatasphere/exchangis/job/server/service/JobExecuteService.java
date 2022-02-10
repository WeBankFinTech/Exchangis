package com.webank.wedatasphere.exchangis.job.server.service;


import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.vo.*;
import org.apache.linkis.server.Message;

import java.util.Date;
import java.util.List;

public interface JobExecuteService {

    /**
     * Check if the user has the authority of execution job
     * @param jobExecutionId job execution id
     * @param userName user name
     * @return bool
     */
    boolean hasExecuteJobAuthority(String jobExecutionId, String userName);

    ExchangisCategoryLogVo getJobLogInfo(String jobExecutionId, LogQuery logQuery, String userName) throws ExchangisJobServerException;

    ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, LogQuery logQuery, String userName) throws ExchangisJobServerException, ExchangisTaskLaunchException;

    /**
     * Gets task Metrics
     *
     * @param taskid the task id
     * @param jobExecutionId      the job ExecutionId
     * @return the task launched metrics
     */
    ExchangisLaunchedTaskMetricsVO getLaunchedTaskMetrics(String taskid, String jobExecutionId, String userName) throws ExchangisJobServerException;

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
    /**
     * Kill job.
     *
     * @param jobExecutionId      the job ExecutionId
     */
    void killJob(String jobExecutionId);

}
