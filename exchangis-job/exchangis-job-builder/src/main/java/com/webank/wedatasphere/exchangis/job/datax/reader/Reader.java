package com.webank.wedatasphere.exchangis.job.datax.reader;

import com.webank.wedatasphere.exchangis.job.datax.domain.Content;

import java.util.Map;

public class Reader extends Content {

    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }

}
