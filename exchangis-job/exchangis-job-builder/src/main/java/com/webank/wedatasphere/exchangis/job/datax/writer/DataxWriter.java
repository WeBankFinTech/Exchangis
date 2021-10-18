package com.webank.wedatasphere.exchangis.job.datax.writer;

import com.webank.wedatasphere.exchangis.job.domain.Writer;

import java.util.Map;

public class DataxWriter extends Writer {
    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }
}
