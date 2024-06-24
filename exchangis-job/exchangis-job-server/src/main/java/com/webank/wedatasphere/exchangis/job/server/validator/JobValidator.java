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
     * @param jobVo job vo
     * @param execUser
     * @return
     */
    JobValidateResult<T> doValidate(List<ExchangisJobInfoContent> jobVo, String execUser);
}
