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

package com.webank.wedatasphere.exchangis.job.config;

import com.webank.wedatasphere.exchangis.job.config.builder.JobConfBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author enjoyyin
 * 2018/11/22
 */
public abstract class AbstractJobTemplate {

    public static final String IGNORE_EMPTY_KEY_SIGN = "@";
    protected static final String COMMON_TEMPLATE_FILE = "exchangis.tpl";
    protected static final String READER_DIRECTORY = "/readers";
    protected static final String WRITER_DIRECTORY = "/writers";
    protected static final String DEFAULT_SUFFIX = ".tpl";

    /**
     * //TODO cache the template
     * @param location
     * @return
     */
    protected String getTemplateContent(String location){
        return getTemplateContent(location, false);
    }

    protected String getTemplateContent(String location, boolean includeLineBreak){
        String lineBreak = includeLineBreak?"\n":"";
        InputStream inputStream = JobConfBuilder.class.getResourceAsStream(location);
        if(null != inputStream) {
            StringBuilder buffer = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String tmp = null;
                while ((tmp = reader.readLine()) != null) {
                    if(!includeLineBreak){
                        tmp = tmp.trim();
                    }
                    buffer.append(tmp).append(lineBreak);
                }
                return buffer.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

}
