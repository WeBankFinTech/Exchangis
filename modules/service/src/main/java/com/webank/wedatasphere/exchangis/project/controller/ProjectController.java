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

package com.webank.wedatasphere.exchangis.project.controller;

import com.webank.wedatasphere.exchangis.common.auth.data.DataAuthScope;
import com.webank.wedatasphere.exchangis.auth.domain.UserRole;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.AbstractGenericController;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.job.domain.JobInfo;
import com.webank.wedatasphere.exchangis.job.query.JobInfoQuery;
import com.webank.wedatasphere.exchangis.job.service.impl.JobInfoServiceImpl;
import com.webank.wedatasphere.exchangis.project.domain.Project;
import com.webank.wedatasphere.exchangis.project.query.ProjectQuery;
import com.webank.wedatasphere.exchangis.project.service.ProjectService;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * Project management
 * Created by devendeng on 2018/9/20.
 */
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController extends AbstractGenericController<Project, ProjectQuery> {
    @Resource
    private ProjectService projectService;
    @Resource
    private JobInfoServiceImpl jobInfoService;
    @Override
    public IBaseService<Project> getBaseService() {
        return projectService;
    }
    @Resource
    private GroupService groupService;

    @Resource
    private UserInfoService userInfoService;

    @PostConstruct
    public void init(){
        security.registerUserExternalDataAuthGetter(Project.class, userName ->{
            UserInfo userInfo = userInfoService.selectByUsername(userName);
            if(userInfo.getUserType() >= UserRole.MANGER.getValue()){
                //No limit
                return null;
            }
            return groupService.queryGroupRefProjectsByUser(userName);
        });
        security.registerExternalDataAuthGetter(Project.class, project -> Collections.singletonList(String.valueOf(project.getId())));
        security.registerExternalDataAuthScopeGetter(Project.class, project -> Arrays.asList(DataAuthScope.READ, DataAuthScope.WRITE, DataAuthScope.EXECUTE));
    }

    /**
     * Get child projects
     * @return
     */
    @RequestMapping(value = "/{id:\\w+}/child", method = RequestMethod.GET)
    public Response<Object> treeChild(@PathVariable("id")Long id, HttpServletRequest request){
        List<Project> list = projectService.listChild(id, security.getUserName(request));
        return new Response<>().successResponse(list);
    }

    @Override
    public Response<Project> add(@Valid @RequestBody Project project, HttpServletRequest request) {
        if(project.getParentId() == null){
            project.setParentId(0);
        }
        if(project.getParentId() > 0) {
            Project parent = projectService.get(project.getParentId());
            if(null == parent){
                return new Response<Project>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                        super.informationSwitch("exchange.project.not.exist"));
            }
            if(!hasDataAuth(Project.class, DataAuthScope.WRITE, request, parent)){
                return new Response<Project>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.project.no.operation.privilege"));
            }
        }
        //No limit for adding
        return super.add(project, request);
    }

    @Override
    public Response<Project> update(@Valid @RequestBody Project project, HttpServletRequest request) {
        Project storedProject = projectService.get(project.getId());
        if(!hasDataAuth(Project.class, DataAuthScope.WRITE, request, storedProject)){
            return new Response<Project>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.project.no.operation.privilege"));
        }
        if(project.getParentId() == null){
            project.setParentId(0);
        }
        if(project.getParentId() > 0){
            Project parent = projectService.get(project.getParentId());
            if(null == parent){
                return new Response<Project>().errorResponse(CodeConstant.PARAMETER_ERROR, null, super.informationSwitch("exchange.project.not.exist"));
            }else if (!hasDataAuth(Project.class, DataAuthScope.WRITE, request, parent)){
                return new Response<Project>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.project.no.operation.privilege"));
            }
        }
        return super.update(project, request);
    }

    @Override
    public Response<Project> delete(@PathVariable Long id, HttpServletRequest request) {
        Project project = projectService.get(id);
        if(!hasDataAuth(Project.class, DataAuthScope.DELETE, request, project)){
            return new Response<Project>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.project.no.operation.privilege"));
        }
        //Cascade delete
        List<Object> ids = new ArrayList<>();
        Queue<Project> queue = new LinkedList<>();
        List<Project> stored = new ArrayList<>();
        JobInfoQuery query = new JobInfoQuery();
        queue.add(project);
        stored.add(project);
        while(!queue.isEmpty()){
            project = queue.poll();
            //Delete if there are no jobs under the project
            query.setProjectId(project.getId());
            List<JobInfo> jobs = jobInfoService.selectAllList(query);
            if(!jobs.isEmpty()){
                return new Response<Project>().errorResponse(CodeConstant.PARAMETER_ERROR, null,
                        super.informationSwitch("exchange.project.cannot.delete"));
            }
            ids.add(project.getId());
            ProjectQuery pjQuery = new ProjectQuery();
            pjQuery.setParentId(project.getId());
            List<Project> projects = getBaseService().selectAllList(pjQuery);
            queue.addAll(projects);
            stored.addAll(projects);
        }
        boolean result = projectService.delete("", stored);
        return  result ? new Response<Project>().successResponse(null) : new Response<Project>().errorResponse(1, null, super.informationSwitch("exchange.project.tasks.delete.failed"));
    }

    @Override
    public Response<Project> show(@PathVariable Long id, HttpServletRequest request) throws Exception {
        Project project = projectService.get(id);
        if(!hasDataAuth(Project.class, DataAuthScope.READ, request, project)){
            return new Response<Project>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchange.project.no.operation.privilege"));
        }
        return new Response<Project>().successResponse(project);
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public Response<Object> tree(ProjectQuery query, HttpServletRequest request){
        String username = security.getUserName(request);
        if(StringUtils.isNotBlank(username)){
            security.bindUserInfoAndDataAuth(query, request,
                    security.userExternalDataAuthGetter(getActualType()).get(username));

        }
        List<Project> data = getBaseService().selectAllList(query);
        Map<String, Project> treeMap = new HashMap<>();
        data.forEach(element -> {
            //Bind authority scopes
            treeMap.put(String.valueOf(element.getId()), element);
            security.bindAuthScope(data, security.externalDataAuthScopeGetter(getActualType()).get(element));
        });
        //Build tree
        for (Map.Entry<String, Project> entry : treeMap.entrySet()) {
            Project value = entry.getValue();
            if (null != value.getParentId()) {
                Project parent = treeMap.get(String.valueOf(value.getParentId()));
                if (null != parent) {
                    parent.getChildren().add(value);
                    value.setLevel(parent.getLevel() + 1);
                    data.remove(value);
                }
            }
        }

        return new Response<>().successResponse(data);
    }
}
