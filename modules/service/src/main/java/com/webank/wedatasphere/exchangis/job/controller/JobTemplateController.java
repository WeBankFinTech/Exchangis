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

package com.webank.wedatasphere.exchangis.job.controller;

import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.datasource.TypeEnums;
import com.webank.wedatasphere.exchangis.job.config.builder.JobPostProcessorBuilder;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import com.webank.wedatasphere.exchangis.job.service.JobInfoConfService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Job template
 * @author enjoyyin
 * 2019/1/18
 */

@RequestMapping("/api/v1/job/tpl")
@RestController
public class JobTemplateController extends ExceptionResolverContext {

    @Resource
    private JobInfoConfService jobInfoConfSerivce;

    @RequestMapping(value = "/src/{src_type:\\w+}/dest/{dest_type:\\w+}", method = RequestMethod.GET)
    public Response<JobInfo> transportTemplate(@PathVariable("src_type")String srcType, @PathVariable("dest_type")String destType){
        if(TypeEnums.NONE == TypeEnums.type(srcType)){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_template.source.data.not.supported"));
        }
        if(TypeEnums.NONE == TypeEnums.type(destType)){
            return new Response<JobInfo>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_template.target.data.not.supported"));
        }
        JobInfo jobInfo = jobInfoConfSerivce.templateInfo(TypeEnums.type(srcType), TypeEnums.type(destType));
        jobInfo.setExecUser("<execUser>");
        jobInfo.setAlarmUser("<alarmUser>");
        return new Response<JobInfo>().successResponse(jobInfo);
    }

    @RequestMapping(value = "/proc/{type}", method = RequestMethod.GET)
    public Response<String> procTemplate(@PathVariable("type")String type){
        if(TypeEnums.NONE == TypeEnums.type(type)){
            return new Response<String>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.job_template.unknow.data.source.type"));
        }
        String rawTemplate = JobPostProcessorBuilder.custom(TypeEnums.type(type)).getRawTemplate();
        return new Response<String>().successResponse(rawTemplate);
    }
}
