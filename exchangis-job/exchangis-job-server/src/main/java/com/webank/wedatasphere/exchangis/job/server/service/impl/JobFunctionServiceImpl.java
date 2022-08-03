package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.server.mapper.JobFunctionDao;
import com.webank.wedatasphere.exchangis.job.server.service.JobFuncService;
import com.webank.wedatasphere.exchangis.job.server.vo.JobFunction;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author davidhua
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
