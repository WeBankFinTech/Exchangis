package com.webank.wedatasphere.exchangis.datasource.core.ui.viewer;

import com.webank.wedatasphere.exchangis.datasource.core.ui.*;

import java.util.List;

public interface ExchangisDataSourceUIViewer {
    ExchangisDataSourceIdsUI getDataSourceIds();

    ExchangisDataSourceParamsUI getParams();

    ExchangisDataSourceTransformsUI getTransforms();

    List<ElementUI> getSettings();
}
