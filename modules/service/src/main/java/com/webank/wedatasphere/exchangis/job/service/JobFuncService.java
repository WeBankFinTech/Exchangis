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

package com.webank.wedatasphere.exchangis.job.service;

import com.webank.wedatasphere.exchangis.job.domain.JobFunction;

import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
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
