package com.webank.wedatasphere.exchangis.common.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;

import java.util.ArrayList;
import java.util.List;

/**
 * Global configuration for exchangis
 */
public class GlobalConfiguration {
    public static final CommonVars<String> ADMIN_USERS = CommonVars.apply(
            "wds.exchangis.common.auth.admin", "hadoop");

    public static final CommonVars<Boolean> PROXY_MODE = CommonVars.apply(
            "wds.exchangis.common.user.proxy.mode", false
    );
    public static final String AUTH_SEPARATOR = ",";

    private static final List<String> administrators = new ArrayList<>();

    static {
        String adminStr = ADMIN_USERS.getValue();
        if (StringUtils.isNotBlank(adminStr)) {
            for (String admin : adminStr.split(AUTH_SEPARATOR)) {
                if (StringUtils.isNotBlank(admin)) {
                    administrators.add(admin);
                }
            }
        }
    }

    /**
     * Get admin user
     * @return username
     */
    public static String getAdminUser(){
        return administrators.size() > 0? administrators.get(0) : null;
    }

    /**
     * Is admin user
     * @param username username
     * @return bool
     */
    public static boolean isAdminUser(String username){
        return administrators.contains(username);
    }

}
