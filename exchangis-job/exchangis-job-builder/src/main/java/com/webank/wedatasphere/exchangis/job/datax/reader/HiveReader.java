package com.webank.wedatasphere.exchangis.job.datax.reader;

import java.util.Map;

public class HiveReader extends Reader {

    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }
}
