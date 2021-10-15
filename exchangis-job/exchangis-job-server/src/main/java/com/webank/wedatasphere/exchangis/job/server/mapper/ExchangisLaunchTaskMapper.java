package com.webank.wedatasphere.exchangis.job.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;

/**
 * The interface Exchangis launch task mapper.
 *
 * @author yuxin.yuan
 * @since 2021-09-08
 */
@Mapper
public interface ExchangisLaunchTaskMapper extends BaseMapper<ExchangisLaunchTask> {

    List<ExchangisLaunchTask> getTaskList(@Param("taskId") Long taskId, @Param("taskName") String taskName,
        @Param("status") String status, @Param("launchStartTime") Long launchStartTime,
        @Param("launchEndTime") Long launchEndTime, @Param("start") int start, @Param("size") int size);
}
