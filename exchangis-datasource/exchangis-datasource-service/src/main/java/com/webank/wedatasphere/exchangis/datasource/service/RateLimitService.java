package com.webank.wedatasphere.exchangis.datasource.service;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.datasource.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.domain.RateLimitUsed;
import com.webank.wedatasphere.exchangis.datasource.domain.RateLimitVo;
import com.webank.wedatasphere.exchangis.datasource.domain.RateLimitQuery;

import java.util.List;

public interface RateLimitService {

    /**
     * Insert
     *
     * @param rateLimit data
     * @return primary key
     */
    boolean add(RateLimit rateLimit);

    boolean update(RateLimit rateLimit);

    boolean delete(RateLimit rateLimit);

    List<RateLimitVo> findRateLimitPage(RateLimitQuery pageQuery);

    RateLimit selectOne(RateLimit rateLimit);

    /**
     * Limit the job rate
     * @param jobInfo job info
     * @return boolean
     */
    boolean rateLimit(ExchangisJobInfo jobInfo);

    /**
     * Limit by applying used
     * @param applyUsed apply used
     */
    void rateLimit(List<RateLimitUsed> applyUsed);
    /**
     * Release job rate limit
     * @param jobInfo job info
     */
    void releaseRateLimit(ExchangisJobInfo jobInfo);

    /**
     * Reset rateLimitUsed by rateLimit
     * @param rateLimit
     */
    void resetRateLimitUsed(RateLimit rateLimit);

}
