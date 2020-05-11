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
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;

import java.util.List;

/**
 * @author  by v_fwu on 2019/7/3.
 */
public interface DataSourceModelDao extends IBaseDao<DataSourceModel> {

    /**
     * Select and lock
     * @param key
     * @return
     */
    DataSourceModel selectOneAndLock(Object key);
    List<DataSourceModel> selectInfoList(Object key);

    /**
     * Select by model name
     * @param name model name
     * @return
     */
    DataSourceModel selectOneByName(String name);
}
