package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.server.vo.JobFunction;

import java.util.List;
import java.util.Map;

/**
 * @author davidhua
 * 2020/4/21
 */
public interface JobFuncService {

    /**
     * Fetch map: function -> refer name
     * @param tabName tab name
     * @param functionType type
     * @return
     */
    Map<String, String> getFuncRefName(String tabName, JobFunction.FunctionType functionType);

    /**
     * Fetch function list
     * @param tabName tab name
     * @param functionType type
     * @return
     */
    List<JobFunction> getFunctions(String tabName, JobFunction.FunctionType functionType);
}
