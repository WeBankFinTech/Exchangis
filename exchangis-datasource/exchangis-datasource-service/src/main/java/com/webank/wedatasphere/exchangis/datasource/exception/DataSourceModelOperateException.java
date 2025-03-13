package com.webank.wedatasphere.exchangis.datasource.exception;

import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import org.apache.linkis.common.exception.ErrorException;

public class DataSourceModelOperateException extends ErrorException {

    public DataSourceModelOperateException(String desc) {
        super(ExchangisDataSourceExceptionCode.DATA_SOURCE_MODEL_OPERATE_ERROR.getCode(), desc);
    }

    public DataSourceModelOperateException(String desc, Throwable cause) {
        super(ExchangisDataSourceExceptionCode.DATA_SOURCE_MODEL_OPERATE_ERROR.getCode(), desc);
        this.initCause(cause);
    }
}
