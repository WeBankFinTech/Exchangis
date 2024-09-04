package com.webank.wedatasphere.exchangis.datasource.core.domain;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DataSourceModelQuery extends PageQuery {
    private long id;
    private long modelId;
    private String clusterName;
    private String modelName;
    /**
     * For exact query
     */
    private String modelExactName;
    private String sourceType;
    private Date createTimeBegin;
    private Date createTimeEnd;
    private String createUser;
    private String createOwner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
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

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getCreateUser() {
        if(StringUtils.isNotBlank(createOwner)){
            return null;
        }
        return createUser;
    }

    public String getCreateOwner() {
        return createOwner;
    }

    public void setCreateOwner(String createOwner) {
        this.createOwner = createOwner;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModelExactName() {
        return modelExactName;
    }

    public void setModelExactName(String modelExactName) {
        this.modelExactName = modelExactName;
    }

}
