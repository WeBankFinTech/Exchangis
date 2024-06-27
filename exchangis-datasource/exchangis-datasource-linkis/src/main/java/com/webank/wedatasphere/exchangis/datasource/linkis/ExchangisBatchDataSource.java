package com.webank.wedatasphere.exchangis.datasource.linkis;

import com.webank.wedatasphere.exchangis.datasource.core.AbstractExchangisDataSourceDefinition;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;

/**
 * Batch data source
 */
public abstract class ExchangisBatchDataSource extends AbstractExchangisDataSourceDefinition {


    @Override
    public LinkisDataSourceRemoteClient getDataSourceRemoteClient() {
        return ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
    }

    @Override
    public LinkisMetaDataRemoteClient getMetaDataRemoteClient() {
        return ExchangisLinkisRemoteClient.getLinkisMetadataRemoteClient();
    }

}
