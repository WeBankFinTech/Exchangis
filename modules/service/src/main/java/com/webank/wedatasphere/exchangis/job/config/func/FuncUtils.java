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

package com.webank.wedatasphere.exchangis.job.config.func;

import com.webank.wedatasphere.exchangis.job.config.dto.Transform;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function utils
 * @author enjoyyin
 * 2018/10/30
 */
public class FuncUtils {
    private static final Pattern FUNC = Pattern.compile("^([\\s\\S]+)\\(([\\s\\S]*)?\\)$");

    public static Transform parseVerifyFuncToTransform(int columnIndex, String verifyFunc, Map<String, String> verifyFuncRefName){
        Matcher matcher = FUNC.matcher(verifyFunc.trim());
        if(matcher.find()){
            Transform transform = new Transform();
            Transform.Parameter parameter = new Transform.Parameter();
            parameter.setColumnIndex(columnIndex);
            String code = matcher.group(1);
            if(verifyFuncRefName.containsKey(code)){
                transform.setName(verifyFuncRefName.get(code));
                String value = matcher.group(2);
                try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage());
                }
                parameter.setParas(Arrays.asList(code, value));
                transform.setParameter(parameter);
                return transform;
            }
        }
        return null;
    }

    public static Transform parseTransformFunc(int columnIndex, String transformFunc, List<String> functionNames){
        Matcher matcher = FUNC.matcher(transformFunc.trim());
        if(matcher.find()){
            Transform transform = new Transform();
            String name = matcher.group(1);
            if(!functionNames.contains(name)){
                return null;
            }
            Transform.Parameter parameter = new Transform.Parameter();
            parameter.setColumnIndex(columnIndex);
            String funcParams = matcher.group(2);
            String[] funcParamArray = funcParams.split(",");
            try {
                for (int i = 0; i < funcParamArray.length; i++) {
                    funcParamArray[i] = URLDecoder.decode(funcParamArray[i], "UTF-8");
                }
            }catch (UnsupportedEncodingException e){
                throw new RuntimeException(e.getMessage());
            }
            transform.setName(name);
            parameter.setParas(Arrays.asList(funcParamArray));
            transform.setParameter(parameter);
            return transform;
        }
        return null;
    }
}
