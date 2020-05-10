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

package com.webank.wedatasphere.exchangis.i18n;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


/**
 * @author davidhua
 */
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    private static final Logger logger = LoggerFactory.getLogger(CustomLocaleResolver.class);

    private static final String LANG = "Accept-Language";
    private static final String SPLIT_UNDERLINE = "_";
    private static final String SPLIT_LINE = "-";
    private static final String SPLIT_SPOT = ",";

    private String localeHeader;

    public CustomLocaleResolver(String localeHeader){
        this.localeHeader = localeHeader;
    }
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getHeader(localeHeader);
        Locale locale = Locale.getDefault();
        if(StringUtils.isNotBlank(lang)){
            if(lang.contains(SPLIT_SPOT)){
                String str = lang.split(SPLIT_SPOT)[0];
                locale = splitStr(str);
            }else{
                locale = splitStr(lang);
            }
        }
        return locale;
    }

    private Locale splitStr(String str){
        Locale locale;
        if(str.contains(SPLIT_UNDERLINE)){
            String[] language = str.split(SPLIT_UNDERLINE);
            locale = new Locale(language[0],language[1]);
        }else if(str.contains(SPLIT_LINE)){
            String[] language = str.split(SPLIT_LINE);
            locale = new Locale(language[0],language[1]);
        }else {
            locale = new Locale(str);
        }
        return locale;
    }
}
