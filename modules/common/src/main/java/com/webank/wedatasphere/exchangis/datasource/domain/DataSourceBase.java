/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.datasource.domain;

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class of data source
 * @author davidhua
 * 2020/4/5
 */
public class DataSourceBase {
    /**
     * Id
     */
    private long id;

    /**
     * Data source name
     */
    @NotBlank(message = "{udes.domain.datasource.name.notBlank}")
    @Size(max = 100, message = "{udes.domain.datasource.name.maxSize}")
    private String sourceName;

    /**
     * Description
     */
    @Size(max = 100, message = "{udes.domain.datasource.desc.maxSize}")
    private String sourceDesc;

    /**
     * Data source type
     */
    @NotBlank(message = "{udes.domain.datasource.type.notBlank}")
    @Size(max = 50, message = "{udes.domain.datasource.type.maxSize}")
    private String sourceType;

    /**
     * Data source owner
     */
    @NotBlank(message = "{udes.domain.datasource.owner.notBlank}")
    @Size(max = 50, message = "{udes.domain.datasource.owner.maxSize}")
    private String owner;

    /**
     * ID of data source model
     */
    private Integer modelId;

    /**
     * Auth credential for data source connection
     */
    private String authCreden;

    /**
     * Auth entity for data source connection
     */
//    @NotBlank(message = "{udes.domain.datasource.authentity.notBlank}")
    private String authEntity;

    /**
     * Create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * Create user
     */
    private String createUser = "0";

    /**
     * Modify user
     */
    private String modifyUser = "0";

    /**
     * Modify time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    /**
     * Parameters of connection
     */
    private String parameter;

    @JsonIgnore
    private Map<String, Object> parameterMap;

    /**
     * Project id
     */
    private Long projectId = 0L;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getAuthCreden() {
        return authCreden;
    }

    public void setAuthCreden(String authCreden) {
        this.authCreden = authCreden;
    }

    public String getAuthEntity() {
        return authEntity;
    }

    public void setAuthEntity(String authEntity) {
        this.authEntity = authEntity;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }


    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameterMap = null;
        this.parameter = parameter;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    @Override
    public String toString(){
        return "";
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

}
