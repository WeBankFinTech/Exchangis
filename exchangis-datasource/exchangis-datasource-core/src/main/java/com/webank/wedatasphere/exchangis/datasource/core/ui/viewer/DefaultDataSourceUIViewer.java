package com.webank.wedatasphere.exchangis.datasource.core.ui.viewer;

import com.webank.wedatasphere.exchangis.datasource.core.ui.*;

import java.util.List;

public class DefaultDataSourceUIViewer implements ExchangisDataSourceUIViewer {

    private final ExchangisDataSourceIdsUI dataSourceIds;
    private final ExchangisDataSourceParamsUI params;
    private final ExchangisDataSourceTransformsUI transforms;
    private final List<ElementUI> settings;

    public DefaultDataSourceUIViewer(ExchangisDataSourceIdsUI dataSourceIds, ExchangisDataSourceParamsUI params, ExchangisDataSourceTransformsUI transforms, List<ElementUI> settings) {
        this.dataSourceIds = dataSourceIds;
        this.params = params;
        this.transforms = transforms;
        this.settings = settings;
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
    public ExchangisDataSourceTransformsUI getTransforms() {
        return this.transforms;
    }

    @Override
    public List<ElementUI> getSettings() {
        return this.settings;
    }
}
