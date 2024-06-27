package com.webank.wedatasphere.exchangis.job.utils;

import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils to data column
 */
public class ColumnDefineUtils {
    /**
     * Pattern of decimal column
     */
    public static final Pattern DECIMAL_PATTERN = Pattern.compile("^decimal[(（](\\d+)[^,]*?,[^,]*?(\\d+)[)）]$");

    /**
     * Get data column
     * @param name column name
     * @param type column type
     * @param index index
     * @return data column
     */
    public static SubExchangisJob.ColumnDefine getColumn(String name, String type, Integer index){
        if (StringUtils.isNotBlank(type)) {
            Matcher decimalMatch = DECIMAL_PATTERN.matcher(type.toLowerCase());
            if (decimalMatch.matches()) {
                int precision = Integer.parseInt(decimalMatch.group(1));
                int scale = Integer.parseInt(decimalMatch.group(2));
                return new SubExchangisJob.DecimalColumnDefine(name, type, index, precision, scale);
            }
        }
        return new SubExchangisJob.ColumnDefine(name, type, index);
    }

    public static SubExchangisJob.ColumnDefine getColumn(String name, String type){
        return getColumn(name, type, null);
    }
}
