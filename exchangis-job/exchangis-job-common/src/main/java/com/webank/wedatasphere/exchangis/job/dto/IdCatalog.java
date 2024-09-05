package com.webank.wedatasphere.exchangis.job.dto;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/15 10:33
 */
public class IdCatalog {

    /**
     * Job ids
     */
    private Map<Long, Long> jobIds = Maps.newHashMap();

    public Map<Long, Long> getJobIds() {
        return jobIds;
    }

    public void setJobIds(Map<Long, Long> jobIds) {
        this.jobIds = jobIds;
    }
}
