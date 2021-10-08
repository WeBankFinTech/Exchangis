package com.webank.wedatasphere.exchangis.job.launcher;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public class ExchangisJobConfiguration {
    public static final CommonVars<String> LINKIS_SERVER_URL = CommonVars.apply("wds.exchangis.linkis.serverurl", "");
}
