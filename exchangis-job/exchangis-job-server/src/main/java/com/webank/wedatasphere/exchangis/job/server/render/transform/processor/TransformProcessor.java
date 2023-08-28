package com.webank.wedatasphere.exchangis.job.server.render.transform.processor;

import com.webank.wedatasphere.exchangis.common.linkis.bml.BmlResource;

import java.util.Objects;

/**
 * Processor entity
 */
public class TransformProcessor {
    /**
     * Id
     */
    private Long id;

    /**
     * Job id
     */
    private Long jobId;

    /**
     * Code content
     */
    private String codeContent;

    /**
     * Code language
     */
    private String codeLanguage = "java";

    /**
     * Bml resourceId
     */
    private String codeBmlResourceId;

    /**
     * Bml version
     */
    private String codeBmlVersion;

    /**
     * Bml resource
     */
    private BmlResource bmlResource;

    /**
     * Creator
     */
    private String creator;
    public TransformProcessor(){

    }

    public TransformProcessor(ProcessorRequestVo requestVo){
        this.jobId = Long.valueOf(requestVo.getJobId());
        this.codeContent = requestVo.getCode();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }

    public BmlResource getBmlResource() {
        if (Objects.isNull(bmlResource) && Objects.nonNull(this.codeBmlResourceId)
                && Objects.nonNull(this.codeBmlVersion)){
            this.bmlResource = new BmlResource(this.codeBmlResourceId, this.codeBmlVersion);
        }
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
        if (Objects.nonNull(bmlResource)){
            this.codeBmlResourceId = bmlResource.getResourceId();
            this.codeBmlVersion = bmlResource.getVersion();
        }
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCodeBmlResourceId() {
        return codeBmlResourceId;
    }

    public void setCodeBmlResourceId(String codeBmlResourceId) {
        this.codeBmlResourceId = codeBmlResourceId;
    }

    public String getCodeBmlVersion() {
        return codeBmlVersion;
    }

    public void setCodeBmlVersion(String codeBmlVersion) {
        this.codeBmlVersion = codeBmlVersion;
    }
}
