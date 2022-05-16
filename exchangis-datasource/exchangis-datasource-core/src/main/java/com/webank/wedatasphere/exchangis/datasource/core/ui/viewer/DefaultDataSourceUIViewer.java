package com.webank.wedatasphere.exchangis.datasource.core.ui.viewer;

import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;

import java.util.List;

public class DefaultDataSourceUIViewer implements ExchangisDataSourceUIViewer {
    private String subJobName;
    private final ExchangisDataSourceIdsUI dataSourceIds;
    private final ExchangisDataSourceParamsUI params;
//    private final ExchangisDataSourceTransformsUI transforms;
    private final ExchangisJobTransformsContent transforms;
    private final List<ElementUI<?>> settings;

    public DefaultDataSourceUIViewer(String subJobName, ExchangisDataSourceIdsUI dataSourceIds, ExchangisDataSourceParamsUI params, ExchangisJobTransformsContent transforms, List<ElementUI<?>> settings) {
        this.subJobName = subJobName;
        this.dataSourceIds = dataSourceIds;
        this.params = params;
        this.transforms = transforms;
        this.settings = settings;
    }

    @Override
    public String getSubJobName() {
        return subJobName;
    }

    public void setSubJobName(String subJobName) {
        this.subJobName = subJobName;
    }

    @Override
    public ExchangisDataSourceIdsUI getDataSourceIds() {
        return this.dataSourceIds;
    }

    @Override
    public ExchangisDataSourceParamsUI getParams() {
        return this.params;
    }

    @Override
    public ExchangisJobTransformsContent getTransforms() {
        return this.transforms;
    }

    @Override
    public List<ElementUI<?>> getSettings() {
        return this.settings;
    }
}
