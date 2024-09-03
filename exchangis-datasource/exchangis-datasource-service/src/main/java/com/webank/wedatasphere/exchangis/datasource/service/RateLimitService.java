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
     * @param jobInfo job info
     * @return boolean
     */
    boolean rateLimit(ExchangisJobInfo jobInfo) throws RateLimitNoLeftException;

    /**
     * Limit by applying used
     * @param applyUsed apply used
     */
    void rateLimit(List<RateLimitUsed> applyUsed) throws RateLimitNoLeftException;
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
