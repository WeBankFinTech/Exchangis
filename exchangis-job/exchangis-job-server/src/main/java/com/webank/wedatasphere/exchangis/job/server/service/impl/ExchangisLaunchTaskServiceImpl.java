package com.webank.wedatasphere.exchangis.job.server.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobServerException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.launcher.entity.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;

@Service
public class ExchangisLaunchTaskServiceImpl extends ServiceImpl<ExchangisLaunchTaskMapper, ExchangisLaunchTask>
        implements ExchangisLaunchTaskService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;

    @Override
    public List<ExchangisTaskInfoVO> listTasks(Long taskId, String taskName, String status, Long launchStartTime,
                                               Long launchEndTime, int current, int size) throws ExchangisJobServerException{
        if (current <= 0) {
            current = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        int start = (current - 1) * size;

        Date startTime = launchStartTime == null ? null : new Date(launchStartTime);
        Date endTime = launchEndTime == null ? null : new Date(launchEndTime);
        List<ExchangisLaunchTask> taskList =
                this.baseMapper.listTasks(taskId, taskName, status, startTime, endTime, start, size);

        return taskList.stream().map(task -> {
            return modelMapper.map(task, ExchangisTaskInfoVO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public int count(Long taskId, String taskName, String status, Long launchStartTime, Long launchEndTime) {
        Date startTime = launchStartTime == null ? null : new Date(launchStartTime);
        Date endTime = launchEndTime == null ? null : new Date(launchEndTime);

        return this.baseMapper.count(taskId, taskName, status, startTime, endTime);
    }

    @Override
    public void delete(Long historyId) throws Exception {
        ExchangisLaunchTask task = this.baseMapper.selectById(historyId);
        if (null == task) {
            throw new ExchangisJobServerException(ExchangisDataSourceExceptionCode.DELETE_HISTORY_ERROR.getCode(), "Task " + historyId + " not exists.");
        }
        if (task.getStatus().equals("SUCCESS") || task.getStatus().equals("FAILED")) {
            this.baseMapper.deleteById(historyId);
        } else {
            throw new ExchangisJobServerException(ExchangisDataSourceExceptionCode.DELETE_HISTORY_ERROR.getCode(), "The status of task " + historyId + " is " + task.getStatus() + ", " +
                    "only 'SUCCESS' or 'FAILED' status can be deleted.");
        }
    }
}
