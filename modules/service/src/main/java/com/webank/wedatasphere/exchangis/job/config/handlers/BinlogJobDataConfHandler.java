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
import com.webank.wedatasphere.exchangis.job.config.exception.JobDataParamsInValidException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author enjoyyin
 * 2020/3/2
 */
@Service(JobDataConfHandler.PREFIX + "binlog")
public class BinlogJobDataConfHandler extends AbstractJobDataConfHandler{
    @Override
    protected String[] connParamNames() {
        return new String[]{Constants.PARAM_DEFAULT_PASSWORD,
        Constants.PARAM_DEFAULT_USERNAME,
        Constants.PARAM_BINLOG_HOST,
        Constants.PARAM_BINLOG_PORT};
    }
    private static final String DB_TABLE_LIST = "dbTableList";

    @Override
    protected void prePersistValidate(Map<String, Object> dataFormParams) {
        Object dbTableListObj = dataFormParams.get(DB_TABLE_LIST);
        if(null == dbTableListObj || !Map.class.isAssignableFrom(dbTableListObj.getClass())
            || ((Map)dbTableListObj).size() <= 0){
            throw new JobDataParamsInValidException("exchange.job.handler.binlog.dbTable.notNull");
        }
    }

    @Override
    protected void prePersistReader(DataSource dataSource, Map<String, Object> dataFormParams) {
        //TODO GET slave databases by dcn and set
    }
}
