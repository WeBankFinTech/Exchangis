package com.webank.wedatasphere.exchangis.dss.appconn.operation.ref;


import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.exchangis.dss.appconn.constraints.Constraints;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.OperationStrategy;
import com.webank.wedatasphere.exchangis.dss.appconn.operation.impl.ExchangisOptStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ModuleFactory {
    private volatile static ModuleFactory factory;
    private static Map<String, OperationStrategy> map = new HashMap<>();

    static {
        map.put(Constraints.ENGINE_TYPE_SQOOP_NAME, new ExchangisOptStrategy());
    }

    private ModuleFactory() {
    }


    public OperationStrategy crateModule(String name) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(name) || !map.containsKey(name)) {
            throw new ExternalOperationFailedException(90177, "create module failed,Unknown task type: " + name, null);
        }
        return map.get(name);
    }


     public static ModuleFactory getInstance() {
        if (factory == null) {
            synchronized (ModuleFactory.class) {
                if (factory == null) {
                    factory = new ModuleFactory();
                }
            }
        }

        return factory;
    }


}
