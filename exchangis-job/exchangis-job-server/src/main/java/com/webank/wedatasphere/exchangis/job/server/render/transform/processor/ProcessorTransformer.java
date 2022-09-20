package com.webank.wedatasphere.exchangis.job.server.render.transform.processor;

import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import com.webank.wedatasphere.exchangis.job.server.render.transform.Transformer;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRequestVo;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformSettings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.datasourcemanager.common.exception.JsonErrorException;
import org.apache.linkis.datasourcemanager.common.util.PatternInjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Transform of processor
 */
public class ProcessorTransformer implements Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorTransformer.class);

    public static final CommonVars<String> CODE_TEMPLATE_PATH = CommonVars.apply("wds.exchangis.job.render.transform.processor.code-template.path", "transform-processor-templates");

    public static final CommonVars<String> CODE_TEMPLATE_NAME = CommonVars.apply("wds.exchangis.job.render.transform.processor.code-template.name", "${engine}-processor.${type}");

    @Override
    public String name() {
        return TransformRule.Types.PROCESSOR.name();
    }

    @Override
    public TransformSettings getSettings(TransformRequestVo requestVo) {
        return null;
    }

    /**
     * Fetch code template
     * @param engine
     * @param codeType
     * @return
     */
    public String getCodeTemplate(String engine, String codeType){
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        String templateName = null;
        try {
            templateName = PatternInjectUtils.inject(CODE_TEMPLATE_NAME.getValue(), new String[]{engine, codeType});
        } catch (JsonErrorException e) {
            LOG.warn("Unable to generate the template name", e);
        }
        if (StringUtils.isNotBlank(templateName)){
            URL resource = currentClassLoader.getResource(CODE_TEMPLATE_PATH.getValue() + IOUtils.DIR_SEPARATOR_UNIX + templateName);
            if (Objects.nonNull(resource)){
                try {
                    return IOUtils.toString(new FileInputStream(resource.getPath()), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    LOG.warn("Unable to load code template form: {}", resource.getPath(), e);
                }
            }
        }
        return null;
    }
}
