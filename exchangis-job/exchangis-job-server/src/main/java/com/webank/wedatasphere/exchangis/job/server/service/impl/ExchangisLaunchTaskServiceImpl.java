package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangisLaunchTaskServiceImpl extends ServiceImpl<ExchangisLaunchTaskMapper, ExchangisLaunchTask>
        implements ExchangisLaunchTaskService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExchangisLaunchTaskService exchangisLaunchTaskService;

    @Override
    public List<ExchangisTaskInfoVO> getTaskList(long taskId, String taskName, String status, long launchTime, long completeTime, int current, int size) {
        LambdaQueryChainWrapper<ExchangisLaunchTask> query = exchangisLaunchTaskService.lambdaQuery();
        if (taskId != 0) {
            query.eq(ExchangisLaunchTask::getId, taskId);
        }
        if (StringUtils.isNotBlank(taskName)) {
            query.like(ExchangisLaunchTask::getTaskName, taskName.trim());
        }
        if (StringUtils.isNotBlank(status)) {
            query.like(ExchangisLaunchTask::getStatus, status.trim());
        }
        if (launchTime != 0) {
            String lt = DateFormatUtils.format(launchTime, "yyyy-MM-dd HH:mm:ss");
            query.like(ExchangisLaunchTask::getLaunchTime, lt);
        }
        if (completeTime != 0) {
            String ct = DateFormatUtils.format(completeTime, "yyyy-MM-dd HH:mm:ss");
            query.like(ExchangisLaunchTask::getCompleteTime, ct);
        }

        Page<ExchangisLaunchTask> page = new Page<>(current, size);
        List<ExchangisLaunchTask> exchangisLaunchTasks = query.page(page).getRecords();

        List<ExchangisTaskInfoVO> returnlist = new ArrayList<>();
        exchangisLaunchTasks.stream().forEach(task -> returnlist.add(modelMapper.map(task, ExchangisTaskInfoVO.class)));

        return returnlist;
    }
}
