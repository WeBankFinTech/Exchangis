package com.webank.wedatasphere.exchangis.common;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Environment utils
 */
public class EnvironmentUtils {

    private static final CommonVars<String> JVM_USER = CommonVars.apply("wds.exchangis.env.jvm.user", System.getProperty("user.name", "hadoop"));

    /**
     * Jvm user
     * @return user name
     */
    public static String getJvmUser(){
        return JVM_USER.getValue();
    }
}
