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

package com.webank.wedatasphere.exchangis.exec.service.impl;

import com.webank.wedatasphere.exchangis.auth.dao.UserExecUserDao;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.Paginator;
import com.webank.wedatasphere.exchangis.exec.dao.ExecNodeUserDao;
import com.webank.wedatasphere.exchangis.exec.dao.ExecUserDao;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.exec.query.ExecUserQuery;
import com.webank.wedatasphere.exchangis.exec.service.ExecUserService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author davidhua
 * 2019/11/18
 */
@Service
public class ExecUserServiceImpl implements ExecUserService {

    @Resource
    private UserExecUserDao userExecUserDao;

    @Resource
    private ExecNodeUserDao execNodeUserDao;

    @Resource
    private ExecUserDao execUserDao;

    @Override
    public boolean havePermission(String appUser, String execUser) {
        Integer result =  userExecUserDao.exists(appUser, execUser);
        return result != null && result > 0;
    }

    @Override
    public List<String> getExecUserByAppUser(String appUser) {
        return userExecUserDao.getExcUserByAppUser(appUser);
    }

    @Override
    public List<ExecUser> listExecUser() {
        return execUserDao.listAll();
    }

    @Override
    public void addExecUser(ExecUser execUser) {
        execUserDao.insertOne(execUser);
    }

    @Override
    public ExecUser selectExecUserByName(String execUser) {
        return execUserDao.selectByName(execUser);
    }

    @Override
    public ExecUser selectExecUser(Integer id) {
        return execUserDao.selectOne(id);
    }

    @Override
    public void deleteExecUser(Integer id) {
        execUserDao.deleteOne(id);
    }

    @Override
    public PageList<ExecUser> findExecUserPage(ExecUserQuery query) {
        long count = execUserDao.count(query);
        int currentPage = query.getPage();
        int pageSize = query.getPageSize();
        PageList<ExecUser> pageList = new PageList<>(new Paginator(currentPage, pageSize, (int) count));
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<ExecUser> result = execUserDao.findPage(query, new RowBounds(offset, pageSize));
        pageList.setData(result);
        return pageList;
    }

    @Override
    public boolean hasBoundNode(String execUser) {
        Integer result = execNodeUserDao.existExecUser(execUser);
        return result != null && result > 0;
    }

    @Override
    public boolean hasBoundAppUser(String execUser) {
        Integer result = userExecUserDao.existsExecUser(execUser);
        return result != null && result > 0;
    }
}
