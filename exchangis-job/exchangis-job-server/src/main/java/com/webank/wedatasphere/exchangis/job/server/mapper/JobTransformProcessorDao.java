package com.webank.wedatasphere.exchangis.job.server.mapper;

import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.TransformProcessor;

/**
 * Job transform processor dao
 */
public interface JobTransformProcessorDao {
    /**
     * Save one entity
     * @param processor processor entity
     * @return id
     */
    Long saveOne(TransformProcessor processor);

    /**
     * Get the processor detail (with code content)
     * @param id id
     * @return processor
     */
    TransformProcessor getProcDetail(Long id);

    /**
     * Get the processor basic information
     * @param id id
     * @return processor
     */
    TransformProcessor getProcInfo(Long id);

    void updateOne(TransformProcessor processor);
}
