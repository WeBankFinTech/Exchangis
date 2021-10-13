package com.webank.wedatasphere.exchangis.job.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;

import java.util.List;

public interface ExchangisLaunchTaskService extends IService<ExchangisLaunchTask> {

    /**
     * Gets task list(return task basic info).
     *
     * @param taskId   the task id
     * @param taskName the task name
     * @return the task list
     */
    List<ExchangisTaskInfoVO> getTaskList(long taskId, String taskName);
}
