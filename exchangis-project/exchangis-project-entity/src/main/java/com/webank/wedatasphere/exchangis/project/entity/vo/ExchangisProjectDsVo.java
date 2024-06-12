package com.webank.wedatasphere.exchangis.project.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;

import java.util.Date;

/**
 * Project id name
 */
@JsonIgnoreProperties({"id", "name", "type", "desc", "creator"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExchangisProjectDsVo implements ExchangisDataSource {

    private Long dataSourceId;
    /**
     * Data source name
     */
    @JsonProperty("dataSourceName")
    private String dataSourceName;

    /**
     * Data source desc
     */
    @JsonProperty("dataSourceDesc")
    private String dataSourceDesc;

    @JsonProperty("createUser")
    private String createUser;

    /**
     * Data source type
     */
    @JsonProperty("dataSourceType")
    private String dataSourceType;

    /**
     * Create time
     */
    @JsonProperty("createTime")
    private Date createTime;

    /**
     * Modify time
     */
    @JsonProperty("modifyTime")
    private Date modifyTime;

    @Override
    public Long getId() {
        return dataSourceId;
    }

    @Override
    public void setId(Long id) {
        this.dataSourceId = id;
    }

    @Override
    public String getType() {
        return this.dataSourceType;
    }

    @Override
    public void setType(String type) {
        this.dataSourceType = type;
    }

    @Override
    public String getName() {
        return this.dataSourceName;
    }

    @Override
    public void setName(String name) {
        this.dataSourceName = name;
    }

    @Override
    public String getCreator() {
        return this.createUser;
    }

    @Override
    public void setCreator(String creator) {
        this.createUser = creator;
    }

    @Override
    public String getDesc() {
        return this.dataSourceDesc;
    }

    @Override
    public void setDesc(String desc) {
        this.dataSourceDesc = desc;
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

}
