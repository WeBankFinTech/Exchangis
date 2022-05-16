package com.webank.wedatasphere.exchangis.datasource.core.ui.viewer;

import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;

import java.util.List;

public interface ExchangisDataSourceUIViewer {
    String getSubJobName();

    ExchangisDataSourceIdsUI getDataSourceIds();

    ExchangisDataSourceParamsUI getParams();

    ExchangisJobTransformsContent getTransforms();

    List<ElementUI<?>> getSettings();
}
