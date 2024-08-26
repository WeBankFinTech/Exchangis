package com.webank.wedatasphere.exchangis.common.util;

import com.webank.wedatasphere.exchangis.common.util.json.Json;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternInjectUtils {

    private PatternInjectUtils(){

    }

    private static final String PARAMETER_PREFIX = "[#|$]";

    private static final String ASSIGN_SYMBOL = "=";

    private static final Pattern REGEX = Pattern.compile(
            "(" + ASSIGN_SYMBOL + "?)" +
                   "("+ PARAMETER_PREFIX + ")" +
                    "\\{([\\w-.]+)[|]?([^}]*)}?"
    );

    /**
     * Inject pattern
     * @param template
     * @param params
     * @param useDefault use default Value
     * @return
     */
    public static String inject(String template, Object[] params, boolean useDefault, boolean escape, boolean placeholder){
        Matcher matcher = REGEX.matcher(template);
        StringBuffer sb = new StringBuffer();
        int offset = 0;
        while(matcher.find()){
            String value = "";
            String extra = "";
            if( offset < params.length && null != params[offset] ){
                Object paramsV = params[offset];
                if( paramsV instanceof  String || paramsV instanceof Enum ||
                        paramsV.getClass().isPrimitive() ||
                        isWrapClass(paramsV.getClass())){
                    value = escape? StringEscapeUtils.escapeJava(String.valueOf(paramsV))
                        : String.valueOf(paramsV);
                }else{
                    //For default not to escape
                    value = "$".equals(matcher.group(2)) ?
                            Json.toJson(paramsV, null) :
                            StringEscapeUtils.escapeJava(Json.toJson(paramsV, null));
                }
                if( null != matcher.group(1)
                        && !"".equals(matcher.group(1))){
                    extra = matcher.group(1);
                }
            }else if(null != matcher.group(4) && useDefault){
                //For default not to escape
                value = matcher.group(4);
            }
            offset ++;
            if(StringUtils.isBlank(value) && !useDefault){
                value = "\"*#{" + (escape?StringEscapeUtils.escapeJava(matcher.group(3))
                        : matcher.group(3)) + "}*\"";
            }else if(!"$".equals(matcher.group(2)) && placeholder){
                value = "\"" + StringEscapeUtils.escapeJava(value) + "\"";
            }
            String result = (extra + value).replace("$", "\\$");
            matcher.appendReplacement(sb, result);
        }
        matcher.appendTail(sb);
        return sb.toString().replace("\\$","$");
    }
    public static String inject(String pattern, Object[] params){
        return inject(pattern, params, true, true, true);
    }

    /**
     * Inject pattern
     * @param template
     * @param params
     * @param useDefault
     * @return
     */
    public static String inject(String template, Map<String, Object> params, boolean useDefault, boolean escape, boolean placeholder){
        Matcher matcher = REGEX.matcher(template);
        StringBuffer sb = new StringBuffer();
        //will be more faster?
        while(matcher.find()){
            String injected = matcher.group(3);
            if(null !=  injected  && !"".equals(injected)){
                int flag = 0;
                String value = "";
                String extra = "";
                for(Map.Entry<String, Object> entry : params.entrySet()){
                    if(injected.equals(entry.getKey()) && null != entry.getValue()){
                        Object entryV = entry.getValue();
                        if(entryV instanceof  String || entryV instanceof Enum ||
                                entryV.getClass().isPrimitive() ||
                                isWrapClass(entryV.getClass())){
                            value = escape? StringEscapeUtils.escapeJava(String.valueOf(entryV))
                            : String.valueOf(entryV);
                        }else{
                            //For default not to escape
                            value = "$".equals(matcher.group(2)) ?
                                    Json.toJson(entryV, null) :
                                    StringEscapeUtils.escapeJava(Json.toJson(entryV, null));
                        }
                        if(null != matcher.group(1)
                                || !"".equals(matcher.group(1))){
                            extra = matcher.group(1);
                        }
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0 && null != matcher.group(4) && useDefault){
//                value = escape? StringEscapeUtils.escapeJava(String.valueOf(matcher.group(4)))
//                        : matcher.group(4);
                    //For default not to escape
                    value = matcher.group(4);
                }
                if(StringUtils.isBlank(value) && !useDefault){
                    value = "\"*#{" + (escape?StringEscapeUtils.escapeJava(matcher.group(3))
                            : matcher.group(3)) + "}*\"";
                } else if(!"$".equals(matcher.group(2)) && placeholder){
                    value = "\"" + StringEscapeUtils.escapeJava(value) + "\"";
                }
                String result = (extra + value).replace("$", "\\$");
                matcher.appendReplacement(sb, result);
            }
        }
        matcher.appendTail(sb);
        String print = sb.toString();
        return print.replace("\\$","$").replace("","");
    }

    public static String injectPattern(String template, String valuePattern){
        Matcher matcher = REGEX.matcher(template);
        StringBuffer sb  = new StringBuffer();
        while(matcher.find()){
            String extra = matcher.group(1);
            String value = StringEscapeUtils.escapeJava(matcher.group(3));
            value = (extra + value.replaceAll("[\\s\\S]+", valuePattern))
                    .replace("$", "\\$");
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString().replace("\\$", "$");
    }

    public static String inject(String template, Map<String, Object> params){
        return inject(template, params, true, true, true);
    }


    private static boolean isWrapClass(Class<?> clz){
        try{
            return ((Class<?>)clz.getField("TYPE").get(null)).isPrimitive();
        }catch(Exception e){
            return false;
        }
    }

    public static void main(String[] args) {
        String input = "\"--input-null-non-string\":{\n" +
                "        \"@require\": false,\n" +
                "        \"@value\": \"\\'{nullFormat}\\'\",\n" +
                "        \"params\":{\n" +
                "            \"nullFormat\": #{nullFormat|\\\\N}\n" +
                "        }\n" +
                "    },";
        System.out.println(inject(input, new String[]{"\\N"}));
        System.out.println(StringEscapeUtils.escapeJava("\u0001"));
        char i = '\t';
        System.out.println(Integer.toHexString((i)));
        System.out.println(StringEscapeUtils.escapeJava(String.valueOf((char)Integer.parseInt("009", 16))));

        System.out.println(StringUtils.containsWhitespace("\\N") || !StringUtils.isAsciiPrintable("\\N"));
    }
}
