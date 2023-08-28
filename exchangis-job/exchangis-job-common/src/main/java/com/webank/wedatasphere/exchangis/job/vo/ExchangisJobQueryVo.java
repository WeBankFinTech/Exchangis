package com.webank.wedatasphere.exchangis.job.vo;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobPageQuery;

public class ExchangisJobQueryVo extends ExchangisJobPageQuery {

    private static final Integer defaultCurrentPage = 1;

    private static final Integer defaultPageSize = 10;

    public ExchangisJobQueryVo(){
    }

    public ExchangisJobQueryVo(Long projectId, String jobType, String name) {
        this(projectId, jobType, name, defaultCurrentPage, defaultPageSize);
    }

    public ExchangisJobQueryVo(Long projectId, String jobType,
                               String name, Integer current, Integer size){
        this.projectId = projectId;
        this.jobType = jobType;
        this.jobName = name;
        this.current = current;
        this.size = size;
    }
}
