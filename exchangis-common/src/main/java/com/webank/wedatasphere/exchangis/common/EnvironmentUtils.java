package com.webank.wedatasphere.exchangis.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.DataWorkCloudApplication;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.server.utils.LinkisMainHelper;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.ApplicationContext;


/**
 * Environment utils
 */
public class EnvironmentUtils {

    private static final CommonVars<String> JVM_USER = CommonVars.apply("wds.exchangis.env.jvm.user", System.getProperty("user.name", "hadoop"));

    private static final CommonVars<String> SERVER_NAME = CommonVars.apply(LinkisMainHelper.SERVER_NAME_KEY(),  "exchangis");

    /**
     * Jvm user
     * @return user name
     */
    public static String getJvmUser(){
        return JVM_USER.getValue();
    }

    /**
     * Server name
     * @return name
     */
    public static String getServerName(){
        return SERVER_NAME.getValue();
    }

    /**
     * Get server address
     * @return address
     */
    public static String getServerAddress(){
        ApplicationContext context = DataWorkCloudApplication.getApplicationContext();
        String hostname;
        if (Configuration.PREFER_IP_ADDRESS()) {
            hostname =  context
                    .getEnvironment().getProperty("spring.cloud.client.ip-address");
        } else {
            hostname = context.getEnvironment().getProperty("eureka.instance.hostname", "");
            if (StringUtils.isBlank(hostname)) {
                hostname = Utils.getComputerName();
            }
        }
        String serverPort = context.getEnvironment().getProperty("server.port");
        return hostname + (StringUtils.isNotBlank(serverPort) ? ":" + serverPort : "");
    }
    /**
     * Get server host name
     * @return hostname
     */
    public static String getServerHost(){
        ApplicationContext context = DataWorkCloudApplication.getApplicationContext();
        if (Configuration.PREFER_IP_ADDRESS()) {
            return context
                    .getEnvironment().getProperty("spring.cloud.client.ip-address");
        } else {
            String hostname = context.getEnvironment().getProperty("eureka.instance.hostname", "");
            if (StringUtils.isBlank(hostname)) {
                return Utils.getComputerName();
            }
            return hostname;
        }
    }

    /**
     * Get server port
     * @return port number
     */
    public static Integer getServerPort(){
        String serverPort = DataWorkCloudApplication.getApplicationContext()
                .getEnvironment().getProperty("server.port");
        return StringUtils.isNotBlank(serverPort) ? Integer.parseInt(serverPort) : null;
    }

    /**
     * Examine if the server instance alive(use discovery client cache)
     * @param serverAddress server address
     * @return bool
     */
    public static boolean isServerAlive(String serverAddress){
        EurekaDiscoveryClient discoveryClient = DataWorkCloudApplication.getApplicationContext()
                .getBean(EurekaDiscoveryClient.class);
        return false;
    }

    /**
     * Get the server container startup time
     * @return timestamp
     */
    public static long getStartupTime(){
        return DataWorkCloudApplication.getApplicationContext().getStartupDate();
    }
}
