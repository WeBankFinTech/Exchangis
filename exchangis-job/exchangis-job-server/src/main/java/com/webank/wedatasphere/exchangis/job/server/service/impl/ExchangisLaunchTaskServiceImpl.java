package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisLaunchTaskService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisTaskInfoVO;
import org.apache.commons.lang.StringUtils;
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
    public List<ExchangisTaskInfoVO> getTaskList(long taskId, String taskName) {
        LambdaQueryChainWrapper<ExchangisLaunchTask> query =
                exchangisLaunchTaskService.lambdaQuery().eq(ExchangisLaunchTask::getId, taskId);
        if (StringUtils.isNotBlank(taskName)) {
            query.like(ExchangisLaunchTask::getTaskName, taskName.trim());
        }
        List<ExchangisLaunchTask> exchangisLaunchTasks = query.list();

        List<ExchangisTaskInfoVO> returnlist = new ArrayList<>();
        exchangisLaunchTasks.stream().forEach(task -> returnlist.add(modelMapper.map(task, ExchangisTaskInfoVO.class)));

        return returnlist;
    }
}
