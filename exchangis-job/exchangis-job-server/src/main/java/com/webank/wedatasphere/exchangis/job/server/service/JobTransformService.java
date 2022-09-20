package com.webank.wedatasphere.exchangis.job.server.service;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRequestVo;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformSettings;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.TransformProcessor;

import java.util.Map;

/**
 * Transform service
 */
public interface JobTransformService {

    /**
     * Get transform settings
     * @param requestVo request vo
     * @return type => settings
     */
    Map<String, TransformSettings> getSettings(TransformRequestVo requestVo);
    /**
     * Save processor
     * @param processor processor
     * @return process code id
     */
    Long saveProcessor(TransformProcessor processor);

    /**
     * Save processor information
     * @param processor processor
     * @return process code id
     */
    Long saveProcessorInfo(TransformProcessor processor);
    /**
     * Get processor with code content
     * @param procCodeId process code id
     * @return processor
     */
    TransformProcessor getProcessorWithCode(String procCodeId);

    /**
     * Get processor with code content
     * @param procCodeId process code id
     * @return processor
     */
    TransformProcessor getProcessorInfo(String procCodeId);

    /**
     * Update processor
     * @param processor processor
     * @return process code id
     */
    Long updateProcessor(TransformProcessor processor);

    /**
     * Update processor information
     * @param processor processor
     * @return process code id
     */
    Long updateProcessorInfo(TransformProcessor processor);

}
