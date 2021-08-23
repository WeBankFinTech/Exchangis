package com.webank.wedatasphere.exchangis.appconn;

import com.webank.wedatasphere.dss.appconn.core.ext.AlmightyAppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.exchangis.appconn.standard.ExchangisDevelopmentIntergrationStandard;
import com.webank.wedatasphere.exchangis.appconn.standard.ExchangisStructureIntegrationStandard;

import java.util.ArrayList;
import java.util.List;

public class ExchangisAppConn implements AlmightyAppConn {
    private final List<AppStandard> standards = new ArrayList<>();
    private AppDesc appDesc;

    public ExchangisAppConn() {
        init();
    }

    private void init() {
        this.standards.add(new ExchangisDevelopmentIntergrationStandard(this));
        this.standards.add(ExchangisStructureIntegrationStandard.getInstance(this));
    }

    public List<AppStandard> getAppStandards() {
        return this.standards;
    }

    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }
}