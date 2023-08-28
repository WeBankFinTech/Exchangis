package com.webank.wedatasphere.exchangis.job.server.builder.transform.handlers;


import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.builder.JobParamConstraints;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

/**
 * With authentication
 */
public abstract class AuthEnabledSubExchangisJobHandler extends AbstractLoggingSubExchangisJobHandler{
    /**
     * Disable encrypt
     */
    protected static final JobParamDefine<Boolean> ENCRYPT_DISABLE = JobParams.define("encrypt.disable");

    /**
     * User name
     */
    protected static final JobParamDefine<String> USERNAME = JobParams.define(JobParamConstraints.USERNAME);

    /**
     * Password
     */
    protected static final JobParamDefine<String> PASSWORD = JobParams.define(JobParamConstraints.PASSWORD, paramSet -> {
        JobParam<String> password = paramSet.get(JobParamConstraints.PASSWORD);
        if (Objects.nonNull(password) && StringUtils.isNotBlank(password.getValue())) {
            Boolean encrypt = ENCRYPT_DISABLE.getValue(paramSet);
            if (Objects.isNull(encrypt) || !encrypt) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                        oos.writeObject(password.getValue());
                        oos.flush();
                    }
                    return new String(new Base64().encode(bos.toByteArray()));
                } catch (Exception e) {
                    throw new ExchangisJobException.Runtime(-1, "Fail to encrypt password", e);
                }
            }
            return password.getValue();
        }
        return null;
    });
}
