package com.webank.wedatasphere.exchangis.job.server.validator;

import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;

import java.util.List;

/**
 * Basic validator for exchangis job
 */
public interface JobValidator<T> {

    /**
     * Job validate result
     * @param jobName job name
     * @param contents content list
     * @param execUser  exec user
     * @return
     */
    JobValidateResult<T> doValidate(String jobName,
                                    List<ExchangisJobInfoContent> contents, String execUser);
}
