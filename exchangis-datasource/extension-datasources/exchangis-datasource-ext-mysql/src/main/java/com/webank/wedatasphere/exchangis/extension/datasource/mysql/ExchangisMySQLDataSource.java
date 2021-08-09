package com.webank.wedatasphere.exchangis.extension.datasource.mysql;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.client.DataSourceRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.client.MetaDataRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.FormElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.InputElementUI;

import java.util.ArrayList;
import java.util.List;

public class ExchangisMySQLDataSource implements ExchangisDataSource {
    @Override
    public String type() {
        return "MYSQL";
    }

    @Override
    public List<ElementUI> getDataSourceParamsUI() {

        FormElementUI form = new FormElementUI();

        InputElementUI input1 = new InputElementUI();
        input1.setField("数据源名称");
        input1.setContent("");

        form.getSubElements().add(input1);

        List<ElementUI> uis = new ArrayList<>();
        uis.add(form);
        return uis;
    }

    @Override
    public List<ElementUI> getTransForm() {
        return null;
    }

    @Override
    public DataSourceRemoteClient getDataSourceRemoteClient() {
        return new MySQLDataSourceRemoteClient();
    }

    @Override
    public MetaDataRemoteClient getMetaDataRemoteClient() {
        return null;
    }
}