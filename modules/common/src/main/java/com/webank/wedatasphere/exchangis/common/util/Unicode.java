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

package com.webank.wedatasphere.exchangis.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author davidhua
 * 2019/4/13
 */
public class Unicode {
    public static String unicodeToString(String unicode){
        if(StringUtils.isEmpty(unicode)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while((i = unicode.indexOf("\\u", pos)) != -1){
            sb.append(unicode.substring(pos, i));
            if(i + 5 < unicode.length()){
                pos = i + 6;
                sb.append((char)Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }
        sb.append(unicode.substring(pos));
        return sb.toString();
    }

    public static String stringToUnicode(String string){
        if(StringUtils.isEmpty(string)){
            return null;
        }
        char[] bytes = string.toCharArray();
        StringBuilder unicode = new StringBuilder();
        for(int i = 0; i < bytes.length; i++){
            String hexString = Integer.toHexString(bytes[i]);
            unicode.append("\\u");
            if(hexString.length() < 4){
                unicode.append("0000", hexString.length(), 4);
            }
            unicode.append(hexString);
        }
        return unicode.toString();
    }
}
