package com.webank.wedatasphere.exchangis.job.reader;

import java.util.Map;

public class MysqlReader extends Reader {

    private Map parameter;

    public Map getParameter() {
        return parameter;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }
}
