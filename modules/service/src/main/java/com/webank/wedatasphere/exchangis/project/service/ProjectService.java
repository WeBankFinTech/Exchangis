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

package com.webank.wedatasphere.exchangis.project.service;

import com.webank.wedatasphere.exchangis.common.service.IBaseService;
import com.webank.wedatasphere.exchangis.project.domain.Project;

import java.util.List;

/**
 * @author davidhua
 * 2018/10/18
 */
public interface ProjectService extends IBaseService<Project> {
    /**
     * List all child project by parentId
     * @param parentId parent id
     * @param userName username
     * @return
     */
    List<Project> listChild(Long parentId, String userName);

    /**
     * Delete
     * @param projects projects
     */
    boolean delete(String operator, List<Project> projects);
}
