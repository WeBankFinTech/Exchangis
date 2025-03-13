package com.webank.wedatasphere.exchangis.datasource.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RateLimitVo {

    public static final String DEFAULT_LIMIT_REALM = "MODEL";

    public static final String DEFAULT_FLOW_RATE_LIMIT_UNIT = "MB";

    public static final Boolean DEFAULT_OPEN_LIMIT = false;

    private Long id;

    private Long limitRealmId;

    /**
     * Default: MODEL
     */
    private String limitRealm;

    private String modelName;

    private String sourceType;

    private Long flowRateLimit;

    private String flowRateLimitUnit;

    private Long flowRateLimitUsed;

    private Long recordRateLimit;

    private Long recordRateLimitUsed;

    private Long parallelLimit;

    private Long parallelLimitUsed;

    /**
     * Open limit
     * 1 with open; 0 with close
     */
    private Boolean openLimit;

    /**
     * Create user
     */
    private String createUser;

    /**
     * Create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * Modify user
     */
    private String modifyUser;

    /**
     * Modify time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLimitRealmId() {
        return limitRealmId;
    }

    public void setLimitRealmId(Long limitRealmId) {
        this.limitRealmId = limitRealmId;
    }

    public String getLimitRealm() {
        return limitRealm;
    }

    public void setLimitRealm(String limitRealm) {
        this.limitRealm = limitRealm;
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

    public Long getFlowRateLimit() {
        return flowRateLimit;
    }

    public void setFlowRateLimit(Long flowRateLimit) {
        this.flowRateLimit = flowRateLimit;
    }

    public String getFlowRateLimitUnit() {
        return flowRateLimitUnit;
    }

    public void setFlowRateLimitUnit(String flowRateLimitUnit) {
        this.flowRateLimitUnit = flowRateLimitUnit;
    }

    public Long getFlowRateLimitUsed() {
        return flowRateLimitUsed;
    }

    public void setFlowRateLimitUsed(Long flowRateLimitUsed) {
        this.flowRateLimitUsed = flowRateLimitUsed;
    }

    public Long getRecordRateLimit() {
        return recordRateLimit;
    }

    public void setRecordRateLimit(Long recordRateLimit) {
        this.recordRateLimit = recordRateLimit;
    }

    public Long getRecordRateLimitUsed() {
        return recordRateLimitUsed;
    }

    public void setRecordRateLimitUsed(Long recordRateLimitUsed) {
        this.recordRateLimitUsed = recordRateLimitUsed;
    }

    public Long getParallelLimit() {
        return parallelLimit;
    }

    public void setParallelLimit(Long parallelLimit) {
        this.parallelLimit = parallelLimit;
    }

    public Long getParallelLimitUsed() {
        return parallelLimitUsed;
    }

    public void setParallelLimitUsed(Long parallelLimitUsed) {
        this.parallelLimitUsed = parallelLimitUsed;
    }

    public Boolean getOpenLimit() {
        return openLimit;
    }

    public void setOpenLimit(Boolean openLimit) {
        this.openLimit = openLimit;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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
}
