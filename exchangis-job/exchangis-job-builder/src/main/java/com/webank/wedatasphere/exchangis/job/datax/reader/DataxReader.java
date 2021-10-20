package com.webank.wedatasphere.exchangis.job.datax.reader;

import com.webank.wedatasphere.exchangis.job.domain.Reader;

import java.util.Map;

public class DataxReader extends Reader {
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
