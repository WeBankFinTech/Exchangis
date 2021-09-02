package com.webank.wedatasphere.exchangis.job.writer;

import java.util.Map;

public class HiveWriter extends Writer {

    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }
}
