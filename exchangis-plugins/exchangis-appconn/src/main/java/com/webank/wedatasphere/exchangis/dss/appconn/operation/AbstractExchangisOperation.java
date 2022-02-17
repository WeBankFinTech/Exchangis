package com.webank.wedatasphere.exchangis.dss.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.config.ExchangisConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * Abstract implement, contains the method to create sso request
 */
public abstract class AbstractExchangisOperation {

    private SSORequestService ssoRequestService;

    private String uri;

    /**
     * Refer to the url in table 'dss_appconn_instance'
     */
    private String baseURL;

    public AbstractExchangisOperation(){

    }

    public AbstractExchangisOperation(String[] uriParts){
        if (Objects.nonNull(uriParts)){
            uri = StringUtils.join(uriParts, IOUtils.DIR_SEPARATOR_UNIX);
        }
    }
    public AbstractExchangisOperation(AppIntegrationService<SSORequestService> appIntegrationService){
        setSSORequestService(appIntegrationService);
    }

    /**
     * Set sso request service
     * @param appIntegrationService integrated sso request service
     */
    protected void setSSORequestService(AppIntegrationService<SSORequestService> appIntegrationService){
        if (Objects.nonNull(appIntegrationService)){
            this.ssoRequestService = appIntegrationService.getSSORequestService();
            // Also upgrade the base url
            this.baseURL = appIntegrationService.getAppInstance().getBaseUrl();
        }
    }

    @SuppressWarnings("unchecked")
    protected <T, R>SSORequestOperation<T, R> getOrCreateSSORequestOperation(String name) throws ExternalOperationFailedException {
        if (Objects.nonNull(this.ssoRequestService)){
            return ssoRequestService.createSSORequestOperation(name);
        }
        throw new ExternalOperationFailedException(-1, "The ssoRequestService is empty in operation: ["
                + this.getClass().getSimpleName() + "]");
    }

    /**
     * AppConn name
     * @return name value
     */
    protected String getAppName() {
        return ExchangisConfig.EXCHANGIS_APPCONN_NAME;
    }

    /**
     * Get request url of operation
     * @return url value
     */
    public String requestURL(){
        return baseURL.endsWith(IOUtils.DIR_SEPARATOR_UNIX + "") ?
                baseURL + uri : baseURL + IOUtils.DIR_SEPARATOR_UNIX + uri;
    }
}
