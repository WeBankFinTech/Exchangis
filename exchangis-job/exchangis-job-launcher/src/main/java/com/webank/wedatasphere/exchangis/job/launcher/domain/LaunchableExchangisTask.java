package com.webank.wedatasphere.exchangis.job.launcher.domain;

import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisTask;
import com.webank.wedatasphere.exchangis.job.utils.LabelConvertUtils;

import java.util.*;

/**
 * Task could be executed
 */
public class LaunchableExchangisTask implements ExchangisTask {

    private Long id;

    private String name;

    private Date createTime;

    private Date lastUpdateTime;

    private String engineType;

    private String executeUser;

    private String linkisJobName;

    private Long jobId;

    private String jobExecutionId;

    private List<Long> dsModelIds = new ArrayList<>();

    /**
     * Job content in Linkis
     */
    private String linkisJobContent;

    /**
     * Job params in Linkis
     */
    private String linkisParams;

    /**
     * Source message in Linkis
     */
    private String linkisSource;

    /**
     * Rate params
     */
    private String rateParams;

    private String sourceType;

    private String sinkType;

    private String sourceId;

    private String sinkId;

    private String content;

    /**
     * Labels string value
     */
    private String labels;

    /**
     * Instance name
     */
    private String instance;

    /**
     * Launch delay time
     */
    private Date delayTime;

    /**
     * Launch delay count
     */
    private Integer delayCount = 0;

    private Map<String, Object> linkisContentMap;

    private Map<String, Object> linkisParamsMap;

    private Map<String, Object> rateParamsMap;

    private Map<String, Object> linkisSourceMap;

    private Map<String, Object> labelsMap;

    /**
     * Commit version
     */
    private Integer commitVersion;

    @Override
    public String getEngineType() {
        return this.engineType;
    }

    @Override
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    @Override
    public String getExecuteUser() {
        return this.executeUser;
    }

    @Override
    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    @Override
    public String getInstance() {
        return this.instance;
    }

    @Override
    public void setInstance(String instance) {
        this.instance = instance;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
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
        return this.lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public List<Long> getDsModelIds() {
        return dsModelIds;
    }

    public void setDsModelIds(List<Long> dsModelIds) {
        this.dsModelIds = dsModelIds;
    }

    public void setLinkisContentMap(Map<String, Object> linkisContentMap) {
        Optional.ofNullable(linkisContentMap).ifPresent(value -> this.linkisContentMap = value);
    }

    public void setLinkisParamsMap(Map<String, Object> linkisParamsMap) {
        Optional.ofNullable(linkisParamsMap).ifPresent(value -> this.linkisParamsMap = value);
    }

    public Map<String, Object> getRateParamsMap() {
        return rateParamsMap;
    }

    public void setRateParamsMap(Map<String, Object> rateParamsMap) {
        this.rateParamsMap = rateParamsMap;
    }

    public void setLinkisSourceMap(Map<String, Object> linkisSourceMap) {
        Optional.ofNullable(linkisSourceMap).ifPresent(value -> this.linkisSourceMap = value);
    }

    public Map<String, Object> getLinkisContentMap() {
        if(Objects.isNull(this.linkisContentMap) && Objects.nonNull(this.linkisJobContent)){
            this.linkisContentMap = Json.fromJson(this.linkisJobContent, Map.class);
        }
        return linkisContentMap;
    }

    public Map<String, Object> getLinkisParamsMap() {
        if(Objects.isNull(this.linkisParamsMap) && Objects.nonNull(this.linkisParams)){
            this.linkisParamsMap = Json.fromJson(this.linkisParams, Map.class);
        }
        return linkisParamsMap;
    }

    public Map<String, Object> getLinkisSourceMap() {
        if(Objects.isNull(this.linkisSourceMap) && Objects.nonNull(this.linkisSource)){
            this.linkisSourceMap = Json.fromJson(this.linkisParams, Map.class);
        }
        return linkisSourceMap;
    }

    public Map<String, Object> getLabelsMap() {
        if(Objects.isNull(this.labelsMap) && Objects.nonNull(this.labels)){
            this.labelsMap = LabelConvertUtils.stringToLabelMap(this.labels);
        }
        return labelsMap;
    }

    public void setLabelsMap(Map<String, Object> labelsMap) {
        this.labelsMap = labelsMap;
    }

    public String getLinkisJobName() {
        return linkisJobName;
    }

    public void setLinkisJobName(String linkisJobName) {
        this.linkisJobName = linkisJobName;
    }

    public String getLinkisJobContent() {
        if (Objects.isNull(this.linkisJobContent) && Objects.nonNull(this.linkisContentMap)){
            this.linkisJobContent = Json.toJson(this.linkisContentMap, null);
        }
        return linkisJobContent;
    }

    public void setLinkisJobContent(String linkisJobContent) {
        this.linkisJobContent = linkisJobContent;
    }

    public String getLinkisParams() {
        if (Objects.isNull(this.linkisParams) && Objects.nonNull(this.linkisParamsMap)){
            this.linkisParams = Json.toJson(this.linkisParamsMap, null);
        }
        return linkisParams;
    }

    public void setLinkisParams(String linkisParams) {
        this.linkisParams = linkisParams;
    }

    public String getLinkisSource() {
        if (Objects.isNull(this.linkisSource) && Objects.nonNull(this.linkisSourceMap)){
            this.linkisSource = Json.toJson(this.linkisSourceMap, null);
        }
        return linkisSource;
    }

    public void setLinkisSource(String linkisSource) {
        this.linkisSource = linkisSource;
    }

    public String getRateParams() {
        return rateParams;
    }

    public void setRateParams(String rateParams) {
        this.rateParams = rateParams;
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

    public String getLabels() {
        if (Objects.isNull(this.linkisParams) && Objects.nonNull(this.linkisParamsMap)){
            this.linkisParams = Json.toJson(this.linkisParamsMap, null);
        }
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Date getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Date delayTime) {
        this.delayTime = delayTime;
    }

    public Integer getDelayCount() {
        return delayCount;
    }

    public void setDelayCount(Integer delayCount) {
        this.delayCount = delayCount;
    }

    public Integer getCommitVersion() {
        return commitVersion;
    }

    public void setCommitVersion(Integer commitVersion) {
        this.commitVersion = commitVersion;
    }

    /**
     * Clear the content
     */
    public void simplify(){
        this.linkisParams = null;
        this.linkisParamsMap = null;
        this.linkisJobContent = null;
        this.linkisContentMap = null;
        this.linkisSource = null;
        this.linkisSourceMap = null;//todo
    }

}
