package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitUsed;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface RateLimitUsedMapper {

    /**
     * Insert rateLimitUsed list
     * @param rateLimitUsedList
     */
    Integer insert(List<RateLimitUsed> rateLimitUsedList);

    /**
     * Update rateLimitUsed list
     * @param rateLimitUsedList
     */
    Integer update(List<RateLimitUsed> rateLimitUsedList);

    /**
     * Delete
     *
     * @return affect rows
     */
    int delete(@Param("ids") List<Object> ids);

    /**
     * Select used by rate limit ids
     * @param rateLimitIds ids
     * @return used
     */
    List<RateLimitUsed> selectUsedInLimitIds(List<Long> rateLimitIds);

    /**
     * Update used
     * @param rateLimitUsedList list
     * @return affected rows
     */
    int applyRateLimitUsed(RateLimitUsed rateLimitUsedList);

    /**
     * Recycle used
     * @param rateLimitUsedList list
     */
    int releaseRateLimitUsed(List<RateLimitUsed> rateLimitUsedList);

    /**
     * Reset rateLimitUsed by rateLimit
     * @param rateLimit
     */
    void resetRateLimitUsed(RateLimit rateLimit);

    /**
     * Count result
     *
     * @param pageQuery page query
     * @return value
     */
    long count(PageQuery pageQuery);

    /**
     * Search
     *
     * @return
     */
    List<RateLimitUsed> findPage(PageQuery pageQuery, RowBounds rowBound);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<RateLimitUsed> selectAllList(PageQuery pageQuery);

}
