package com.webank.wedatasphere.exchangis.job.server.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.exchangis.job.launcher.entity.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;

public interface ExchangisLaunchTaskService extends IService<ExchangisLaunchTask> {

    /**
     * Gets task list(return task basic info).
     *
     * @param taskId          the task id
     * @param taskName        the task name
     * @param status          the status
     * @param launchStartTime the launch start time
     * @param launchEndTime   the launch end time
     * @param current         the current
     * @param size            the size
     * @return the task list
     */
    List<ExchangisTaskInfoVO> listTasks(Long taskId, String taskName, String status, Long launchStartTime,
                                        Long launchEndTime, int current, int size);

    /**
     * Count int.
     *
     * @param taskId          the task id
     * @param taskName        the task name
     * @param status          the status
     * @param launchStartTime the launch start time
     * @param launchEndTime   the launch end time
     * @return the int
     */
    int count(Long taskId, String taskName, String status, Long launchStartTime, Long launchEndTime);

    void delete(Long historyId) throws Exception;
}
