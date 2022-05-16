package com.webank.wedatasphere.exchangis.job.api;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;

import java.util.List;

/**
 * Open for the other module to invoke
 */
public interface ExchangisJobOpenService {

    /**
     * Get job entity by id
     * @param id
     * @return
     */
    ExchangisJobEntity getJobById(Long id, boolean basic) throws ExchangisJobException;

    /**
     * Query job entity
     * @param queryVo query vo
     * @param inPage if in page
     * @return
     */
    List<ExchangisJobEntity> queryJobs(ExchangisJobQueryVo queryVo, boolean inPage)
        throws ExchangisJobException;

    /**
     * Delete the job entities
     * @param idList id list
     */
    void deleteJobBatch(List<Long> idList) throws ExchangisJobException;
}
