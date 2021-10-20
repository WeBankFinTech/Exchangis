package com.webank.wedatasphere.exchangis.job.datax.writer;

import com.webank.wedatasphere.exchangis.job.domain.Writer;

import java.util.Map;

public class DataxWriter extends Writer {
    protected String name;
    private Map parameter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }

}
