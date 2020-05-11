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

package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author davidhua
 * 2020/4/5
 */
public interface DataSourceService extends IBaseService<DataSource> {

    /**
     * Get detail information (encrypted)
     * @param id id
     * @return
     */
    DataSource getDetail(Object id);

    /**
     * Get simple information (decrypted)
     * @param id
     * @return
     */
    DataSource getDecryptedSimpleOne(Object id);
    /**
     * Store file
     * @param authFile multipart file
     * @param authType authentication type
     * @param disposable disposable
     * @return File
     * @throws IOException
     */
    File store(MultipartFile authFile, String authType, boolean disposable) throws IOException;

    /**
     * Store file
     * @param authFile multipart file
     * @param authType authentication type
     * @return File
     * @throws IOException
     */
    File store(MultipartFile authFile, String authType) throws IOException;

    /**
     * Add parameters in data source model to map
     * @param map map
     * @param authEntity entity
     * @param authCreden credential
     * @param modelName model name
     */
    void addDataSourceModelParamsToMap(Map<String, Object> map, String authEntity, String authCreden, String modelName);
    void addDataSourceModelParamsToMap(Map<String, Object> map,
                                       String authEntity, String authCreden, Integer modelId);
    /**
     * Fill data source entity with model
     * @param dataSource data source
     * @param modelId model id
     */
    void fillDataSourceWithModel(DataSource dataSource, Integer modelId);

    /**
     * Get data source permission
     * @param id
     * @return
     */
    List<DataAuthScope> getPermission(Long id);

    /**
     * To bind project
     * @param projectId
     * @param dataSourceIds
     */
    void bindDataSourceToProject( Long projectId, Long... dataSourceIds);
}
