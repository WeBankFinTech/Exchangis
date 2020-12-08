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

package com.webank.wedatasphere.exchangis.user.service.impl;

import com.webank.wedatasphere.exchangis.auth.dao.UserExecNodeDao;
import com.webank.wedatasphere.exchangis.auth.dao.UserExecUserDao;
import com.webank.wedatasphere.exchangis.auth.domain.UserExecNode;
import com.webank.wedatasphere.exchangis.auth.domain.UserExecUser;
import com.webank.wedatasphere.exchangis.common.dao.IBaseDao;
import com.webank.wedatasphere.exchangis.common.service.AbstractGenericService;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.page.PageList;
import com.webank.wedatasphere.exchangis.common.util.page.PageQuery;
import com.webank.wedatasphere.exchangis.common.util.page.Paginator;
import com.webank.wedatasphere.exchangis.exec.domain.ExecUser;
import com.webank.wedatasphere.exchangis.job.domain.ExecutorNode;
import com.webank.wedatasphere.exchangis.user.dao.UserInfoDao;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author davidhua
 * 2019/4/9
 */
@Service
public class UserInfoServiceImpl extends AbstractGenericService<UserInfo> implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;

    @Resource
    private UserExecNodeDao userExecNodeDao;

    @Resource
    private UserExecUserDao userExecUserDao;

    private static final Integer PASSWORD_MIN_LENGTH = 12;

    @Override
    protected IBaseDao<UserInfo> getDao() {
        return userInfoDao;
    }

    @Override
    @Transactional
    public boolean add(UserInfo userInfo) {
        //Encrypt password
        if(StringUtils.isNotBlank(userInfo.getPassword())){
            userInfo.setPassword(CryptoUtils.md5(userInfo.getPassword(), userInfo.getUserName(), 2));
        }
        return super.add(userInfo);
    }


    /**
     * Sync user info into database
     * @param userInfo
     */
    @Override
    public void sync(UserInfo userInfo){
        boolean exist = userInfoDao.selectByUsername(userInfo.getUserName()) != null;
        if(!exist) {
            userInfoDao.insertOrUpdateOne(userInfo);
        }else{
            userInfoDao.update(userInfo);
        }
    }
    @Override
    public UserInfo selectByUsername(String userName){
        return userInfoDao.selectByUsername(userName);
    }

    @Override
    public UserInfo selectDetailByUsername(String userName) {
        return userInfoDao.selectDetailByUsername(userName);
    }

    @Override
    public PageList<ExecUser> findExecUserPage(Integer appUserId, PageQuery query) {
        Long count = userExecUserDao.count(appUserId);
        int currentPage = query.getPage();
        int pageSize = query.getPageSize();
        PageList<ExecUser> page = new PageList<>(new Paginator(currentPage, pageSize, count.intValue()));
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<ExecUser> result = userExecUserDao.findPageByAppUserId(appUserId, query, new RowBounds(offset, pageSize));
        page.setData(result);
        return page;
    }

    @Override
    public void bindExecUser(String appUser, String execUser) {
        UserExecUser userExecUser = new UserExecUser(appUser, execUser);
        userExecUserDao.addOne(userExecUser);
    }

    @Override
    public void unbindExecNode(String appUser, String execUser) {
        UserExecUser userExecUser = new UserExecUser(appUser, execUser);
        userExecUserDao.deleteOne(userExecUser);
    }

    @Override
    public PageList<ExecutorNode> findExecNodePage(Integer appUserId, PageQuery query) {
        Long count = userExecNodeDao.count(appUserId);
        int currentPage = query.getPage();
        int pageSize = query.getPageSize();
        PageList<ExecutorNode> page = new PageList<>(new Paginator(currentPage, pageSize, count.intValue()));
        int offset = currentPage > 0 ? (currentPage - 1) * pageSize : 0;
        List<ExecutorNode> result = userExecNodeDao.findPageByUserId(appUserId, query, new RowBounds(offset, pageSize));
        page.setData(result);
        return page;
    }

    @Override
    public void bindExecNode(String appUser, Integer nodeId) {
        UserExecNode userExecNode = new UserExecNode(appUser, nodeId);
        userExecNodeDao.addOne(userExecNode);
    }

    @Override
    public void unbindExecNode(String appUser, Integer nodeId) {
        UserExecNode userExecNode = new UserExecNode(appUser, nodeId);
        userExecNodeDao.deleteOne(userExecNode);
    }

    @Override
    public boolean hasBoundExecUser(String appUser) {
        Integer result = userExecUserDao.existsAppUser(appUser);
        return result != null && result > 0;
    }

    @Override
    public boolean hasBoundExecNode(String appUser) {
        Integer result = userExecNodeDao.existsAppUser(appUser);
        return result != null && result > 0;
    }

    @Override
    public boolean resetPassword(Integer id, String password) {
        UserInfo userInfo = userInfoDao.selectOne(id);
        if(null != userInfo) {
            String userName = userInfo.getUserName();
            String encryptedPassword = CryptoUtils.md5(password, userName, 2);
            int affect = userInfoDao.resetPassword(id, encryptedPassword);
            return affect > 0;
        }
        return false;
    }

    @Override
    public UserInfo createUser(String username) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(username);
        userInfo.setPassword(generatePassword(PASSWORD_MIN_LENGTH));
        userInfo.setUserType(0);
        userInfo.setCreateTime(new Date());
        userInfo.setUpdateTime(new Date());
        if (add(userInfo)) {
            return userInfo;
        }
        return null;
    }

    public String generatePassword(int pwdLength) {
        List<String> charSetList = new ArrayList<>();
        charSetList.add("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        charSetList.add("1234567890");
        charSetList.add("~!@#$%^&*.?");

        Integer[] indexArray = new Integer[pwdLength];
        Arrays.fill(indexArray, 0);
        indexArray[0] = 1;
        indexArray[1] = 1;
        indexArray[2] = 2;
        List<Integer> index = Arrays.asList(indexArray);
        Collections.shuffle(index);

        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < index.size(); i++) {
            String charSet = charSetList.get(index.get(i));
            password.append(charSet.charAt(random.nextInt(charSet.length())));
        }

        return password.toString();
    }


}
