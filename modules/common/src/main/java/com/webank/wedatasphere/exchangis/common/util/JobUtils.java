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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by devendeng on 2018/10/22.
 */
public class JobUtils {
    private static Logger logger = LoggerFactory.getLogger(JobUtils.class);

    /**
     * Replace the parameter in variables
     * @param map
     * @param key
     * @param subValue
     */
    private static void replaceParameters(Map<String, String> map, String key,
                                          StringBuffer subValue) {
        StringBuilder subKey = new StringBuilder();
        char[] chars = key.toCharArray();
        int i=0;
        boolean isParamter = false;
        int count = 0;
        while(i<chars.length){
            if(chars[i] == '$' && chars[i + 1] == '{'){
                count = 0;
                isParamter = true;
                if(count > 0){
                    subKey.append(chars[i]).append(chars[i+1]);
                }
                count++;
                i=i+2;
                continue;
            }else if(chars[i] == '}'){
                count--;
                if(count == 0){
                    String parameter = subKey.toString();
                    if(parameter.contains("${") && parameter.contains("}")){
                        StringBuffer sb = new StringBuffer();
                        replaceParameters(map, parameter, sb);
                        parameter = sb.toString();
                    }
                    String v = map.get(parameter);
                    isParamter = false;
                    subKey.delete(0, subKey.length());
                    if(null != v && !"".equals(v)){
                        if(v.contains("${") && v.contains("}")){
                            replaceParameters(map, v, subValue);
                        }else{
                            subValue.append(v);
                        }
                    }else{
                        subValue.append("${").append(parameter).append("}");
                    }
                    i=i+1;
                    continue;
                }

            }
            if(isParamter){
                subKey.append(chars[i]);
            }else{
                subValue.append(chars[i]);
            }
            i=i+1;
        }
    }

    public static String render(String template, long time){
        DateTool tool = new DateTool();
        Map<String, Object> map = new HashMap<>(2);
        template = renderDt(template, time);
        map.put("yyyyMMdd", tool.format("yyyyMMdd", time));
        map.put("yyyy-MM-dd", tool.format("yyyy-MM-dd", time));
        map.put("timestamp", tool.currentTimestamp());
        return PatternInjectUtils.inject(template, map, true, true, false);
    }

    public static String render(String template){
        Calendar calendar=Calendar.getInstance();
        return render(template, calendar.getTimeInMillis());
    }

    private static String renderDt(String template, long time){
        if(template==null){
            return null;
        }
        Matcher matcher= DateTool.TIME_REGULAR_PATTERN.matcher(template);
        while(matcher.find()){
            try {
                String m = template.substring(matcher.start(), matcher.end());
                StringWriter sw = new StringWriter();
                DateTool dataTool = new DateTool(time);
                String symbol = matcher.group(1);
                if (null != symbol) {
                    if (DateTool.MONTH_BEGIN_SYMBOL.equals(symbol)) {
                        dataTool.getMonthBegin(0);
                    } else if (DateTool.MONTH_END_SYMBOL.equals(symbol)) {
                        dataTool.getMonthEnd(0);
                    } else{
                        dataTool.addDay(-1);
                    }
                }
                String calculate = matcher.group(3);
                String number = matcher.group(4);
                if (null != calculate && null != number) {
                    if ("+".equals(calculate)) {
                        dataTool.addDay(Integer.parseInt(number));
                    } else if ("-".equals(calculate)) {
                        dataTool.addDay(-Integer.parseInt(number));
                    }
                }
                String formatSymbol = matcher.group(2);
                if(DateTool.LINE_SYMBOL.equals(formatSymbol)){
                    sw.append(dataTool.format("yyyy-MM-dd"));
                }else{
                    sw.append(dataTool.format("yyyyMMdd"));
                }
                template=template.replace(m, sw.toString());
                matcher= DateTool.TIME_REGULAR_PATTERN.matcher(template);
            }catch(Exception e){
                logger.error("TASK_ERROR, cannot render job's configuration, message: {}", e.getMessage(), e);
                break;
            }
        }
        //${yesterday}
        return template.replace("${yesterday}",new DateTool(time).addDay(-1).format("yyyyMMdd"));
    }

}
