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

package com.webank.wedatasphere.exchangis.datasource.conns;

import com.webank.wedatasphere.exchangis.common.exceptions.DataAccessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author davidhua
 * 2018/9/14
 */
public class UserGroupInformationWrapper {
    private static ReentrantLock lock = new ReentrantLock();

    public static UserGroupInformation loginUserFromKeytab(final Configuration conf, String  user, String path) throws Exception {
        lock.lock();
        try{
            UserGroupInformation.setConfiguration(conf);
            return UserGroupInformation.loginUserFromKeytabAndReturnUGI(user, path);
        }catch(Exception e){
            String message = e.getMessage();
            if(StringUtils.isNotBlank(message)){
                //Avoid path information in message
                String[] messageArray = message.split(" ");
                for(int i = 0; i < messageArray.length; i++){
                    if(messageArray[i].contains(path)){
                        messageArray[i] = "";
                    }
                }
                message = StringUtils.join(messageArray, " ");
            }
            throw new DataAccessException(message, e);
        }finally{
            lock.unlock();
        }
    }
    public static UserGroupInformation createProxyUser(final Configuration conf, String user) throws Exception{
        lock.lock();
        try{
            UserGroupInformation.setLoginUser(null);
            UserGroupInformation.setConfiguration(conf);
            return UserGroupInformation.createProxyUser(user, UserGroupInformation.getLoginUser());
        }finally{
            lock.unlock();
        }
    }

    public static UserGroupInformation createRemoteUser(final Configuration conf, String user) throws Exception{
        lock.lock();
        try{
            UserGroupInformation.setLoginUser(null);
            UserGroupInformation.setConfiguration(conf);
            return UserGroupInformation.createRemoteUser(user);
        }finally{
            lock.unlock();
        }
    }
    public static void setConfiguration(Configuration conf, Callable execute) throws Exception{
        lock.lock();
        try{
            UserGroupInformation.setConfiguration(conf);
            execute.call();
        }finally{
            lock.unlock();
        }
    }

}
