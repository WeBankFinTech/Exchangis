package com.webank.wedatasphere.exchangis.datasource.core.domain;


import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * DataSource model
 */
public class DataSourceModel {

    private Long id;

    private String clusterName;

    private String modelName;

    private String sourceType;

    private String modelDesc;

    private String createOwner;

    private String parameter;

    private Map<String, Object> parameterMap;

    private Date createTime;

    private String modifyUser;

    private Date updateTime;

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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
