package com.webank.wedatasphere.exchangis.job.server.service.impl;


import com.webank.wedatasphere.exchangis.common.linkis.bml.BmlResource;
import com.webank.wedatasphere.exchangis.engine.resource.bml.BmlClients;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.mapper.JobTransformProcessorDao;
import com.webank.wedatasphere.exchangis.job.server.mapper.JobTransformRuleDao;
import com.webank.wedatasphere.exchangis.job.server.render.transform.*;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.TransformProcessor;
import com.webank.wedatasphere.exchangis.job.server.service.JobTransformService;
import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.bml.protocol.BmlDownloadResponse;
import org.apache.linkis.bml.protocol.BmlUpdateResponse;
import org.apache.linkis.bml.protocol.BmlUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DefaultJobTransformService implements JobTransformService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJobTransformService.class);

    @Resource
    private JobTransformProcessorDao processorDao;

    @Resource
    private JobTransformRuleDao transformRuleDao;

    @Resource
    private TransformRulesFusion<TransformDefine> transformDefineRulesFusion;

    @Resource
    private TransformerContainer transformerContainer;

    private JobTransformService selfService;


    @Override
    public Map<String, TransformSettings> getSettings(TransformRequestVo requestVo) {
        Map<String, TransformSettings> settingsMap = new HashMap<>();
        // First to get the definition rule to select transformers
        TransformDefine sourceDefine = getTransformDefineRule(requestVo.getSourceTypeId(), requestVo.getEngine(), TransformRule.Direction.SOURCE.name());
        TransformDefine sinkDefine = getTransformDefineRule(requestVo.getSinkTypeId(), requestVo.getEngine(), TransformRule.Direction.SINK.name());
        TransformDefine transformDefine = transformDefineRulesFusion.fuse(sourceDefine, sinkDefine);
        for(String type : transformDefine.getTypes()){
            Transformer transformer = this.transformerContainer.getTransformer(type);
            if (Objects.nonNull(transformer)){
                settingsMap.put(type, transformer.getSettings(requestVo));
            }
        }
        return settingsMap;
    }

    @Override
    public Long saveProcessor(TransformProcessor processor) {
        String content = processor.getCodeContent();
        if (StringUtils.isNotBlank(content)){
            try {
                BmlResource bmlResource = saveCodeToBml(processor.getCreator(), content);
                Optional.ofNullable(bmlResource).ifPresent(resource -> {
                    processor.setBmlResource(resource);
                    // Empty the code content
                    processor.setCodeContent(null);
                });
            }catch (Exception e){
                LOG.warn("Unable to save the code content of processor to the bml server: [{}]", e.getMessage());
            }
        }
        return getSelfService().saveProcessorInfo(processor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveProcessorInfo(TransformProcessor processor) {
        processorDao.saveOne(processor);
        return processor.getId();
    }

    @Override
    public TransformProcessor getProcessorWithCode(String procCodeId) {
        TransformProcessor processor = processorDao.getProcDetail(Long.valueOf(procCodeId));
        if (Objects.nonNull(processor)) {
            if (StringUtils.isBlank(processor.getCodeContent()) &&
                    Objects.nonNull(processor.getBmlResource())) {
                // Means that the content has stored in bml
                try {
                    String codeContent = downloadCodeFromBml(processor.getCreator(), processor.getBmlResource());
                    processor.setCodeContent(codeContent);
                } catch (Exception e) {
                    LOG.warn("Unable to fetch the code content of processor from bml server: [{}]", e.getMessage());
                }
            }
        }
        return processor;
    }

    @Override
    public TransformProcessor getProcessorInfo(String procCodeId) {
        return processorDao.getProcInfo(Long.valueOf(procCodeId));
    }

    @Override
    public Long updateProcessor(TransformProcessor processor) {
        BmlResource bmlResource = processor.getBmlResource();
        try {
            if (Objects.nonNull(bmlResource)) {
                bmlResource = updateCodeToBml(processor.getCreator(), bmlResource, processor.getCodeContent());
            } else {
                bmlResource = saveCodeToBml(processor.getCreator(), processor.getCodeContent());
            }
            Optional.ofNullable(bmlResource).ifPresent(resource -> {
                processor.setBmlResource(resource);
                // Empty the code content
                processor.setCodeContent(null);
            });
        } catch (Exception e){
            LOG.warn("Unable to save/update the code content of processor to the bml server: [{}]", e.getMessage());
        }
        return getSelfService().updateProcessorInfo(processor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateProcessorInfo(TransformProcessor processor) {
        processorDao.updateOne(processor);
        return processor.getId();
    }

    /**
     * Save code content to bml server
     * @param operator operator
     * @param codeContent code content
     * @return bml resource
     */
    private BmlResource saveCodeToBml(String operator, String codeContent){
        // TODO abstract the bml client in common module
        BmlUploadResponse uploadResponse = BmlClients.getInstance().uploadResource(operator, "code",
                IOUtils.toInputStream(codeContent, StandardCharsets.UTF_8));
        return Objects.nonNull(uploadResponse) ?
                new BmlResource(uploadResponse.resourceId(), uploadResponse.version()) : null;
    }


    /**
     * Update code content to bml server
     * @param operator operator
     * @param resource resource
     * @param codeContent code content
     * @return bml resource
     */
    private BmlResource updateCodeToBml(String operator, BmlResource resource, String codeContent){
        // TODO abstract the bml client in common module
        BmlUpdateResponse updateResponse = BmlClients.getInstance().updateResource(operator, resource.getResourceId(),
                "code", IOUtils.toInputStream(codeContent, StandardCharsets.UTF_8));
        return Objects.nonNull(updateResponse) ?
                new BmlResource(updateResponse.resourceId(), updateResponse.version()) : null;
    }

    /**
     * Download code content from bml server
     * @param operator operator
     * @param resource resource
     * @return bml resource
     */
    private String downloadCodeFromBml(String operator, BmlResource resource) throws IOException {
        // TODO abstract the bml client in common module
        BmlDownloadResponse downloadResponse = BmlClients.getInstance()
                .downloadResource(operator, resource.getResourceId(), resource.getVersion());
        return Objects.nonNull(downloadResponse) && Objects.nonNull(downloadResponse.inputStream())?
                IOUtils.toString(downloadResponse.inputStream(), StandardCharsets.UTF_8) : null;
    }

    /**
     * Get transform define rule
     * @param dataSourceType data source type
     * @param engine engine
     * @param direction direction
     * @return
     */
    private TransformDefine getTransformDefineRule(String dataSourceType, String engine, String direction){
        TransformDefine resultDefine = new TransformDefine(TransformRule.Types.DEF, null);
        this.transformRuleDao.getTransformRules(TransformRule.Types.DEF.name(), dataSourceType)
                .stream().filter(rule -> rule.matchInFraction(dataSourceType, engine, direction) > 0)
                .forEach(rule -> Optional.ofNullable(rule.toRule(TransformDefine.class)).ifPresent(define -> resultDefine.getTypes().addAll(define.getTypes())));
        if (resultDefine.getTypes().isEmpty()){
            // Add MAPPING type default
            resultDefine.getTypes().add(TransformTypes.MAPPING.name());
        }
        return resultDefine;
    }
    /**
     * Get the self-service
     * @return transform service
     */
    private JobTransformService getSelfService(){
        if (Objects.isNull(selfService)){
            this.selfService = SpringContextHolder.getBean(JobTransformService.class);
            if (Objects.isNull(this.selfService)){
                throw new ExchangisJobException.Runtime(ExchangisJobExceptionCode.RENDER_TRANSFORM_ERROR.getCode(),
                        "JobTransformService cannot be found in spring context", null);
            }
        }
        return this.selfService;
    }
}
