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

package com.webank.wedatasphere.exchangis.executor.controller;

import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.executor.util.LinuxPlatFormUtil;
import com.webank.wedatasphere.exchangis.executor.domain.ExecSysUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author davidhua
 * 2019/10/28
 */
@RestController
@RequestMapping("/api/v1/executor/env")
public class SystemEnvController {
    private static final String ENV_WIND_TAG = "wind";

    private static Logger LOG = LoggerFactory.getLogger(SystemEnvController.class);

    private Pair<Integer, Integer> procUserInfo = new MutablePair<>(null, null);
    @PostConstruct
    public void init(){
        String osName = System.getProperty("os.name");
        if(StringUtils.isBlank(osName) || !osName.toLowerCase().equalsIgnoreCase(ENV_WIND_TAG)){
            procUserInfo = LinuxPlatFormUtil.currentUser();
            LOG.info("Get current process's user UID: " + procUserInfo.getLeft() + ", GID: " + procUserInfo.getRight());
        }
    }

    /**
     * Create system user
     * @param username
     * @param uid
     * @param gid
     * @return
     */
    @RequestMapping(value = "/user/{username}/{uid:\\d+}/{gid:\\d+}", method = RequestMethod.POST)
    public Response<ExecSysUser> createSysUser(@PathVariable("username")String username,
                                          @PathVariable("uid")Integer uid, @PathVariable("gid")Integer gid){
        String osName = System.getProperty("os.name");
        if(StringUtils.isNotBlank(osName) && osName.toLowerCase().equalsIgnoreCase(ENV_WIND_TAG)){
            return new Response<ExecSysUser>().errorResponse(CodeConstant.SYS_ERROR, null, "System Environment is: " + osName);
        }
        try{
            if(procUserInfo.getRight() != null){
                gid = procUserInfo.getRight();
            }
            Triple<Boolean, Integer, Integer> triple = LinuxPlatFormUtil.existUser(username);
            if(!triple.getLeft()){
                Pair<Boolean, String> pair = LinuxPlatFormUtil.createUser(username, uid, gid);
                if(!pair.getLeft()){
                    return new Response<ExecSysUser>().errorResponse(CodeConstant.SYS_ERROR, null,
                            "Cannot create user: " + username + ", uid:" + uid + ", gid:" + gid + ", " +
                                    "message:" + pair.getRight());
                }
                if(uid <= 0 || gid <= 0){
                    triple = LinuxPlatFormUtil.existUser(username);
                    if(triple.getLeft()){
                        uid = triple.getMiddle();
                        gid = triple.getRight();
                    }
                }
            }else{
                return new Response<ExecSysUser>().successResponse(new ExecSysUser(username,
                        triple.getMiddle(), triple.getRight()));
            }

            return new Response<ExecSysUser>().successResponse(new ExecSysUser(username,
                    uid, gid));
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            return new Response<ExecSysUser>().errorResponse(CodeConstant.SYS_ERROR, null, e.getMessage());
        }
    }

    /**
     * Delete system user
     * @param username
     * @return
     */
    @RequestMapping(value = "/user/{username}", method = RequestMethod.DELETE)
    public Response<Object> deleteSysUser(@PathVariable("username")String username){
        String osName = System.getProperty("os.name");
        if(StringUtils.isNotBlank(osName) && osName.toLowerCase().equalsIgnoreCase(ENV_WIND_TAG)){
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, "System Environment is: " + osName);
        }
        try {
            Triple<Boolean, Integer, Integer> triple = LinuxPlatFormUtil.existUser(username);
            if(!triple.getLeft()){
                return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, "User: " + username + " doesn't exist");
            }else if(triple.getMiddle().equals(procUserInfo.getLeft())){
                return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, "User: " + username + " is processor's user");
            }else if(!triple.getRight().equals(procUserInfo.getRight())){
                return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, "Cannot delete user: " + username +
                        ", GID: " + triple.getRight());
            }
            Pair<Boolean, String> pair = LinuxPlatFormUtil.deleteUser(username);
            if(!pair.getLeft()){
                return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null,
                        "Cannot delete user: " + username + ", message:" + pair.getRight());
            }
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            return new Response<>().errorResponse(CodeConstant.SYS_ERROR, null, e.getMessage());
        }
        return new Response<>().successResponse("success");
    }

}
