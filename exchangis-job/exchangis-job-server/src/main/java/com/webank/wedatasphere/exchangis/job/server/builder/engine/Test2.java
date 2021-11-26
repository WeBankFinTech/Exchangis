package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParamDefine;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParams;

public class Test2 {

    private static final JobParamDefine<String> HCATALOG_IMPORT = JobParams.define("hcatalog.import", (k, v) -> {
        System.out.println(k);
        System.out.println(v);
        return "abc";
    });

    public static void main(String[] args) {
        String xxx = HCATALOG_IMPORT.newParam("xxx").getValue();
        System.out.println(xxx);
    }
}
