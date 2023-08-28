package com.webank.wedatasphere.exchangis.job.server.service;


import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.exception.ExchangisTaskLaunchException;
import com.webank.wedatasphere.exchangis.job.log.LogQuery;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import com.webank.wedatasphere.exchangis.job.server.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface JobExecuteService {

    /**
     * Check if the user has the authority of execution job
     * @param jobExecutionId job execution id
     * @return bool
     */
    ExchangisCategoryLogVo getJobLogInfo(String jobExecutionId, LogQuery logQuery) throws ExchangisJobServerException;

    ExchangisCategoryLogVo getTaskLogInfo(String taskId, String jobExecutionId, LogQuery logQuery) throws ExchangisJobServerException, ExchangisTaskLaunchException;

    /**
     * Gets task Metrics
     *
     * @param taskid the task id
     * @param jobExecutionId      the job ExecutionId
     * @return the task launched metrics
     */
    ExchangisLaunchedTaskMetricsVo getLaunchedTaskMetrics(String taskid, String jobExecutionId) throws ExchangisJobServerException;

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
    ExchangisJobProgressVo getJobStatus(String jobExecutionId) throws ExchangisJobServerException;

    /**
     * Gets Executed task list
     * @param jobExecutionId      the job ExecutionId
     * @return the launched taskList
     */
    List<ExchangisJobTaskVo> getExecutedJobTaskList(String jobExecutionId) throws ExchangisJobServerException;

    /**
     * Gets Executed job list
     * @return the launched jobList
     */
    List<ExchangisLaunchedJobListVo> getExecutedJobList(String jobExecutionId, String jobName, String status,
                                                        Long launchStartTime, Long launchEndTime, int  current, int size, HttpServletRequest request) throws ExchangisJobServerException;

    /**
     * Count int.
     *
     * @param jobExecutionId          the job id
     * @param jobName        the job name
     * @param status          the status
     * @param launchStartTime the launch start time
     * @param launchEndTime   the launch end time
     * @return the int
     */
    int count(String jobExecutionId, String jobName, String status, Long launchStartTime, Long launchEndTime, HttpServletRequest request);

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
    void killJob(String jobExecutionId) throws ExchangisJobServerException;

    /**
     * @param jobExecutionId      the job ExecutionId
     */
    void deleteJob(String jobExecutionId) throws ExchangisJobServerException;

    List<String> allTaskStatus(String jobExecutionId) throws ExchangisJobServerException;
}
