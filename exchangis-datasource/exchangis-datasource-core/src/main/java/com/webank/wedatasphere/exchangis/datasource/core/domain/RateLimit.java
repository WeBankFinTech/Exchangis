package com.webank.wedatasphere.exchangis.datasource.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RateLimit {

    public static final String DEFAULT_LIMIT_REALM = "MODEL";

    public static final String DEFAULT_FLOW_RATE_LIMIT_UNIT = "MB";

    public static final Boolean DEFAULT_OPEN_LIMIT = false;

    private Long id;

    private Long limitRealmId;

    /**
     * Default: MODEL
     */
    private String limitRealm;

    @NotNull(message = "速率限速值不为空")
    @Min(0)
    @Max(value = Integer.MAX_VALUE, message = "Value must be less than or equal to " + Integer.MAX_VALUE)
    private Integer flowRateLimit;

    private String flowRateLimitUnit;

    @NotNull(message = "记录数限速值不为空")
    @Min(0)
    @Max(value = Integer.MAX_VALUE, message = "Value must be less than or equal to " + Integer.MAX_VALUE)
    private Integer recordRateLimit;

    @NotNull(message = "并行度限速值不为空")
    @Min(0)
    @Max(value = Integer.MAX_VALUE, message = "Value must be less than or equal to " + Integer.MAX_VALUE)
    private Integer parallelLimit;

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

    public Integer getFlowRateLimit() {
        return flowRateLimit;
    }

    public void setFlowRateLimit(Integer flowRateLimit) {
        this.flowRateLimit = flowRateLimit;
    }

    public String getFlowRateLimitUnit() {
        return flowRateLimitUnit;
    }

    public void setFlowRateLimitUnit(String flowRateLimitUnit) {
        this.flowRateLimitUnit = flowRateLimitUnit;
    }

    public Integer getRecordRateLimit() {
        return recordRateLimit;
    }

    public void setRecordRateLimit(Integer recordRateLimit) {
        this.recordRateLimit = recordRateLimit;
    }

    public Integer getParallelLimit() {
        return parallelLimit;
    }

    public void setParallelLimit(Integer parallelLimit) {
        this.parallelLimit = parallelLimit;
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

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public RateLimit() {
        this.limitRealm = DEFAULT_LIMIT_REALM;
        this.flowRateLimitUnit = DEFAULT_FLOW_RATE_LIMIT_UNIT;
        this.openLimit = DEFAULT_OPEN_LIMIT;
    }

    public RateLimit(Long id) {
        this.id = id;
        this.limitRealm = DEFAULT_LIMIT_REALM;
        this.flowRateLimitUnit = DEFAULT_FLOW_RATE_LIMIT_UNIT;
        this.openLimit = DEFAULT_OPEN_LIMIT;
    }

    public RateLimit(RateLimitVo rateLimitVo) {
        this.id = rateLimitVo.getId();
        this.limitRealmId = rateLimitVo.getLimitRealmId();
        this.limitRealm = rateLimitVo.getLimitRealm();
        this.flowRateLimit = rateLimitVo.getFlowRateLimit();
        this.flowRateLimitUnit = rateLimitVo.getFlowRateLimitUnit();
        this.recordRateLimit = rateLimitVo.getRecordRateLimit();
        this.parallelLimit = rateLimitVo.getParallelLimit();
        this.openLimit = rateLimitVo.getOpenLimit();
        this.createUser = rateLimitVo.getCreateUser();
        this.modifyUser = rateLimitVo.getModifyUser();
    }

}
