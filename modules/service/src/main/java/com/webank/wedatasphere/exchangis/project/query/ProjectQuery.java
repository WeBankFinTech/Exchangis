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

package com.webank.wedatasphere.exchangis.project.query;

import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by devendeng on 2018/9/20.
 */
public class ProjectQuery extends PageQuery{
    private String projectName;
    private Long parentId;
    private String createUser;
    private Set<String> userDataAuth = new HashSet<>();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Set<String> getUserDataAuth() {
        return userDataAuth;
    }

    public void setUserDataAuth(Set<String> userDataAuth) {
        this.userDataAuth = userDataAuth;
    }
}
