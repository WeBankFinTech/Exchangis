package com.webank.wedatasphere.exchangis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.appconn.ExchangisConf;
import com.webank.wedatasphere.exchangis.appconn.ExchangisExceptionUtils;
import com.webank.wedatasphere.exchangis.appconn.ref.ExchangisProjectResponseRef;
import com.webank.wedatasphere.exchangis.appconn.service.ExchangisProjectService;
import com.webank.wedatasphere.exchangis.appconn.sso.ExchangisHttpPost;
import com.webank.wedatasphere.exchangis.appconn.sso.ExchangisPostRequestOperation;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExchangisProjectCreationOperation implements ProjectCreationOperation, ExchangisConf {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisProjectCreationOperation.class);

    private ExchangisProjectService exchangisProjectService;

    private SSOUrlBuilderOperationImpl ssoUrlBuilderOperation;

    private SSORequestOperation<ExchangisHttpPost, CloseableHttpResponse> postOperation;

    private String projectUrl;

    public ExchangisProjectCreationOperation(ExchangisProjectService exchangisProjectService) {
        this.exchangisProjectService = exchangisProjectService;
        init();
    }

    private void init() {
        this.ssoUrlBuilderOperation = new SSOUrlBuilderOperationImpl();
        this.ssoUrlBuilderOperation.setAppName(this.exchangisProjectService.getAppDesc().getAppName());
        this.postOperation = new ExchangisPostRequestOperation(this.exchangisProjectService.getAppInstance().getBaseUrl());
        this.projectUrl = this.exchangisProjectService.getAppInstance().getBaseUrl().endsWith("/") ? (this.exchangisProjectService.getAppInstance().getBaseUrl() + "exchangis/createProject") : (this.exchangisProjectService.getAppInstance().getBaseUrl() + "/exchangis/createProject");
    }

    public void setStructureService(StructureService service) {
        if (service instanceof ExchangisProjectService)
            this.exchangisProjectService = (ExchangisProjectService)service;
    }

    public ProjectResponseRef createProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        System.out.println("example project create operation .....");
        System.out.println(requestRef.getWorkspaceName());
        System.out.println(requestRef.getCreateBy());
        System.out.println(requestRef.getDescription());
        System.out.println(requestRef.getType());
        System.out.println(requestRef.getName());
        System.out.println(requestRef.getId());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
        ExchangisProjectResponseRef responseRef = new ExchangisProjectResponseRef();
        System.out.println(this.projectUrl);
        System.out.println(this.ssoUrlBuilderOperation.getDssUrl());
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("workspaceName", requestRef.getWorkspaceName()));
        params.add(new BasicNameValuePair("name", requestRef.getName()));
        params.add(new BasicNameValuePair("createdBy", requestRef.getCreateBy()));
        HttpEntity entity = EntityBuilder.create().setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)).setParameters(params).build();
        System.out.println("before new ExampleHttpPost()");
        ExchangisHttpPost httpPost = new ExchangisHttpPost(this.projectUrl, requestRef.getCreateBy());
        httpPost.setEntity(entity);
        CloseableHttpResponse httpResponse = null;
        try {
            System.out.println("before this.postOperation.requestWithSSO()");
            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entStr = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            LOGGER.error("{}, exchangis {}", requestRef.getName(), entStr);
            responseRef.setProjectRefId(0L);
        } catch (Throwable t) {
            ExchangisExceptionUtils.dealErrorException(60051, "failed to create project in example", t, ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
        return responseRef;
    }
}
