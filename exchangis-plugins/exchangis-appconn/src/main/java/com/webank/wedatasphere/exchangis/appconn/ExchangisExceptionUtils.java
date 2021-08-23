package com.webank.wedatasphere.exchangis.appconn;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.lang.reflect.Constructor;

public class ExchangisExceptionUtils {
    public static <T extends ErrorException> void dealErrorException(int errorCode, String errorDesc, Throwable throwable, Class<T> clazz) throws T {
        ErrorException errorException1;
        T errorException = null;
        try {
            Constructor<T> constructor = clazz.getConstructor(new Class[] { int.class, String.class, Throwable.class });
            errorException1 = constructor.newInstance(new Object[] { Integer.valueOf(errorCode), errorDesc, throwable });
            errorException1.setErrCode(errorCode);
            errorException1.setDesc(errorDesc);
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to instance %s", new Object[] { clazz.getName() }), e);
        }
        errorException1.initCause(throwable);
        throw (T)errorException1;
    }

    public static <T extends ErrorException> void dealErrorException(int errorCode, String errorDesc, Class<T> clazz) throws T {
        ErrorException errorException1;
        T errorException = null;
        try {
            Constructor<T> constructor = clazz.getConstructor(new Class[] { int.class, String.class });
            errorException1 = constructor.newInstance(new Object[] { Integer.valueOf(errorCode), errorDesc });
            errorException1.setErrCode(errorCode);
            errorException1.setDesc(errorDesc);
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to instance %s", new Object[] { clazz.getName() }), e);
        }
        throw (T)errorException1;
    }
}
