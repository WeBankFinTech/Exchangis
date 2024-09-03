package com.webank.wedatasphere.exchangis.datasource.utils;

import java.util.List;
import java.util.Map;

public interface RateLimitsProvider {

    List<String> getRealms();

    Map<String, String> getRateLimits(String realmId);

}
