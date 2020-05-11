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

package com.webank.wedatasphere.exchangis.executor.task;

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceBase;
import com.webank.wedatasphere.exchangis.executor.service.CallBackService;
import com.webank.wedatasphere.exchangis.executor.util.TaskConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author davidhua
 * 2019/12/24
 */
public abstract class AbstractTaskConfigBuilder implements TaskConfigBuilder{
    private static final String DATASOURCE_ID_PARAMETER = "datasource";
    /**
     * Inject data source parameters into task configuration
     * @param callBackService call back service api
     * @param configuration task configuration
     * @param path the path to inject
     */
    protected DataSourceBase injectDataSourceParamToConf(CallBackService callBackService,
                                               TaskConfiguration configuration, String path){
        Integer ds = null;
        try{
            ds = configuration.getInt(StringUtils.join(new String[]{path, DATASOURCE_ID_PARAMETER}, TaskConfiguration.SPLIT_CHAR));
        }catch(Exception e){
            //ignore
        }
        DataSourceBase dataSourceBase = null;
        if(ds != null && ds > 0){
            dataSourceBase = callBackService.getDataSource(ds).getData();
            if(StringUtils.isNotBlank(dataSourceBase.getParameter())) {
                Map<String, Object> map = Json.fromJson(dataSourceBase.getParameter(), Map.class);
                map.forEach((key, value) -> {
                    //Search and insert (key, value)
                    if (!TaskConfiguration.searchKeyToInsertValue(configuration, path, key, value)) {
                        //Not found, set the value to {path}.key node
                        String key0 = StringUtils.join(new String[]{path, key}, TaskConfiguration.SPLIT_CHAR);
                        configuration.set(key0, value);
                    }
                });
            }
        }
        return dataSourceBase;
    }
}
