package com.webank.wedatasphere.exchangis.job.domain;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * For each sub job entity,
 * will have parameter set which divided into different realm
 */
public class SubExchangisJob extends ExchangisJobBase{

    public static final String REALM_JOB_SETTINGS = "job.realm.settings";

    public static final String REALM_JOB_DATA_SOURCE = "job.realm.data-source";

    public static final String REALM_JOB_CONTENT_SINK = "job.realm.content.sink";

    public static final String REALM_JOB_CONTENT_SOURCE = "job.realm.content.source";

    public static final String REALM_JOB_MAPPING = "job.realm.mappings";

    /**
     * Realm params set
     */
    private Map<String, JobParamSet> realmParamSet = new ConcurrentHashMap<>();

    /**
     * Add
     * @param realm realm info
     * @param paramSet param set
     */
    public void addRealmParams(String realm, JobParamSet paramSet){
        realmParamSet.put(realm, paramSet);
    }

    /**
     * Get
     * @param realm realm info
     * @return param set
     */
    public JobParamSet getRealmParams(String realm){
        return  realmParamSet.get(realm);
    }

    /**
     * Get and convert to map
     * @param realm realm info
     * @return map
     */
    public Map<String, Object> getParamsToMap(String realm){
        JobParamSet jobParamSet = getRealmParams(realm);
        return jobParamSet.toList().stream().collect(
                Collectors.toMap(JobParam::getParamStrKey, JobParam::getParamValue));
    }

    /**
     * Get all and convert to map
     * @return map
     */
    public Map<String, Object> getParamsToMap(){
        return realmParamSet.values().stream().flatMap(realmParam -> realmParam.toList().stream())
                .collect(Collectors.toMap(JobParam::getParamStrKey, JobParam::getParamValue));
    }
}
