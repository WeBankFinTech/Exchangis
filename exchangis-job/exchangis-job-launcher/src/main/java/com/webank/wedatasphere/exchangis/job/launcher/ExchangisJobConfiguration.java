package com.webank.wedatasphere.exchangis.job.launcher;


import org.apache.linkis.common.conf.CommonVars;

public class ExchangisJobConfiguration {
    public static final CommonVars<String> LINKIS_SERVER_URL = CommonVars.apply("wds.exchangis.linkis.serverurl", "");
}
