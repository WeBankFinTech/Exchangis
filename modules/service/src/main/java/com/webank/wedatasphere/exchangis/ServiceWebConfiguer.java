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

package com.webank.wedatasphere.exchangis;

import com.webank.wedatasphere.exchangis.auth.interceptors.UserInfoSyncInterceptor;
import com.webank.wedatasphere.exchangis.auth.interceptors.UserRoleInterceptor;
import com.webank.wedatasphere.exchangis.common.auth.interceptors.AuthenticationInterceptor;
import com.webank.wedatasphere.exchangis.common.auth.interceptors.AuthorityInterceptor;
import com.webank.wedatasphere.exchangis.http.converters.CustomHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author davidhua
 * 2019/6/27
 */
@Configuration
public class ServiceWebConfiguer implements WebMvcConfigurer {

    @Resource
    private AuthenticationInterceptor authenticationInterceptor;

    @Resource
    private AuthorityInterceptor authorityInterceptor;

    @Resource
    private UserInfoSyncInterceptor userInfoSyncInterceptor;

    @Resource
    private UserRoleInterceptor userRoleInterceptor;

    @Resource
    private CustomHttpMessageConverter customHttpMessageConverter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).order(1).addPathPatterns("/**");
        registry.addInterceptor(authorityInterceptor).order(2).addPathPatterns("/**");
        registry.addInterceptor(userInfoSyncInterceptor).order(3).addPathPatterns("/**");
        registry.addInterceptor(userRoleInterceptor).order(4).addPathPatterns("/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customHttpMessageConverter);
    }
}
