package com.webank.wedatasphere.exchangis.job.server.dto;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author tikazhang
 * @Date 2022/3/15 10:33
 */
public class IdCatalog {

    private Map<Long, Long> sqoop = Maps.newHashMap();

    private Map<Long, Long> datax = Maps.newHashMap();

    public Map<Long, Long> getSqoop() {
        return sqoop;
    }

    public void setSqoop(Map<Long, Long> sqoop) {
        this.sqoop = sqoop;
    }

    public Map<Long, Long> getDatax() {
        return datax;
    }

    public void setDatax(Map<Long, Long> datax) {
        this.datax = datax;
    }
}
