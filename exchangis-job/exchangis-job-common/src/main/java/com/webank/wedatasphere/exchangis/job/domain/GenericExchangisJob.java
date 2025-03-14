package com.webank.wedatasphere.exchangis.job.domain;

import com.webank.wedatasphere.exchangis.job.utils.LabelConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Generic implement
 */
public class GenericExchangisJob implements ExchangisJob {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExchangisJob.class);

    protected Long id;

    protected String name;

    protected String engineType;

    private String sourceType;

    private String sinkType;

    private String sourceId;

    private String sinkId;

    private String content;

    protected String jobLabel;

    private Map<String, Object> labelHolder = new HashMap<>();

    protected Date createTime;

    protected Date lastUpdateTime;

    protected String createUser;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String getEngineType() {
        return this.engineType;
    }

    @Override
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSinkId() {
        return sinkId;
    }

    public void setSinkId(String sinkId) {
        this.sinkId = sinkId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getJobLabel() {
        return jobLabel;
    }

    @Override
    public void setJobLabel(String jobLabel) {
        this.jobLabel = jobLabel;
    }

    @Override
    public Map<String, Object> getJobLabels() {
        return labelHolder;
    }

    @Override
    public void setJobLabels(String labels) {
        this.labelHolder = LabelConvertUtils.stringToLabelMap(labels);
    }

    @Override
    public void setJobLabels(Map<String, Object> jobLabels) {
        this.labelHolder = jobLabels;
    }

    @Override
    public String getCreateUser() {
        return createUser;
    }

    @Override
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
