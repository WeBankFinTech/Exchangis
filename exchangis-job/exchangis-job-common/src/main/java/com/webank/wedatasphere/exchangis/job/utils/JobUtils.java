package com.webank.wedatasphere.exchangis.job.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.common.util.json.Json;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.VariableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author jefftlin
 * @date 2024/8/15
 */
public class JobUtils {

    private static Logger logger = LoggerFactory.getLogger(JobUtils.class);

    private static final String MARKER_HEAD = "r";

    protected static final ObjectMapper mapper = new ObjectMapper();

    public static List<ExchangisJobInfoContent> parseJobContent(String content) {
        List<ExchangisJobInfoContent> jobInfoContents;
        if (Strings.isNullOrEmpty(content)) {
            jobInfoContents = new ArrayList<>();
        } else {
            jobInfoContents = Json.fromJson(content, List.class, ExchangisJobInfoContent.class);
        }
        return jobInfoContents;
    }

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

    public static String renderDt(String template, long time){
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
                boolean spec = false;
                if (null != symbol) {
                    for(String specSymbol : DateTool.HOUR_SPEC_SYMBOLS){
                        if(specSymbol.equals(symbol)){
                            spec = true;
                            break;
                        }
                    }
                    if(!spec) {
                        if (DateTool.MONTH_BEGIN_SYMBOL.equals(symbol)) {
                            dataTool.getMonthBegin(0);
                        } else if (DateTool.MONTH_END_SYMBOL.equals(symbol)) {
                            dataTool.getMonthEnd(0);
                        } else {
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
        return replaceVariable(source, variables, Calendar.getInstance().getTimeInMillis());
    }

    public static String replaceVariable(String source, Map<String, Object> variables, long time){
        String result = source;
        if (StringUtils.isNotBlank(result)){
            result = VariableUtils.replace(MARKER_HEAD + source, variables).substring(MARKER_HEAD.length());
            if (StringUtils.isNotBlank(result)){
                // Render again
                result = renderDt(result, time);
            }
        }
        return result;
    }
}
