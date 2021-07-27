package com.webank.wedatasphere.exchangis.datasource.loader.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

public class NoSuchExchangisExtDataSourceException extends ErrorException {
    public NoSuchExchangisExtDataSourceException(String errDecs) {
        super(70059, errDecs);
    }

    public NoSuchExchangisExtDataSourceException(int errCode, String desc) {
        super(errCode, desc);
    }
}
