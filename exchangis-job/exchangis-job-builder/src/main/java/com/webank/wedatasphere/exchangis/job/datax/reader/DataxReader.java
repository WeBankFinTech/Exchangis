package com.webank.wedatasphere.exchangis.job.datax.reader;

import com.webank.wedatasphere.exchangis.job.domain.Reader;

import java.util.Map;

public class DataxReader extends Reader {

    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }

}
