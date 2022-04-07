package com.webank.wedatasphere.exchangis.dss.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.ref.ExchangisRefExecutionOperation;
import com.webank.wedatasphere.exchangis.dss.appconn.request.action.HttpExtAction;
import com.webank.wedatasphere.exchangis.dss.appconn.response.result.ExchangisEntityRespResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.request.UserAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;

/**
 * Abstract implement, contains the method to create sso request
 */
public abstract class AbstractExchangisOperation {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractExchangisOperation.class);

    private SSORequestService ssoRequestService;

    private String uri;

    /**
     * Refer to the url in table 'dss_appconn_instance'
     */
    private String baseURL;

    private String redirectUrl;

    public AbstractExchangisOperation(){

    }

    protected abstract Logger getLogger();

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
            // Append the api prefix
            this.baseURL = this.baseURL.endsWith(IOUtils.DIR_SEPARATOR_UNIX + "")?
                    baseURL + API_REQUEST_PREFIX: baseURL + IOUtils.DIR_SEPARATOR_UNIX + API_REQUEST_PREFIX;
            this.redirectUrl = String.valueOf(appIntegrationService.getAppInstance().getConfig().get("redirectUrl"));
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

    protected  <T, R>SSORequestOperation<T, R> getOrCreateSSORequestOperation() throws ExternalOperationFailedException{
        return getOrCreateSSORequestOperation(getAppName());
    }

    /**
     * Get sso url builder operation
     * @param workspace work space
     * @param appName app name
     * @param url url
     * @return
     */
    protected SSOUrlBuilderOperation getSSOUrlBuilderOperation(Workspace workspace,
                                                               String appName, String url){
        LOG.info("requestURL555555: {}", requestURL());
        return workspace.getSSOUrlBuilderOperation().copy().setAppName(appName).setReqUrl(url)
                .setWorkspace(workspace.getWorkspaceName());
    }

    protected SSOUrlBuilderOperation getSSOUrlBuilderOperation(Workspace workspace){
        return getSSOUrlBuilderOperation(workspace, getAppName(), requestURL());
    }

    /**
     * AppConn name
     * @return name value
     */
    protected String getAppName() {
        return Constraints.EXCHANGIS_APPCONN_NAME;
    }

    /**
     * Get request url of operation
     * @return url value
     */
    public String requestURL(){
        return requestURL(uri);
    }

    public String requestURL(String customUri){
        return baseURL.endsWith(IOUtils.DIR_SEPARATOR_UNIX + "") ?
                baseURL + customUri : baseURL + IOUtils.DIR_SEPARATOR_UNIX + customUri;
    }

    public String pageUrl(String customUri){
        return redirectUrl.endsWith(IOUtils.DIR_SEPARATOR_UNIX + "") ?
                redirectUrl + customUri : redirectUrl + IOUtils.DIR_SEPARATOR_UNIX + customUri;
    }

    /**
     * Send request and get response entity
     * @param url url
     * @param workspace workspace
     * @param requestRef request ref
     * @param httpActionBuilder action builder
     * @param entityClass response entity class
     * @param entityParameters parametric types of response entity class
     * @param <R> response entity
     * @param <T> request ref
     * @return
     * @throws ExternalOperationFailedException
     */
    protected <R, T extends RequestRef>ExchangisEntityRespResult.BasicMessageEntity<R> requestToGetEntity(String url,
                                                                                                          Workspace workspace,
                                                                                                          T requestRef, HttpActionBuilder<T> httpActionBuilder, Class<?> entityClass, Class<?>... entityParameters) throws ExternalOperationFailedException {
        LOG.info("Create job url{}: ", url);
        HttpExtAction action = httpActionBuilder.build(requestRef);
        LOG.info("Action123456{}: ", action.getRequestBody());
        if (Objects.nonNull(action)){
            SSOUrlBuilderOperation ssoUrlBuilderOperation = getSSOUrlBuilderOperation(workspace, getAppName(), url);
            ExchangisEntityRespResult.BasicMessageEntity<R> entity;
            try {
                LOG.info("ssoUrlBuilderOperation666: {}", ssoUrlBuilderOperation.getBuiltUrl());
                action.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
                SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = getOrCreateSSORequestOperation();
                ExchangisEntityRespResult httpResult = new ExchangisEntityRespResult(ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, action));
                entity = httpResult.getEntity(entityClass, entityParameters);
                return entity;
            } catch (Exception e){
                if (e instanceof ExternalOperationFailedException){
                    getLogger().error("Response process exception: url: [" + url + "], message: [" + e.getMessage(), e);
                    throw (ExternalOperationFailedException)e;
                }
                getLogger().error("Request to url: [" + url + "] exception", e);
                throw new ExternalOperationFailedException(-1, e.getMessage());
            }
        }

        return null;
    }

    protected <R, T extends RequestRef>ExchangisEntityRespResult.BasicMessageEntity<R> requestToGetEntity(Workspace workspace,
                                                                                                          T requestRef, HttpActionBuilder<T> httpActionBuilder, Class<?> entityClass, Class<?>... entityParameters) throws ExternalOperationFailedException {
        return requestToGetEntity(requestURL(), workspace, requestRef, httpActionBuilder, entityClass, entityParameters);
    }
    @FunctionalInterface
    public interface HttpActionBuilder<T>{

        /**
         * Build main entrance
         * @return http action
         */
        HttpExtAction build(T input) throws ExternalOperationFailedException;
    }

}
