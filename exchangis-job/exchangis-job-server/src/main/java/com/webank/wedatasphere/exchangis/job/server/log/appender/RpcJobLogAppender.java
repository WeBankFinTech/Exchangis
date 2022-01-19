package com.webank.wedatasphere.exchangis.job.server.log.appender;

import com.webank.wedatasphere.exchangis.job.server.log.JobLogService;
import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Custom appender
 */
@Plugin(name = "RpcLog", category = "Core", elementType = "appender", printObject = true)
public class RpcJobLogAppender extends AbstractAppender {

    private static final Logger LOG = LoggerFactory.getLogger(RpcJobLogAppender.class);

    protected RpcJobLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent logEvent) {
        String logContent = new String(getLayout().toByteArray(logEvent));
        if (Objects.nonNull(JobLogServiceInstance.jobLogService)){
            JobLogServiceInstance.jobLogService.appendLog("", "", Collections.singletonList(logContent));
        } else {
            LOG.error("JobLogService instance cannot be found ! missing log => [" + logContent + "]");
        }
    }
    private static class JobLogServiceInstance{
        static JobLogService jobLogService;
        static {
            try {
                jobLogService = SpringContextHolder.getBean(JobLogService.class);
            }catch(Exception e){
                LOG.warn("Cannot get the bean of JobLogService from spring context !", e);
            }
        }
    }

    @PluginFactory
    public static RpcJobLogAppender createAppender(@PluginAttribute("name")String name,
                                                   @PluginElement("Filter")final Filter filter,
                                                   @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                   @PluginAttribute("ignoreExceptions")boolean ignoreExceptions){
        AtomicReference<RpcJobLogAppender> jobLogAppender = new AtomicReference<>();
        Optional.ofNullable(name).ifPresent(appenderName -> {
            Layout<? extends Serializable> layoutDef = layout;
            if (Objects.isNull(layout)){
                layoutDef = PatternLayout.createDefaultLayout();
            }
            jobLogAppender.set(new RpcJobLogAppender(name, filter, layoutDef, ignoreExceptions, Property.EMPTY_ARRAY));
        });
        return jobLogAppender.get();
    }
}
