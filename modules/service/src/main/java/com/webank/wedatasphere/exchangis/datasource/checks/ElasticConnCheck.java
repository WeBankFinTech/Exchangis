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

package com.webank.wedatasphere.exchangis.datasource.checks;

import com.webank.wedatasphere.exchangis.datasource.conns.ElasticSearch;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

import static com.webank.wedatasphere.exchangis.datasource.Constants.*;
import static com.webank.wedatasphere.exchangis.datasource.checks.DataSourceConnCheck.PREFIX;

/**
 * @author davidhua
 * 2019/8/16
 */
@Service(PREFIX + "elasticsearch")
public class ElasticConnCheck extends AbstractDataSourceConnCheck{
    private static final Logger logger = LoggerFactory.getLogger(ElasticConnCheck.class);

    @Override
    public void validate(DataSourceModel md) throws Exception {
        Map<String, Object> params = md.resolveParams();
        if(!params.containsKey(PARAM_ES_URLS)){
            throw new Exception(PARAM_ES_URLS + " cannot be found");
        }
    }

    @Override
    public void check(DataSource ds, File file) throws Exception {
        Map<String, Object> parameters = ds.resolveParams();
        String elasticUrls = String.valueOf(parameters.get(PARAM_ES_URLS));
        String[] endPoints = elasticUrls.split(DEFAULT_ENDPOINT_SPLIT);
        String username = String.valueOf(parameters.getOrDefault(PARAM_DEFAULT_USERNAME, ""));
        String password = String.valueOf(parameters.getOrDefault(PARAM_DEFAULT_PASSWORD, ""));
        ElasticSearch.ElasticMeta client = ElasticSearch.buildClient(endPoints, username, password);
        client.ping();
        client.close();
    }
}
