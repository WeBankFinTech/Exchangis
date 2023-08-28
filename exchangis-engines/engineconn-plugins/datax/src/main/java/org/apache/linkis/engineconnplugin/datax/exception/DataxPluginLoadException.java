package org.apache.linkis.engineconnplugin.datax.exception;

import org.apache.linkis.common.exception.ErrorException;

/**
 * Plugin load exception
 */
public class DataxPluginLoadException extends ErrorException {

    public static final int ERROR_CODE = 16022;

    public DataxPluginLoadException(String message, String desc) {
        super(ERROR_CODE, message);

    }
}
