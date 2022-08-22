package org.apache.linkis.engineconnplugin.datax.utils;

import com.alibaba.datax.common.util.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Security utils
 */
public class SecretUtils {
    /**
     * Extracted from 'Engine' class
     */
    public static Configuration filterSensitiveConfiguration(Configuration configuration){
        Set<String> keys = configuration.getKeys();
        String[] sensitiveSuffixes = new String[]{"password", "accessKey", "path"};
        for (final String key : keys) {
            boolean isSensitive = false;
            for(String suffix : sensitiveSuffixes){
                if(StringUtils.endsWithIgnoreCase(key, suffix)){
                    isSensitive = true;
                    break;
                }
            }
            if (isSensitive && configuration.get(key) instanceof String) {
                configuration.set(key, configuration.getString(key).replaceAll("[\\s\\S]", "*"));
            }
        }
        return configuration;
    }
}
