package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;

import java.util.List;

public interface TaskObserverService {


    List<LaunchableExchangisTask> onPublishLaunchableTask(int limitSize);
}
