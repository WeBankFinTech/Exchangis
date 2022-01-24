package com.webank.wedatasphere.exchangis.job.launcher.entity;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.domain.task.TaskStatus;

/**
 * Task entity (could be persisted)
 */
public interface ExchangisTaskEntity extends ExchangisTask {

    /**
     * Job id
     * @return long value
     */
    Long getJobId();

    void setJobId(Long jobId);

    /**
     * Job name
     * @return string value
     */
    String jobName();

    void setJobName(String jobName);


    /**
     * Task status
     * @return status enum
     */
    TaskStatus getStatus();


    void setStatus(TaskStatus status);

    /**
     * Progress
     * @return 0.0 to 1.0, default 0.0
     */
    double getProgress();

    void setProgress(double progress);

    /**
     * Error code
     * @return default null
     */
    Integer getErrorCode();

    void setErrorCode(Integer code);

    /**
     * Error message
     * @return default null
     */
    String getErrorMessage();

    void setErrorMessage(String errorMessage);

    /**
     * Retry number
     * @return default 0
     */
    Integer getRetryNum();

    void setRetryNum(Integer retryNum);
}
