package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateErrorEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateInitEvent;
import com.webank.wedatasphere.exchangis.job.server.execution.generator.events.TaskGenerateSuccessEvent;
import com.webank.wedatasphere.exchangis.job.server.service.TaskGenerateService;
import org.springframework.stereotype.Service;

/**
 * Task generate service
 */
@Service
public class DefaultTaskGenerateService implements TaskGenerateService {
    @Override
    public void onError(TaskGenerateErrorEvent errorEvent) {

    }

    @Override
    public void onInit(TaskGenerateInitEvent initEvent) {

    }

    @Override
    public void onSuccess(TaskGenerateSuccessEvent successEvent) {

    }
}
