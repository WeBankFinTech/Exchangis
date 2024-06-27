package com.webank.wedatasphere.exchangis.datasource.streamis;

import com.webank.wedatasphere.exchangis.datasource.core.AbstractExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;

/**
 * Exchangis streamis data source
 */
public abstract class ExchangisStreamisDataSource extends AbstractExchangisDataSourceDefinition {

    @Override
    public LinkisDataSourceRemoteClient getDataSourceRemoteClient() {
        return ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
    }

    @Override
    public LinkisMetaDataRemoteClient getMetaDataRemoteClient() {
        return ExchangisLinkisRemoteClient.getLinkisMetadataRemoteClient();
    }
}
