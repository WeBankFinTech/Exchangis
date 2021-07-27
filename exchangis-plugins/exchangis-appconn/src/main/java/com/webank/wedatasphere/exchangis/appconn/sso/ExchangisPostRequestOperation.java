package com.webank.wedatasphere.exchangis.appconn.sso;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangisPostRequestOperation implements SSORequestOperation<ExchangisHttpPost, CloseableHttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisPostRequestOperation.class);

    private ExchangisSecurityService exampleSecurityService;

    private final Object LOCK = new Object();

    private CloseableHttpClient httpClient;

    public ExchangisPostRequestOperation(String baseUrl) {
        this.exampleSecurityService = ExchangisSecurityService.getInstance(baseUrl);
    }

    public CloseableHttpResponse requestWithSSO(SSOUrlBuilderOperation urlBuilder, ExchangisHttpPost req) throws AppStandardErrorException {
        try {
            System.out.println("in requestWithSSO() method req.getUser() => " + req.getUser() + " , exampleSecurityService => " + this.exampleSecurityService);
            System.out.println("console exampleSecurityService");
            System.out.println(this.exampleSecurityService);
            System.out.println("after console exampleSecurityService");
            this.httpClient = HttpClients.custom().build();
            return this.httpClient.execute(req);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            throw new AppStandardErrorException(90009, t.getMessage(), t);
        } finally {}
    }

    public CloseableHttpResponse requestWithSSO(String url, ExchangisHttpPost req) throws AppStandardErrorException {
        return null;
    }
}
