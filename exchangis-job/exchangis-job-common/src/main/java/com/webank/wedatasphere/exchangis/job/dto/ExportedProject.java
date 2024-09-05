package com.webank.wedatasphere.exchangis.job.dto;

import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tikazhang
 * @Date 2022/3/14 12:22
 */
public class ExportedProject {
    /**
     * Project name
     */
    private String name;
    /**
     * Job list
     */
    private List<ExchangisJobVo> jobs = new ArrayList<>();

    public List<ExchangisJobVo> getJobs() {
        return jobs;
    }

    public void setJobs(List<ExchangisJobVo> jobs) {
        this.jobs = jobs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
