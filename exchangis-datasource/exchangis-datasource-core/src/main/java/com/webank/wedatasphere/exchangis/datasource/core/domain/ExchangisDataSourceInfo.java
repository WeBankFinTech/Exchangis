package com.webank.wedatasphere.exchangis.datasource.core.domain;

import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;

import java.util.Date;
import java.util.Map;

public class ExchangisDataSourceInfo {

    private String id;

    private String type;

    private Map<String, Object> connectParams;

    private String createUser;

    private String modifyUser;

    private Date createTime;

    private Date modifyTime;

    private ExchangisDataSourceUIViewer uiViewer;

}
