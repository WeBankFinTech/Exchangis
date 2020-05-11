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

package com.webank.wedatasphere.exchangis.auth.interceptors;

import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Map;

/**
 * @author davidhua
 * 2019/4/9
 */
@Component
public class UserInfoSyncInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(UserInfoSyncInterceptor.class);
    private static final String USER_INFO_SYNC_COOKIE = "user_info_sync";

    @Resource
    private AuthConfiguration conf;

    @Resource
    private AuthTokenHelper authTokenHelper;

    @Resource
    private UserInfoService userInfoService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(conf.enable() && !request.getRequestURI().equals(conf.authLoginUrl())) {
            String token = AppUtil.getCookieValue(request, AuthConstraints.DEFAULT_SSO_COOKIE);
            Map<String, String> authMessage = authTokenHelper.getAuthMessage(token);
            String username = authMessage.get("loginid");
            //If is requested from user
            if (StringUtils.isNotBlank(username)) {
                String sync = AppUtil.getCookieValue(request, USER_INFO_SYNC_COOKIE);
                if (StringUtils.isBlank(sync) || !username.equals(sync)) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(username);
                    userInfo.setDeptCode(authMessage.getOrDefault("deptCode", ""));
                    userInfo.setOrgCode(authMessage.getOrDefault("orgCode", ""));
                    userInfo.setUpdateTime(Calendar.getInstance().getTime());
                    LOG.info("Sync user information:[" + Json.toJson(userInfo, null) +"]");
                    userInfoService.sync(userInfo);
                    Cookie cookie = new Cookie(USER_INFO_SYNC_COOKIE, username);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setSecure(false);
                    cookie.setMaxAge(86400);
                    response.addCookie(cookie);
                }
            }
        }
        return true;
    }
}
