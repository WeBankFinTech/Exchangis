package com.webank.wedatasphere.exchangis.job.server.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.VariableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;


public class JobUtils {

    private static final String MARKER_HEAD = "r";

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


    public static String renderDt(String template, Calendar calendar){
        long time = calendar.getTimeInMillis();
        if(template==null){
            return null;
        }
        Date date =new Date();

        Matcher matcher= DateTool.TIME_REGULAR_PATTERN.matcher(template);
        while(matcher.find()){
            try {
                String m = template.substring(matcher.start(), matcher.end());
                StringWriter sw = new StringWriter();
                DateTool dataTool = new DateTool(time);
                String symbol = matcher.group(1);
                boolean spec = false;
                if (null != symbol) {
                    String startTime = null;
                    String tempTime = null;
                    for(String specSymbol : DateTool.HOUR_SPEC_SYMBOLS){
                        if(specSymbol.equals(symbol)){
                            tempTime = dataTool.format(specSymbol);
                            startTime = template.replace(m, tempTime);
                            return startTime;
                        }
                    }
                    if(!spec) {
                        if (DateTool.MONTH_BEGIN_SYMBOL.equals(symbol)) {
                            dataTool.getMonthBegin(0);
                        } else if (DateTool.MONTH_END_SYMBOL.equals(symbol)) {
                            dataTool.getMonthEnd(0);
                        } else if (DateTool.TIME_PLACEHOLDER_TIMESTAMP.equals(symbol)){
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_MONTH, 0);
                            tempTime = String.valueOf(calendar.getTimeInMillis());
                            startTime = template.replace(m, tempTime);
                            return startTime;
                        }
                        else {
                            dataTool.addDay(-1);
                        }
                    }

                }
                String calculate = matcher.group(3);
                String number = matcher.group(4);
                if (null != calculate && null != number) {
                    if ("+".equals(calculate)) {
                        if(spec){
                            dataTool.addHour(Integer.parseInt(number));
                        }else {
                            dataTool.addDay(Integer.parseInt(number));
                        }
                    } else if ("-".equals(calculate)) {
                        if(spec){
                            dataTool.addHour(-Integer.parseInt(number));
                        }else {
                            dataTool.addDay(-Integer.parseInt(number));
                        }
                    }
                }
                String formatSymbol = matcher.group(2);
                if(spec){
                    sw.append(dataTool.format(symbol));
                }else if(DateTool.FORMAT_STD_SYMBOL.equals(formatSymbol)){
                    sw.append(dataTool.format("yyyy-MM-dd"));
                }else if(DateTool.FORMAT_UTC_SYMBOL.equals(formatSymbol)) {
                    // Set the hour as the beginning of day
                    sw.append(dataTool.format("yyyy-MM-dd'T'HH:00:00.000'Z'", "UTC"));
//                    sw.append(dataTool.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "UTC"));
                } else if(StringUtils.isNotBlank(formatSymbol) && formatSymbol.startsWith("_")){
                    String format = formatSymbol.substring(1);
                    sw.append(dataTool.format(format));
                } else{
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

    /**
     * Replace source string with variable (use Linkis common module)
     * @param source source
     * @return string
     */
    public static String replaceVariable(String source, Map<String, Object> variables){
        String result = source;
        if (StringUtils.isNotBlank(result)){
            result = VariableUtils.replace(MARKER_HEAD + source, variables).substring(MARKER_HEAD.length());
            if (StringUtils.isNotBlank(result)){
                // Render again
                result = renderDt(result, Calendar.getInstance());
            }
        }
        return result;
    }
}
