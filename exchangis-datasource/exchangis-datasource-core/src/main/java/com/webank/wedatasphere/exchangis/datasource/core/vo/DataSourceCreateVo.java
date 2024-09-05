package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Map;

/**
 * Use for updating or creating data source
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceCreateVo {
    @Size(min=0,max=100,message="Length of dataSource name should between 0 and 100(数据源名字的长度应该在0和100之间)")
    private String dataSourceName;

    private Long dataSourceTypeId;

    @Size(min=0,max=200,message="Length of dataSource description should between 0 and 200(数据源描述的长度应该在0和200之间)")
    private String dataSourceDesc;

    private Long dsModelId;

    private String createIdentify;

    private String createSystem;

    private String createUser;

    private String modifyUser;

    private Date createTime;

    private Date modifyTime;

    private Long versionId;

    @Size(min=0,max=200,message="Length of labels should between 0 and 200(标签的长度应该在0和200之间)")
    private String label;

    private Map<String, Object> labels;

    private Long publishedVersionId;

    private Boolean expire = false;

    private String comment;

    private Map<String, Object> connectParams;

    /**
     * Model id
     */
    private Long modelId;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Long getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Long dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    public Long getDsModelId() {
        return dsModelId;
    }

    public void setDsModelId(Long dsModelId) {
        this.dsModelId = dsModelId;
    }

    public String getCreateIdentify() {
        return createIdentify;
    }

    public void setCreateIdentify(String createIdentify) {
        this.createIdentify = createIdentify;
    }

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getPublishedVersionId() {
        return publishedVersionId;
    }

    public void setPublishedVersionId(Long publishedVersionId) {
        this.publishedVersionId = publishedVersionId;
    }

    public Boolean getExpire() {
        return expire;
    }

    public void setExpire(Boolean expire) {
        this.expire = expire;
    }



    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
}
