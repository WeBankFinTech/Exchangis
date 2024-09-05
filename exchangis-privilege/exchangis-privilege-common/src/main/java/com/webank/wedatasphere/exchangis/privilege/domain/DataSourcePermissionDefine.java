package com.webank.wedatasphere.exchangis.privilege.domain;

import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceItem;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceService;
import com.webank.wedatasphere.exchangis.datasource.core.vo.DataSourceQueryVo;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataSourcePermissionDefine extends AbstractPermissionInfo<DataSourceService> {

    public DataSourcePermissionDefine() {
    }

    @Override
    public void setIdentify(ApplicationContext context, String username) {
        this.service = context.getBean(DataSourceService.class);
        DataSourceQueryVo dataSourceQuery = new DataSourceQueryVo();
        dataSourceQuery.setCreateUser(username);
        List<ExchangisDataSourceItem> dataSources = new ArrayList<>();
        try {
            dataSources = getService().listDataSourcesByUser(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.identify = dataSources.stream().collect(Collectors.toMap(ExchangisDataSourceItem::getId, ExchangisDataSourceItem::getName));
    }
}