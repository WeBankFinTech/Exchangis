package com.webank.wedatasphere.exchangis.datasource.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;
import org.apache.linkis.datasourcemanager.common.domain.DataSource;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;

import java.util.Objects;

/**
 * Exchangis data source detail
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExchangisDataSourceDetail extends DataSource implements ExchangisDataSource {
    /**
     * Model info
     */
    private Long modelId;

    private String modelName;


    public ExchangisDataSourceDetail(){

    }

    /**
     * Copy from linkis data source
     * @param source source
     */
    public ExchangisDataSourceDetail(DataSource source){
        this.setId(source.getId());
        this.setDataSourceName(source.getDataSourceName());
        this.setDataSourceDesc(source.getDataSourceDesc());
        this.setDataSourceTypeId(source.getDataSourceTypeId());
        this.setCreateIdentify(source.getCreateIdentify());
        this.setCreateSystem(source.getCreateSystem());
        this.setConnectParams(source.getConnectParams());
        this.setDataSourceEnvId(source.getDataSourceEnvId());
        this.setCreateTime(source.getCreateTime());
        this.setModifyTime(source.getModifyTime());
        this.setModifyUser(source.getModifyUser());
        this.setCreateUser(source.getCreateUser());
        this.setLabels(source.getLabels());
        this.setVersionId(source.getVersionId());
        this.setVersions(source.getVersion());
        this.setPublishedVersionId(source.getPublishedVersionId());
        this.setExpire(source.isExpire());
        this.setDataSourceType(source.getDataSourceType());
        this.setDataSourceEnv(source.getDataSourceEnv());
    }
    @Override
    public String getType() {
        DataSourceType type = this.getDataSourceType();
        if (Objects.nonNull(type)){
            return type.getName();
        }
        return null;
    }

    @Override
    public void setType(String type) {
        // Not support
    }

    @Override
    public String getName() {
        return this.getDataSourceName();
    }

    @Override
    public void setName(String name) {
        // Not support
    }

    @Override
    public String getCreator() {
        return this.getCreateUser();
    }

    @Override
    public void setCreator(String creator) {
        // Not support
    }

    @Override
    public String getDesc() {
        return this.getDataSourceDesc();
    }

    @Override
    public void setDesc(String desc) {
        // Not support
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
