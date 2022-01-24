package com.webank.wedatasphere.exchangis.job.server.dao;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tikazhang
 */
@Mapper
public interface LaunchedTaskDao {

    /**
     * judge task whether launch(insert) or update
     * @param launchedExchangisTaskEntity taskEntity
     * @return insert/update result (1:insert 2:update)
     */
    int insertLaunchedTaskOrUpdate(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * insert launchedTask
     * @param launchedExchangisTaskEntity
     */
    void insertLaunchedTask(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * delete launchedTask
     * @param taskId
     */
    void deleteLaunchedTask(@Param("taskId")String taskId);

    /**
     * upgrade launchedTask
     * @param launchedExchangisTaskEntity
     */
    void upgradeLaunchedTask(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * search launchedTask
     * @param taskId
     */

    LaunchedExchangisTaskEntity getLaunchedTaskEntity(@Param("taskId") String taskId);

    /**
     * upgrade launchedTask metrics
     * @param metrics
     * @param taskId
     */

    void upgradeLaunchedTaskMtrics(@Param("metrics") String metrics, @Param("taskId") String taskId);

    /**
     * upgrade launchedTask status
     * @param status
     * @param taskId
     */

    void upgradeLaunchedTaskStatus(@Param("status") String status, @Param("taskId") String taskId);

    /**
     * search launchedTaskList
     * @param jobExecutionId
     */

    List<LaunchedExchangisTaskEntity> selectTaskListByJobExecutionId(@Param("jobExecutionId") String jobExecutionId);

    /**
     * search getTaskMetrics
     * @param jobExecutionId
     */

    List<String> getTaskMetricsByJobExecutionId(@Param("jobExecutionId") String jobExecutionId);

    /**
     * search launchedTaskList by taskId and jobExecutionId
     * @param jobExecutionId
     */

    LaunchedExchangisTaskEntity getLaunchedTaskMetrics(@Param("jobExecutionId") String jobExecutionId, @Param("taskId") String taskId);


}
