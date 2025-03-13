package com.webank.wedatasphere.exchangis.privilege.domain;

import com.webank.wedatasphere.exchangis.job.api.ExchangisJobOpenService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class JobPermissionDefine extends AbstractPermissionInfo<ExchangisJobOpenService> {

    public JobPermissionDefine() {
    }

    @Override
    public void setIdentify(ApplicationContext context, String username) {
        this.service = context.getBean(ExchangisJobOpenService.class);
        List<ExchangisJobEntity> jobs = null;
        try {
            jobs = getService().queryJobsByUser(username);
        } catch (ExchangisJobException e) {
            throw new RuntimeException(e);
        }
        this.identify = jobs.stream().collect(Collectors.toMap(ExchangisJobEntity::getId, ExchangisJobEntity::getName));
    }
}