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

package com.webank.wedatasphere.exchangis.datasource.query;

import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by devendeng on 2018/8/23.
 */
public class DataSourceQuery extends PageQuery {
    private long id;
    private String sourceName;
    private String sourceType;
    private String owner;
    private Date createTimeBegin;
    private Date createTimeEnd;
    private String createUser;
    private Integer modelId;
    private String projectIds;
    @JsonIgnore
    private List<String> projectIdList = new ArrayList<>();
    @JsonIgnore
    private Set<String> userDataAuth = new HashSet<>();

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

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    public Set<String> getUserDataAuth() {
        if(StringUtils.isNotBlank(projectIds)){
            return null;
        }
        return userDataAuth;
    }

    public List<String> getProjectIdList() {
        if(StringUtils.isNotBlank(projectIds)){
            List<String> projectIdList = new ArrayList<>(this.projectIdList);
            String[] ids = projectIds.split(",");
            for(String id : ids){
                if(null == userDataAuth || userDataAuth.contains(id)){
                    projectIdList.add(id);
                }
            }
            return projectIdList;
        }
        return projectIdList;
    }

    public void setUserDataAuth(Set<String> userDataAuth) {
        this.userDataAuth = userDataAuth;
    }
}
