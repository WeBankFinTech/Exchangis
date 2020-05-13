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

package com.webank.wedatasphere.exchangis.job.service.impl;

import com.webank.wedatasphere.exchangis.job.dao.JobFunctionDao;
import com.webank.wedatasphere.exchangis.job.domain.JobFunction;
import com.webank.wedatasphere.exchangis.job.service.JobFuncService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * 2020/4/23
 */
@Service
public class JobFunctionServiceImpl implements JobFuncService {
    @Resource
    private JobFunctionDao functionDao;


    @Override
    public Map<String, String> getFuncRefName(String tabName, JobFunction.FunctionType functionType) {
        Map<String, String> funcRefNameMap = new HashMap<>();
        List<JobFunction> jobFunctions = functionDao.listFunctions(tabName, functionType.name());
        jobFunctions.forEach(jobFunction -> {
            String funcName = jobFunction.getFuncName();
            String refName = jobFunction.getRefName();
            if(StringUtils.isNotBlank(funcName) && StringUtils.isNotBlank(refName)){
                funcRefNameMap.put(funcName, refName);
            }
        });
        return funcRefNameMap;
    }

    @Override
    public List<JobFunction> getFunctions(String tabName, JobFunction.FunctionType functionType) {
        return functionDao.listFunctions(tabName, functionType.name());
    }
}
