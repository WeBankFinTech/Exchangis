package com.webank.wedatasphere.exchangis.datasource.core.ui.viewer;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;

import java.util.List;

public interface ExchangisDataSourceUIViewer {
    List<ElementUI> getDataSourceParamsUI();

    List<ElementUI> getTransformUI();
}
