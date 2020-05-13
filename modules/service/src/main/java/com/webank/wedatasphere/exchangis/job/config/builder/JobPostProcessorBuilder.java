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

package com.webank.wedatasphere.exchangis.job.config.builder;

import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.job.config.AbstractJobTemplate;
import com.webank.wedatasphere.exchangis.job.JobConstants;

/**
 * @author enjoyyin
 * 2019/8/22
 */
public class JobPostProcessorBuilder extends AbstractJobTemplate {
    private static final String PROCESSORS_DIRECTORY = "/processors";
    private static final String DEFAULT_PROC_SUFFIX = ".java";

    private TypeEnums typeEnums;

    private JobPostProcessorBuilder(TypeEnums typeEnums){
        this.typeEnums = typeEnums;
    }

    public static JobPostProcessorBuilder custom(TypeEnums typeEnums){
        return new JobPostProcessorBuilder(typeEnums);
    }

    public String getRawTemplate(){
        return getTemplateContent(JobConstants.DEFAULT_TEMPLATE_LOC +
                PROCESSORS_DIRECTORY + "/" + typeEnums.v() + DEFAULT_PROC_SUFFIX, true);
    }
}
