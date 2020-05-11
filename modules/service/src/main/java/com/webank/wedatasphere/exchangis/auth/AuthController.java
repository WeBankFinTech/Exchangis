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

package com.webank.wedatasphere.exchangis.auth;

import com.webank.wedatasphere.exchangis.auth.domain.UserPasswordToken;
import com.webank.wedatasphere.exchangis.common.auth.AuthConfiguration;
import com.webank.wedatasphere.exchangis.common.constant.CodeConstant;
import com.webank.wedatasphere.exchangis.common.controller.ExceptionResolverContext;
import com.webank.wedatasphere.exchangis.common.controller.Response;
import com.webank.wedatasphere.exchangis.common.auth.AuthConstraints;
import com.webank.wedatasphere.exchangis.common.auth.AuthTokenHelper;
import com.webank.wedatasphere.exchangis.common.util.CryptoUtils;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.common.util.spring.AppUtil;
import com.webank.wedatasphere.exchangis.user.domain.UserInfo;
import com.webank.wedatasphere.exchangis.user.service.UserInfoService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author davidhua
 * 2018/10/17
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController extends ExceptionResolverContext {
    private static Logger LOG = LoggerFactory.getLogger(AuthController.class);
    @Resource
    private AuthTokenHelper authTokenHelper;

    @Resource
    private AuthConfiguration authConfiguration;

    @Resource
    private UserInfoService userInfoService;

    private long secretFileModifyTime = -1;

    private String authSecretContent = null;

    /**
     * Basic user info
     * @param request
     * @return
     */
    @RequestMapping(value="", method=RequestMethod.GET)
    public Response<Object> response(HttpServletRequest request){
        String token = AppUtil.getCookieValue(request, AuthConstraints.DEFAULT_SSO_COOKIE);
        Map<String, String> message = new HashMap<>(2^4);
        if(null != token){
            message = authTokenHelper.getAuthMessage(token);
        }
        String username = message.get("loginid");
        UserInfo user = userInfoService.selectByUsername(username);
        if(null != user) {
            message.put("role", user.getRole());
            LOG.info(Json.toJson(message, null));
        }
        return new Response<>().successResponse(message);
    }

    /**
     * Login entrance
     */
    @RequestMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response<Object> login(@Valid @RequestBody UserPasswordToken token,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserInfo userInfo = userInfoService.selectDetailByUsername(token.getLoginUser());
        if(null != userInfo){
            String storedPassword = userInfo.getPassword();
            String loginPwd = token.getLoginPwd();
            //Decrypt login password
            String authSecret = authConfiguration.authSecret();
            if(StringUtils.isNotBlank(authSecret)){
                URL url = this.getClass().getClassLoader().getResource(authSecret);
                if(null != url && new File(url.getPath()).lastModified() > secretFileModifyTime){
                    authSecretContent = IOUtils.toString(url);
                    secretFileModifyTime = new File(url.getPath()).lastModified();
                }
            }
            if(StringUtils.isNotBlank(authSecretContent)){
                try {
                    loginPwd = CryptoUtils.decryptRSA(loginPwd, authSecretContent);
                }catch(Exception e){
                    LOG.error("Fail to decrypt password", e);
                }
            }
            String encrypted = CryptoUtils.md5(loginPwd, token.getLoginUser(), 2);
            if(encrypted.equals(storedPassword)){
                String userName = userInfo.getUserName();
                //Set userName, password and id to null
                userInfo.setUserName(null);
                userInfo.setPassword(null);
                userInfo.setId(null);
                Map<String, String> jsonMap = Json.fromJson(Json.toJson(userInfo, null), Map.class);
                assert jsonMap != null;
                jsonMap.put(AuthConstraints.X_AUTH_ID, userName);
                return new Response<>().successResponse(jsonMap);
            }
        }
        return new Response<>().errorResponse(CodeConstant.LOGIN_FAIL, null,
                this.informationSwitch("exchange.auth.login.fail"));
    }
}
