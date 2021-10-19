package com.webank.wedatasphere.exchangis.job.server.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
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
        Long launchEndTime, int current, int size) {
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
}
