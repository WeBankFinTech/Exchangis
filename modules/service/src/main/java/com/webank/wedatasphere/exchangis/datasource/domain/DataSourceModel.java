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
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Data source model
 * @author Created by wufan on 2019/7/3.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataSourceModel {
    private int id;

    /**
     * Model name
     */
    @NotBlank(message = "{udes.domain.model.name.notBlank}")
    @Size(max = 100, message = "{udes.domain.model.name.maxSize}")
    private String modelName;

    /**
     * Model description
     */
    @Size(max = 100, message = "{udes.domain.model.desc.maxSize}")
    private String modelDesc;

    /**
     * Data source type
     */
    @NotBlank(message = "{udes.domain.model.type.notBlank}")
    @Size(max = 50, message = "{udes.domain.model.type.maxSize}")
    private String sourceType;

    /**
     * Model parameters
     */
    @NotBlank(message = "{udes.domain.model.parameter.notBlank}")
    private String parameter;

    @JsonIgnore
    private Map<String, Object> parameterMap;

    /**
     * Create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * The owner of this model
     */
    private String createOwner = "";

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

    private List<String> authScopes = new ArrayList<>();
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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameterMap = null;
        this.parameter = parameter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDesc() {
        return modelDesc;
    }

    public void setModelDesc(String modelDesc) {
        this.modelDesc = modelDesc;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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

    public String getCreateOwner() {
        return createOwner;
    }

    public void setCreateOwner(String createOwner) {
        this.createOwner = createOwner;
    }

    public List<String> getAuthScopes() {
        return authScopes;
    }

    public void setAuthScopes(List<String> authScopes) {
        this.authScopes = authScopes;
    }
}
