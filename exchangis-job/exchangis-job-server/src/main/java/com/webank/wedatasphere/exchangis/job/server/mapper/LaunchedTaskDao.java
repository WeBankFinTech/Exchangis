package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.launcher.entity.LaunchedExchangisTaskEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 *
 */
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
     * Delete task in version (date)
     * @param taskId task id
     * @param versionDate version date
     */
    int deleteLaunchedTaskInVersion(@Param("taskId")String taskId, @Param("version")Integer version);

    /**
     * Update date in version
     * @param updateDate update date
     * @param version version
     * @return affect rows
     */
    int updateDateInVersion(@Param("taskId")String taskId, @Param("updateDate")Date updateDate, @Param("version")Integer version);

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

    void upgradeLaunchedTaskMetrics(@Param("taskId") String taskId, @Param("metrics") String metrics, @Param("updateTime")Date updateTime);

    /**
     * upgrade launchedTask status
     * @param status
     * @param taskId
     */

    void upgradeLaunchedTaskStatus(@Param("taskId") String taskId, @Param("status") String status,  @Param("updateTime")Date updateTime);

    /**
     * upgrade launchedTask progress
     * @param taskId
     * @param progress
     * @param updateTime
     */
    void upgradeLaunchedTaskProgress(@Param("taskId") String taskId, @Param("progress") Float progress, @Param("updateTime")Date updateTime);


    /**
     * Sum the progress value
     * @param jobExecutionId job execution id
     * @return sum result
     */
    float sumProgressByJobExecutionId(@Param("jobExecutionId") String jobExecutionId);
    /**
     * Update the launch information
     * @param launchedExchangisTaskEntity entity
     */
    void updateLaunchInfo(LaunchedExchangisTaskEntity launchedExchangisTaskEntity);

    /**
     * search launchedTaskList
     * @param jobExecutionId
     */

    List<LaunchedExchangisTaskEntity> selectTaskListByJobExecutionId(@Param("jobExecutionId") String jobExecutionId);

    /**
     * Select status list
     * @param jobExecutionId job execution id
     * @return
     */
    List<String> selectTaskStatusByJobExecutionId(@Param("jobExecutionId")String jobExecutionId);
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

    /**
     * Get launched task status
     * @param taskId
     * @return
     */
    String getLaunchedTaskStatus(@Param("taskId") String taskId);

    /**
     * search TaskStatusList
     * @param
     */

    List<String> getTaskStatusList(@Param("jobExecutionId") String jobExecutionId);

    /**
     * delete task
     * @param
     */

    void deleteTask(@Param("jobExecutionId") String jobExecutionId);

    /**
     * Get the expired tasks with status
     * @param instance instance
     * @param status status
     * @param expireTime expire time
     * @param limitSize limit size
     * @return entities
     */
    List<LaunchedExchangisTaskEntity> getLaunchedTaskInExpire(@Param("instance")String instance,
                                                              @Param("status")String status,
                                                              @Param("expireTime") Date expireTime,
                                                              @Param("limitSize")Integer limitSize);

}
