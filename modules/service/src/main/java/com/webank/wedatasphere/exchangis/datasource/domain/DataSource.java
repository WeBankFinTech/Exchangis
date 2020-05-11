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



import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Data source configuration
 * @author Created by devendeng on 2018/8/23.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataSource extends DataSourceBase{

    /**
     * Model structure
     */
    private DataSourceModel model;

    private List<String> authScopes = new ArrayList<>();

    public DataSourceModel getModel() {
        return model;
    }

    public void setModel(DataSourceModel model) {
        this.model = model;
    }

    public List<String> getAuthScopes() {
        return authScopes;
    }

    public void setAuthScopes(List<String> authScopes) {
        this.authScopes = authScopes;
    }

    @Override
    public void setModifyUser(String modifyUser) {
        super.setModifyUser(modifyUser);
    }

    @Override
    public void setCreateUser(String createUser) {
        super.setCreateUser(createUser);
    }

    @Override
    public String getModifyUser() {
        return super.getModifyUser();
    }

    @Override
    public String getCreateUser() {
        return super.getCreateUser();
    }
}
