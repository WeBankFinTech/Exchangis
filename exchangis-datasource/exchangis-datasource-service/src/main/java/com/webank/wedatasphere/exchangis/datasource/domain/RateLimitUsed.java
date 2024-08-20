package com.webank.wedatasphere.exchangis.datasource.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RateLimitUsed {

    private Long id;

    private Long rateLimitId;

    private String rateLimitType;

    private Integer rateLimitUsed;

    private Integer rateLimitTotal;

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

    public Long getRateLimitId() {
        return rateLimitId;
    }

    public void setRateLimitId(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

    public String getRateLimitType() {
        return this.rateLimitType;
    }

    public void setRateLimitType(String rateLimitType) {
        this.rateLimitType = rateLimitType;
    }

    public Integer getRateLimitUsed() {
        return rateLimitUsed;
    }

    public void setRateLimitUsed(Integer rateLimitUsed) {
        this.rateLimitUsed = rateLimitUsed;
    }

    public Integer getRateLimitTotal() {
        return rateLimitTotal;
    }

    public void setRateLimitTotal(Integer rateLimitTotal) {
        this.rateLimitTotal = rateLimitTotal;
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

    public RateLimitUsed() {
        this.rateLimitUsed = 0;
    }

    public RateLimitUsed(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

}
