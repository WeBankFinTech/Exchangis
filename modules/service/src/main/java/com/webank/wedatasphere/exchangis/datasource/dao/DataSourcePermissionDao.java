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

package com.webank.wedatasphere.exchangis.datasource.dao;

import com.webank.wedatasphere.exchangis.datasource.domain.DataSourcePermission;

import java.util.List;

/**
 * @author davidhua
 * 2020/4/5
 */
public interface DataSourcePermissionDao {

    /**
     * Get data source permission
     * @param dataSourceId data source id
     * @return
     */
    DataSourcePermission getPermission(long dataSourceId);

    /**
     * Update
     * @param permission permission
     */
    void update(DataSourcePermission permission);

    /**
     * Insert one
     * @param permission permission
     */
    void insertOne(DataSourcePermission permission);

    /**
     * Delete by data source ids
     * @param dataSourceIds data source id
     */
    int deleteBatch(List<Object> dataSourceIds);
}
