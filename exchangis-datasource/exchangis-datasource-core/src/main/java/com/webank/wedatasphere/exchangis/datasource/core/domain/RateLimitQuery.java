package com.webank.wedatasphere.exchangis.datasource.core.domain;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;

public class RateLimitQuery extends PageQuery {

    private Long id;

    private Long limitRealmId;

    private String limitRealm;

    private String sourceType;

    private Long flowRateLimit;

    private String flowRateLimitUnit;

    private Long recordRateLimit;

    private Long parallelLimit;

    private Boolean openLimit;

    private String userRole;

    private String createUser;

    private String modifyUser;

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

    public Long getRecordRateLimit() {
        return recordRateLimit;
    }

    public void setRecordRateLimit(Long recordRateLimit) {
        this.recordRateLimit = recordRateLimit;
    }

    public Long getParallelLimit() {
        return parallelLimit;
    }

    public void setParallelLimit(Long parallelLimit) {
        this.parallelLimit = parallelLimit;
    }

    public Boolean getOpenLimit() {
        return openLimit;
    }

    public void setOpenLimit(Boolean openLimit) {
        this.openLimit = openLimit;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
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

    public RateLimitQuery() {
    }

    public RateLimitQuery(RateLimit rateLimit) {
        this.id = rateLimit.getId();
        this.limitRealmId = rateLimit.getLimitRealmId();
        this.limitRealm = rateLimit.getLimitRealm();
        this.flowRateLimit = rateLimit.getFlowRateLimit();
        this.flowRateLimitUnit = rateLimit.getFlowRateLimitUnit();
        this.recordRateLimit = rateLimit.getRecordRateLimit();
        this.parallelLimit = rateLimit.getParallelLimit();
        this.openLimit = rateLimit.getOpenLimit();
        this.createUser = rateLimit.getCreateUser();
        this.modifyUser = rateLimit.getModifyUser();
    }
}
