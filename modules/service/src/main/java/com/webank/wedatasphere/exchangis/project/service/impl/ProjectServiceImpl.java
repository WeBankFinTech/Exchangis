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

package com.webank.wedatasphere.exchangis.project.service.impl;

import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.exceptions.EndPointException;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.common.util.PatternInjectUtils;
import com.webank.wedatasphere.exchangis.group.domain.Group;
import com.webank.wedatasphere.exchangis.group.domain.UserGroup;
import com.webank.wedatasphere.exchangis.group.service.GroupService;
import com.webank.wedatasphere.exchangis.project.dao.ProjectDao;
import com.webank.wedatasphere.exchangis.project.domain.Project;
import com.webank.wedatasphere.exchangis.project.query.ProjectQuery;
import com.webank.wedatasphere.exchangis.project.service.ProjectService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by devendeng on 2018/9/20.
 */
@Service
public class ProjectServiceImpl extends AbstractGenericService<Project> implements ProjectService {
    private static final String PROJECT_REF_GROUP_NAME = "${projectName}[AUTH]";
    @Resource
    private GroupService groupService;
    @Resource
    private ProjectDao projectDao;
    @Override
    protected IBaseDao<Project> getDao() {
        return projectDao;
    }

    @Override
    public List<Project> listChild(Long parentId, String userName) {
        ProjectQuery query = new ProjectQuery();
        query.setParentId(parentId);
        query.setCreateUser(userName);
        List<Project> projects =  projectDao.selectAllList(query);
        return queryFilter(projects);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String operator, List<Project> projects) {
        Map<String, List<Long>> creatorMap = new HashMap<>();
        List<Object> projectIds = new ArrayList<>();
        List<Object> groupIds = new ArrayList<>();
        projects.forEach(project ->{
            List<Long> value = creatorMap.computeIfAbsent(project.getCreateUser(), key -> new ArrayList<>());
            value.add(project.getId());
            projectIds.add(project.getId());
        });
        creatorMap.forEach((createUser, projectIds0)->{
            List<Group> groups = groupService.selectByCreatorAndProjects(createUser, projectIds0);
            groups.forEach(group ->{
                groupIds.add(group.getId());
            });
        });
        //Delete projects and the groups associated
        boolean result = true;
        if(!projectIds.isEmpty()){
            result = super.delete(projectIds);
        }
        if(!groupIds.isEmpty()) {
            groupService.delete(groupIds);
        }
        return result;
    }


    @Override
    protected Project queryFilter(Project project) {
        if(null != project){
            return project.cleanSensitive();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Project project) throws Exception {
        boolean result = false;
        try {
            result = super.add(project);
        } catch (DuplicateKeyException e) {
            throw new EndPointException("exchange.duplicate.entry.project_name", e);
        }
        Group group = new Group();
        group.setGroupName(PatternInjectUtils.inject(PROJECT_REF_GROUP_NAME, new String[]
                {project.getProjectName()}, false, false,true));
        group.setProjectId(project.getId());
        group.setCreateTime(Calendar.getInstance().getTime());
        group.setCreateUser(project.getCreateUser());
        //Add create user to group
        group.getUserList().add(new UserGroup(project.getCreateUser(), UserGroup.JoinRole.CREATOR, null));
        groupService.add(group);
        return result;
    }

}
