package com.webank.wedatasphere.exchangis.job.server.restful.configuration;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.validator.groups.InsertGroup;
import com.webank.wedatasphere.exchangis.common.validator.groups.UpdateGroup;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.enums.EngineTypeEnum;
import com.webank.wedatasphere.exchangis.job.server.render.transform.*;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.ProcessorRequestVo;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.ProcessorTransformer;
import com.webank.wedatasphere.exchangis.job.server.render.transform.processor.TransformProcessor;
import com.webank.wedatasphere.exchangis.job.server.service.JobTransformService;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "dss/exchangis/main/job/transform", produces = {"application/json;charset=utf-8"})
public class ExchangisJobTransformRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangisJobTransformRestfulApi.class);

    @Resource
    private JobTransformService transformService;

    @Resource
    private TransformerContainer transformerContainer;

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public Message settings(@Validated @RequestBody TransformRequestVo params,
                            BindingResult result, HttpServletRequest request){
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = UserUtils.getLoginUser(request);
        params.setOperator(userName);
        Message response = Message.ok();
        try{
            Map<String, TransformSettings> settingsMap = this.transformService.getSettings(params);
            if (Objects.nonNull(settingsMap) && !settingsMap.isEmpty()){
                response.data("types", settingsMap.keySet());
                Message finalResponse = response;
                settingsMap.values().forEach(settings -> {
                    // Inject the settings' params
                    Map<String, Object> settingMap = Json.convert(settings, Map.class, String.class, Object.class);
                    if (Objects.nonNull(settingMap)) {
                        settingMap.forEach(finalResponse::data);
                    }
                });
            }

        } catch (Exception e){
            String message = "Fail to get transformer settings (加载转换器(映射/处理器)配置失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    /**
     * Create processor
     * @param requestVo request vo
     * @param result result
     * @param request request
     * @return
     */
    @RequestMapping(value = "/processor/code_content", method = RequestMethod.POST)
    public Message createProcessor(@Validated({InsertGroup.class, Default.class}) @RequestBody ProcessorRequestVo requestVo,
                                   BindingResult result, HttpServletRequest request){
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = UserUtils.getLoginUser(request);
        // TODO validate the authority
        TransformProcessor processor = new TransformProcessor();
        processor.setJobId(Long.valueOf(requestVo.getJobId()));
        processor.setCodeContent(requestVo.getCode());
        processor.setCreator(userName);
        Message response = Message.ok();
        try{
            response.data("proc_code_id", this.transformService.saveProcessor(processor));
        } catch (Exception e){
            String message = "Fail to create transform processor (创建处理器内容失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    /**
     * Update processor
     * @param requestVo request vo
     * @param result result
     * @param procCodeId proc code id
     * @param request
     * @return
     */
    @RequestMapping(value = "/processor/code_content/{proc_code_id:\\w+}", method = RequestMethod.PUT)
    public Message updateProcessor(@Validated({UpdateGroup.class, Default.class}) @RequestBody ProcessorRequestVo requestVo
            , BindingResult result, @PathVariable("proc_code_id")String procCodeId, HttpServletRequest request){
        if (result.hasErrors()){
            return Message.error(result.getFieldErrors().get(0).getDefaultMessage());
        }
        String userName = UserUtils.getLoginUser(request);
        // TODO validate the authority
        TransformProcessor processor = this.transformService.getProcessorInfo(procCodeId);
        if (Objects.isNull(processor)){
            return Message.error("Unable find the transform processor with code_id: [" + procCodeId + "] (处理器不存在)");
        }
        processor.setCodeContent(requestVo.getCode());
        Message response = Message.ok();
        try{
            response.data("proc_code_id", this.transformService.updateProcessor(processor));
        } catch (Exception e){
            String message = "Fail to update transform processor (更新处理器内容失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    @RequestMapping(value = "/processor/code_content/{proc_code_id:\\w+}", method = RequestMethod.GET)
    public Message getProcessor(@PathVariable("proc_code_id")String procCodeId, HttpServletRequest request){
        String userName = UserUtils.getLoginUser(request);
        // TODO validate the authority
        Message response = Message.ok();
        try {
            TransformProcessor processor = this.transformService.getProcessorWithCode(procCodeId);
            response.data("code", Objects.nonNull(processor)? processor.getCodeContent() : null);
        } catch (Exception e){
            String message = "Fail to fetch code content of transform processor (获取处理器内容失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }

    @RequestMapping(value = "/processor/code_template", method = RequestMethod.GET)
    public Message getProcTemplate(@RequestParam(value = "engine", required = false)String engine){
        // Accept all users to request
        Message response = Message.ok();
        try{
            Transformer transformer = this.transformerContainer.getTransformer(TransformRule.Types.PROCESSOR.name());
            String template = null;
            if (Objects.nonNull(transformer) && transformer instanceof ProcessorTransformer){
                template = ((ProcessorTransformer) transformer).getCodeTemplate(Objects.nonNull(engine)? engine :
                        EngineTypeEnum.DATAX.name().toLowerCase(Locale.ROOT), "java");
            }
            response.data("code", template);
        } catch (Exception e){
            String message = "Fail to obtain template code content (获得处理器内容模版失败)";
            LOG.error(message, e);
            response = Message.error(message);
        }
        return response;
    }
}
