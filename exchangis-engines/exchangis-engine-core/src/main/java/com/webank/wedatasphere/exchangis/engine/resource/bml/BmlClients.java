package com.webank.wedatasphere.exchangis.engine.resource.bml;

import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.client.BmlClientFactory;

/**
 * BML client
 */
public class BmlClients {

    private static final BmlClient DEFAULT_CLIENT;
    static{
        //TODO use the common client configuration
        DEFAULT_CLIENT = BmlClientFactory.createBmlClient();
    }

    public static BmlClient getInstance(){
        return DEFAULT_CLIENT;
    }
}
