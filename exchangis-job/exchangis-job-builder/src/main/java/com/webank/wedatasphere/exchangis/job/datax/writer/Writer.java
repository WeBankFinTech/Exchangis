package com.webank.wedatasphere.exchangis.job.datax.writer;

import com.webank.wedatasphere.exchangis.job.datax.domain.Content;

import java.util.Map;

public class Writer extends Content {
    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }
}
