package com.webank.wedatasphere.exchangis.privilege;

import org.apache.linkis.common.conf.CommonVars;

public class PermissionConfiguration {

    /**
     * Default handover user
     */
    public static final CommonVars<String> DEFAULT_HANDOVER_USER  = CommonVars.apply("wds.exchangis.permission.handover.default-user", "hadoop");

}
