package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Field camelCase match strategy
 */
public class FieldCamelCaseMatchStrategy extends AbstractFieldMatchStrategy{

    public static final String CAMEL_CASE_MATCH = "CAMEL_CASE_MATCH";
    @Override
    protected FieldColumn match(FieldColumn dependColumn, Map<String, FieldColumn> searchColumns) {
        String columName = dependColumn.getName();
        // First to search by full name
        FieldColumn matchColumn = searchColumns.get(columName);
        if (Objects.isNull(matchColumn)){
            matchColumn = searchColumns.get(camelToUnderLine(columName));
            if (Objects.isNull(matchColumn)){
                matchColumn = searchColumns.get(underLineToCamel(columName));
            }
        }
        return matchColumn;
    }

    @Override
    public String name() {
        return CAMEL_CASE_MATCH;
    }

    /**
     * Camel string to underline string
     * @param param param
     * @return underline string
     */
    private String camelToUnderLine(String param){
        if (StringUtils.isNotBlank(param)){
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i ++){
                if (Character.isUpperCase(param.charAt(i)) && i > 0){
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(param.charAt(i)));
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Underline string to camel string
     * @param param param
     * @return camel string
     */
    private String underLineToCamel(String param){
        if (StringUtils.isNotBlank(param)){
            String temp = param.toLowerCase(Locale.ROOT);
            int len = temp.length();
            StringBuilder sb = new StringBuilder(len);
            for(int i = 0; i < len; ++i) {
                char c = temp.charAt(i);
                if (c == '_') {
                    ++i;
                    if (i < len) {
                        sb.append(Character.toUpperCase(temp.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }
}
