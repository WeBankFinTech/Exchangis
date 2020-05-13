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

import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.auth.annotations.ContainerAPI;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.job.domain.JobReport;
import com.webank.wedatasphere.exchangis.job.query.JobReportQuery;
import com.webank.wedatasphere.exchangis.job.service.impl.JobReportServiceImpl;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;

/**
 * Report entrance
 * @author devendeng
 * @date 2018/8/24
 */
@RestController
@RequestMapping("/api/v1/report")
public class JobReportController extends AbstractGenericController<JobReport, JobReportQuery> {
    @Resource
    private JobReportServiceImpl jobReportService;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public IBaseService<JobReport> getBaseService() {
        return jobReportService;
    }

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(JobReport.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //No limit
                return null;
            };
            return new ArrayList<>();
        });
    }
    @Override
    @ContainerAPI
    public Response<JobReport> add(@Valid @RequestBody JobReport jobReport, HttpServletRequest request) {
        return super.add(jobReport, request);
    }
}
