package com.webank.wedatasphere.exchangis.job.server.dto;

import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVo;

import java.util.List;

/**
 * @author tikazhang
 * @Date 2022/3/14 12:22
 */
public class ExportedProject {
    String name;
    List<ExchangisJobVo> sqoops;
    List<ExchangisJobVo> dataxes;

    public List<ExchangisJobVo> getSqoops() {
        return sqoops;
    }

    public void setSqoops(List<ExchangisJobVo> sqoops) {
        this.sqoops = sqoops;
    }

    public List<ExchangisJobVo> getDataxes() {
        return dataxes;
    }

    public void setDataxes(List<ExchangisJobVo> dataxes) {
        this.dataxes = dataxes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
