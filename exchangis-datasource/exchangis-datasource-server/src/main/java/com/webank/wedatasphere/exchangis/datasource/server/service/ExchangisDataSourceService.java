package com.webank.wedatasphere.exchangis.datasource.server.service;

import com.webank.wedatasphere.exchangis.datasource.LinkisDataSourceClient;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.DefaultExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.DefaultDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.server.dto.ExchangisDataSourceDTO;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;
import com.webank.wedatasphere.linkis.httpclient.config.ClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ExchangisDataSourceService implements DataSourceUIGetter, DataSourceServiceDispatcher, MetadataServiceDispatcher {

    private final ExchangisDataSourceContext context;

    @Autowired
    public ExchangisDataSourceService(ExchangisDataSourceContext context) {
        this.context = context;
    }

    @Override
    public ExchangisDataSourceUIViewer getDataSourceUI(String type) {

//        DefaultExchangisDataSourceContext context = new DefaultExchangisDataSourceContext();

        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);

        List<ElementUI> dataSourceParamsUI = exchangisDataSource.getDataSourceParamsUI();

        ExchangisDataSourceUIViewer viewer = new DefaultDataSourceUIViewer();

//        ExchangisDataSource exchangisDataSource1 = context.getExchangisDataSource(type);

//        exchangisDataSource.getDataSourceRemoteClient().

        return viewer;
    }

    /**
     * 根据 LocalExchangisDataSourceLoader 加载到的本地的数据源与 Linkis 支持的数据源
     * 做比较，筛选出可以给前端展示的数据源类型
     */
    public Message listDataSources(HttpServletRequest request) {
        Collection<ExchangisDataSource> all = this.context.all();
        List<ExchangisDataSourceDTO> dtos = new ArrayList<>();
        for (ExchangisDataSource item : all) {
            dtos.add(new ExchangisDataSourceDTO(
                    item.type(),
                    item.description(),
                    item.icon()
            ));
        }
//        String userName = SecurityFilter.getLoginUsername(request);
        String userName = "hdfs";
        List<DataSourceType> dataSourceTypes = LinkisDataSourceClient.queryDataSourceTypes(userName);

        System.out.println(dataSourceTypes);

        return Message.ok().data("list", dtos);
    }

    public Message create(HttpServletRequest request, Map<String, Object> json) {
//        DWSClientConfig clientConfig = DWSClientConfigBuilder.newBuilder()
//                .addServerUrl("http://124.70.31.149/")
//                .connectionTimeout(5000)
//                .discoveryEnabled(true)
//                .discoveryFrequency(1, TimeUnit.MINUTES)
//                .loadbalancerEnabled(true)
//                .maxConnectionSize(30)
//                .retryEnabled(true)
//                .readTimeout(5000)
//                .setAuthenticationStrategy(new StaticAuthenticationStrategy())
//                .setAuthTokenKey("")
//                .setAuthTokenValue("")
//                .build();

//        LinkisDataSourceRemoteClient dsRemoteClient = new LinkisDataSourceRemoteClient(clientConfig);

        return null;
    }

    public Message getParamsUI(HttpServletRequest request, String type) {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        List<ElementUI> dataSourceParamsUI = exchangisDataSource.getDataSourceParamsUI();
        return Message.ok().data("list", dataSourceParamsUI);
    }
}
