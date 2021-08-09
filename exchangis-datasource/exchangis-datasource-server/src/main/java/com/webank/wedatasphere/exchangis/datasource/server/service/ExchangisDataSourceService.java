package com.webank.wedatasphere.exchangis.datasource.server.service;
//
//import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
//import com.webank.wedatasphere.exchangis.datasource.core.context.DefaultExchangisDataSourceContext;
//import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
//import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.DefaultDataSourceUIViewer;
//import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ExchangisDataSourceService implements DataSourceUIGetter, DataSourceServiceDispatcher, MetadataServiceDispatcher {
//
//    private DefaultExchangisDataSourceContext context;
//
//    @Override
//    public ExchangisDataSourceUIViewer getDataSourceUI(String type) {
//
//        DefaultExchangisDataSourceContext context = new DefaultExchangisDataSourceContext();
//
//        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
//
//        List<ElementUI> dataSourceParamsUI = exchangisDataSource.getDataSourceParamsUI();
//
//        ExchangisDataSourceUIViewer viewer = new DefaultDataSourceUIViewer();
//
////        ExchangisDataSource exchangisDataSource1 = context.getExchangisDataSource(type);
//
////        exchangisDataSource.getDataSourceRemoteClient().
//
//        return viewer;
//    }
//
//}
