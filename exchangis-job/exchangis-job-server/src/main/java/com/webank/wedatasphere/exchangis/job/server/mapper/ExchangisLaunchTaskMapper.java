package com.webank.wedatasphere.exchangis.job.server.mapper;

import java.util.Date;
import java.util.List;

import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskStatusMetricsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.exchangis.job.launcher.entity.ExchangisLaunchTask;

/**
 * The interface Exchangis launch task mapper.
 *
 * @author yuxin.yuan
 * @since 2021-09-08
 */
@Mapper
public interface ExchangisLaunchTaskMapper extends BaseMapper<ExchangisLaunchTask> {

    List<ExchangisLaunchTask> listTasks(@Param("taskId") Long taskId, @Param("taskName") String taskName,
        @Param("status") String status, @Param("launchStartTime") Date launchStartTime,
        @Param("launchEndTime") Date launchEndTime, @Param("start") int start, @Param("size") int size);

    int count(@Param("taskId") Long taskId, @Param("taskName") String taskName, @Param("status") String status,
        @Param("launchStartTime") Date launchStartTime, @Param("launchEndTime") Date launchEndTime);

    ExchangisTaskStatusMetricsDTO getTaskMetricsByStatus(String status);

}
