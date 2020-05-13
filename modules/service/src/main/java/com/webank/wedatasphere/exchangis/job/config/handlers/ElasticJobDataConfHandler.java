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

package com.webank.wedatasphere.exchangis.job.config.handlers;

import com.webank.wedatasphere.exchangis.datasource.Constants;
import com.webank.wedatasphere.exchangis.datasource.domain.DataSource;
import com.webank.wedatasphere.exchangis.job.JobConstants;
import com.webank.wedatasphere.exchangis.job.config.dto.DataColumn;
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * 2019/8/23
 */
@Service(JobDataConfHandler.PREFIX + "elasticsearch")
public class ElasticJobDataConfHandler extends AbstractJobDataConfHandler{
    private static final Logger logger = LoggerFactory.getLogger(ElasticJobDataConfHandler.class);

    private static final String ES_INDEX_NAME = "indexName";
    private static final String ES_INDEX_TYPE = "indexType";
    private static final String ES_BATCH_SIZE = "batchSize";

    private static final int MAX_BATCH_SIZE = 100000;
    @Override
    protected String[] connParamNames() {
        return new String[]{
                Constants.PARAM_DEFAULT_PASSWORD,
                Constants.PARAM_DEFAULT_USERNAME,
                Constants.PARAM_ES_URLS
        };
    }

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        String index = String.valueOf(dataFormParams.getOrDefault(ES_INDEX_NAME, ""));
        if(StringUtils.isBlank(index)){
            throw new JobDataParamsInValidException("exchange.job.handler.elastic.index.notNull");
        }
        if(StringUtils.isNotBlank(String.valueOf(dataFormParams
                .getOrDefault(ES_BATCH_SIZE, "")))){
            int batchSize = Integer.parseInt(String.valueOf(dataFormParams.get(ES_BATCH_SIZE)));
            if(batchSize > MAX_BATCH_SIZE){
                throw new JobDataParamsInValidException("exchange.job.handler.elastic.index.batchSize",
                        MAX_BATCH_SIZE);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void prePersistWriter(DataSource dataSource, Map<String, Object> dataFormParams) {
        List<DataColumn> columns = (List<DataColumn>) dataFormParams.get(JobConstants.CONFIG_COLUM_NAME);
        //Remove index property
        columns.forEach( dataColumn -> {
            dataColumn.setIndex(null);
        });
    }

    @Override
    protected Map<String, Object> postGetWriter(Map<String, Object> dataConfParams) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(ES_INDEX_NAME, dataConfParams.getOrDefault(ES_INDEX_NAME, ""));
        result.put(ES_INDEX_TYPE, dataConfParams.getOrDefault(ES_INDEX_TYPE, ""));
        result.put(ES_BATCH_SIZE, dataConfParams.getOrDefault(ES_BATCH_SIZE, ""));
        return result;
    }
}
