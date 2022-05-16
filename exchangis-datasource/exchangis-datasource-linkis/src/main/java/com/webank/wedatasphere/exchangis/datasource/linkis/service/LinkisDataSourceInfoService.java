/*
         Copyright 2022 WeBank

         Licensed under the Apache License, Version 2.0 (the "License");
          you may not use this file except in compliance with the License.
         You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

         Unless required by applicable law or agreed to in writing, software
         distributed under the License is distributed on an "AS IS" BASIS,
         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         See the License for the specific language governing permissions and
         limitations under the License.
        */


package com.webank.wedatasphere.exchangis.datasource.linkis.service;

import com.webank.wedatasphere.exchangis.datasource.core.service.DataSourceInfoService;
import com.webank.wedatasphere.exchangis.datasource.core.service.rpc.ServiceRpcClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.service.rpc.LinkisDataSourceServiceRpcDispatcher;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;

public class LinkisDataSourceInfoService extends LinkisDataSourceServiceRpcDispatcher<LinkisDataSourceRemoteClient> implements DataSourceInfoService {
    @Override
    public ServiceRpcClient<LinkisDataSourceRemoteClient> getDefaultRemoteClient() {
        return ExchangisLinkisRemoteClient::getLinkisDataSourceRemoteClient;
    }

}
