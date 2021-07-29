package com.webank.wedatasphere.exchangis.extension.datasource.hive;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.client.DataSourceRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.client.MetaDataRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.InputElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.OptionElementUI;

import java.util.*;

public class ExchangisHiveDataSource implements ExchangisDataSource {

    private List<ElementUI> dataSourceParamsUI;

    public ExchangisHiveDataSource() {
        init();
    }

    private void init() {
        this.dataSourceParamsUI = new ArrayList<>();
    }

    @Override
    public String type() {
        return "HIVE";
    }

    @Override
    public String description() {
        return "Hive Description";
    }

    @Override
    public String icon() {
        return "icon-hive";
    }

    @Override
    public List<ElementUI> getDataSourceParamsUI() {
        // 总的UI
        List<ElementUI> uis = new ArrayList<>();

        // 传输方式
        Set<String> values = new HashSet<>();
        values.add("Record");
        values.add("二进制");
        OptionElementUI<String> transferUI = new OptionElementUI<>();
        transferUI.setField("transfer_type");
        transferUI.setLabel("传输方式");
        transferUI.setValues(values);
        transferUI.setSelected("Record");
        uis.add(transferUI);

        // 分区信息
        InputElementUI parationUI1 = new InputElementUI();
        parationUI1.setField("ds1");
        uis.add(parationUI1);

        // 空值字符
        InputElementUI blankUI = new InputElementUI();
        blankUI.setField("blank_field");
        uis.add(blankUI);

        return uis;
    }

    @Override
    public List<ElementUI> getTransForm() {
        return null;
    }

    @Override
    public DataSourceRemoteClient getDataSourceRemoteClient() {
        return new HiveDataSourceRemoteClient();
    }

    @Override
    public MetaDataRemoteClient getMetaDataRemoteClient() {
        return null;
    }
}
