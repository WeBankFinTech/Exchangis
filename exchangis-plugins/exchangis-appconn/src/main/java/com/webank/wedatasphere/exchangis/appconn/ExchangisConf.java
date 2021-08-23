package com.webank.wedatasphere.exchangis.appconn;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public interface ExchangisConf {
    public static final CommonVars<String> DSS_URL = CommonVars.apply("wds.dss.appconn.exchangis.dss.url", "http://127.0.0.1:8088/");

    public static final CommonVars<String> APP_NAME = CommonVars.apply("wds.dss.appconn.exchangis.app.name", "exchangis");
}
