package com.webank.wedatasphere.exchangis.job.server.validator;

import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultJobValidator implements JobValidator<String>{

    @Override
    public JobValidateResult<String> doValidate(String jobName,
                                                List<ExchangisJobInfoContent> jobVo, String execUser) {
        return null;
    }
}
