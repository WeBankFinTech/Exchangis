package com.webank.wedatasphere.exchangis.datasource.core.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * DataSource model
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataSourceModel {

    /**
     * Model id
     */
    private Long id;

    /**
     * Cluster name
     */
    private String clusterName;

    /**
     * Model name (always equal to cluster name)
     */
    @Size(max = 100)
    private String modelName;

    /**
     * Data source type
     */
    private String sourceType;

    /**
     * Model description
     */
    @Size(max = 100)
    private String modelDesc;

    private String parameter;

    private Map<String, Object> parameterMap = new HashMap<>();

    /**
     * Refer to (master) model id
     */
    private Long refModelId;

    /**
     * Is a duplicate model
     */
    private Boolean isDuplicate = false;

    private String createOwner;

    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String modifyUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    /**
     * Version for update
     */
    private Long version = 0L;

    /**
     * resolve parameters
     * @return
     */
    public Map<String, Object> resolveParams(){
        if(null == parameterMap){
            if (StringUtils.isNotBlank(parameter)){
                parameterMap = Json.fromJson(getParameter(), Map.class);
            }else {
                parameterMap = new HashMap<>(4);
            }
        }
        return parameterMap;
    }

    public DataSourceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getModelDesc() {
        return modelDesc;
    }

    public void setModelDesc(String modelDesc) {
        this.modelDesc = modelDesc;
    }

    public String getCreateOwner() {
        return createOwner;
    }

    public void setCreateOwner(String createOwner) {
        this.createOwner = createOwner;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getRefModelId() {
        return refModelId;
    }

    public void setRefModelId(Long refModelId) {
        this.refModelId = refModelId;
    }

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        isDuplicate = duplicate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
