package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitNoLeftException;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitOperationException;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitUsed;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitVo;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitQuery;

import java.util.List;
import java.util.Map;

public interface RateLimitService {

    /**
     * Insert
     *
     * @param rateLimit data
     * @return primary key
     */
    boolean add(RateLimit rateLimit) throws RateLimitOperationException;

    boolean update(RateLimit rateLimit) throws RateLimitOperationException;

    boolean delete(RateLimit rateLimit) throws RateLimitOperationException;

    PageResult<RateLimitVo> findRateLimitPage(RateLimitQuery pageQuery);

    RateLimit selectOne(RateLimit rateLimit);

    /**
     * Limit the job rate
     * @param rateParams rateParams
     * @param rateParamMap rateParamMap
     * @return boolean
     */
    boolean rateLimit(String rateParams, Map<String, Object> rateParamMap) throws RateLimitNoLeftException;

    List<RateLimitUsed> getRateLimitUsed(String rateParams, Map<String, Object> rateParamMap);

    /**
     * Limit by applying used
     * @param applyUsed apply used
     */
    void rateLimit(List<RateLimitUsed> applyUsed) throws RateLimitNoLeftException;

    /**
     * Release job rate limit
     * @param rateParams rateParams
     * @param rateParamsMap rateParamsMap
     */
    void releaseRateLimit(String rateParams, Map<String, Object> rateParamsMap);

    /**
     * Reset rateLimitUsed by rateLimit
     * @param rateLimit
     */
    void resetRateLimitUsed(RateLimit rateLimit);

}
