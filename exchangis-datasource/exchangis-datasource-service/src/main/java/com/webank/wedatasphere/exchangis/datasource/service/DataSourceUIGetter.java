package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DataSourceUIGetter {

    // 根据已经创建的 Job 来获取该 Job DataSource 的配置信息
    List<ExchangisDataSourceUIViewer> getJobDataSourceUIs(HttpServletRequest request, Long jobId);

    // 在新建 Job 的时候，右侧需要根据选择的数据源类型来获取参数设置项
    List<ElementUI<?>> getDataSourceParamsUI(String dsType, String dir);

    // 在新建 Job 的时候，右侧需要根据选择的引擎类型来获取参数设置项
    List<ElementUI<?>> getJobEngineSettingsUI(String engineType);

}
