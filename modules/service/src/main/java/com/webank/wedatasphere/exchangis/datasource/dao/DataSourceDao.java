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

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import org.apache.ibatis.annotations.Param;

/**
 * @author devendeng on 2018/8/23.
 */
public interface DataSourceDao extends IBaseDao<DataSource> {
    /**
     * Select and lock
     * @param key
     * @return
     */
    DataSource selectOneAndLock(Object key);

    /**
     * Bind project
     * @param dataSourceId data source id
     * @param projectId project id
     */
    void bindProject(@Param("dataSourceId")Long dataSourceId, @Param("projectId") @javax.validation.constraints.NotNull(message = "{udes.domain.jobInfo.project.id.notNull}") Long projectId);
}
