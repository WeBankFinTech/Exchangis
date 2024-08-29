package com.webank.wedatasphere.exchangis.datasource.mapper;

import com.webank.wedatasphere.exchangis.common.pager.PageQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitQuery;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface RateLimitMapper {

    /**
     * Insert
     *
     * @param rateLimit data
     * @return primary key
     */
    int insert(RateLimit rateLimit);

    /**
     * Update
     *
     * @param rateLimit data
     * @return affect rows
     */
    int update(RateLimit rateLimit);

    /**
     * Delete
     *
     * @return affect rows
     */
    int delete(@Param("ids") List<Object> ids);

    List<RateLimitVo> findPageVo(RateLimitQuery pageQuery);

    /**
     * Rate limit dao
     * @param limitRealm realm
     * @param limitRealmIds realm ids
     * @return rate limit rules
     */
    List<RateLimit> selectByRealmIds(String limitRealm, List<Long> limitRealmIds);

    /**
     * Count result
     *
     * @param pageQuery page query
     * @return value
     */
    long count(PageQuery pageQuery);

    /**
     * Select
     *
     * @param rateLimit
     * @return data
     */
    RateLimit selectOne(RateLimit rateLimit);

    /**
     * Search
     *
     * @return
     */
    List<RateLimit> findPage(PageQuery pageQuery, RowBounds rowBound);

    /**
     * 查询所有的数据
     *
     * @return
     */
    List<RateLimit> selectAllList(PageQuery pageQuery);
}
