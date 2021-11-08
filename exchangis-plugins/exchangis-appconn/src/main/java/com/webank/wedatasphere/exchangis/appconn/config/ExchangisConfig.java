package com.webank.wedatasphere.exchangis.appconn.config;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;

public class ExchangisConfig {

    public final static String baseUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/exchangis";
    public final static String EXCHANGIS_APPCONN_NAME = CommonVars.apply("wds.dss.appconn.exchangis.name", "Exchangis").getValue();

}
