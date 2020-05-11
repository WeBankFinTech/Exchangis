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

package com.webank.wedatasphere.exchangis.auth.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Authentication token
 * @author davidhua
 * 2020/4/4
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserPasswordToken {
    @NotBlank(message = "{exchange.auth.domain.loginUser.notNull}")
    @Size(max = 50, message = "{exchange.auth.domain.loginUser.maxSize}")
    private String loginUser;

    @NotBlank(message = "{exchange.auth.domain.loginPwd.notNull}")
    private String loginPwd;

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }
}
