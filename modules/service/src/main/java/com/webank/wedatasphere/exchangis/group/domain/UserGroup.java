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

package com.webank.wedatasphere.exchangis.group.domain;


import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Created by devendeng on 2018/8/23.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserGroup {

    /**
     * Role
     */
    public enum JoinRole{
        MEMBER(0), CREATOR(1);
        private int code;
        JoinRole(int code){
            this.code = code;
        }
        public int code(){
            return this.code;
        }
    }
    /**
     * Username
     */
    @NotBlank(message = "{udes.domain.userInfo.name.notBlank}")
    @Size(max = 200, message = "{udes.domain.userInfo.name.max}")
    private String userName;

    /**
     * ID of user
     */
    private Integer userId;

    /**
     * ID of group
     */
    private Long groupId;

    /**
     * The code of role
     */
    private int roleCode = JoinRole.MEMBER.code;
    public UserGroup(){

    }

    public UserGroup(String userName, Long groupId){
        this.userName = userName;
        this.groupId = groupId;
    }

    public UserGroup(String userName, JoinRole role, Long groupId){
        this.userName = userName;
        this.roleCode = role.code;
        this.groupId = groupId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(int roleCode) {
        this.roleCode = roleCode;
    }
}
