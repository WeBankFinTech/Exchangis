package com.webank.wedatasphere.exchangis.project.provider;

import org.apache.linkis.common.conf.CommonVars;

/**
 * Project configuration
 */
public class ExchangisProjectConfiguration {

    /**
     * Project data sources
     */
    public static final CommonVars<String> PROJECT_DATASOURCES =
            CommonVars.apply("wds.exchangis.project.datasource.names", "");
}
