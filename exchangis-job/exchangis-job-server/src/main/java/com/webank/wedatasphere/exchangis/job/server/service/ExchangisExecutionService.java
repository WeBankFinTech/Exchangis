package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.linkis.server.Message;

public interface ExchangisExecutionService {
    Message getTaskLogInfo(String taskId, Integer fromLine, Integer pageSize);
}
