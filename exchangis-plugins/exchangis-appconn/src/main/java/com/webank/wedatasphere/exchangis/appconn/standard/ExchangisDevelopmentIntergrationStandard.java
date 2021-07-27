package com.webank.wedatasphere.exchangis.appconn.standard;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.exchangis.appconn.ref.service.ExchangisCRUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ExchangisDevelopmentIntergrationStandard extends AbstractLabelDevelopmentIntegrationStandard {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisDevelopmentIntergrationStandard.class);

    private AppConn appConn;

    public ExchangisDevelopmentIntergrationStandard(AppConn appConn) {
        try {
            this.appConn = appConn;
            init();
        } catch (AppStandardErrorException e) {
            LOGGER.error("Failed to init {}", this.getClass().getSimpleName(), e);
        }
    }

    // 这里返回的 RefOperationService 列表，最终会在 DevProcessService、TestProcessService、ProdProcessService 中持有。
    @Override
    protected List<RefOperationService> getRefOperationService() {
        return Lists.newArrayList(
                new ExchangisCRUDService()
        );
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        // do nothing
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }
}
