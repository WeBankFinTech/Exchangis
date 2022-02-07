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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom appender
 */
@Plugin(name = "RpcLog", category = "Core", elementType = "appender", printObject = true)
public class RpcJobLogAppender extends AbstractAppender {

    private static final Logger LOG = LoggerFactory.getLogger(RpcJobLogAppender.class);

    private static final Pattern JOB_LOG_PATTERN = Pattern.compile("^[\\s\\S]+?\\[([\\w\\d-]+):([\\w\\d-]+)]");

    protected RpcJobLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent logEvent) {
        String logContent = new String(getLayout().toByteArray(logEvent));

        if (Objects.nonNull(JobLogServiceInstance.jobLogService)){
            String[] resolved = resolveJobLogLine(logContent);
            if (Objects.nonNull(resolved)){
                JobLogServiceInstance.jobLogService.appendLog(resolved[0], resolved[1], Collections.singletonList(logContent));
            } else {
                LOG.error("JobLogService instance is ignored ! missing log => [" + logContent + "]");
            }
        } else {
            LOG.error("JobLogService instance cannot be found ! missing log => [" + logContent + "]");
        }
    }

    /**
     * Resolve job log line
     * @param line log line
     * @return String[0] => tenancy, String[1] => job_execution_id
     */
    private String[] resolveJobLogLine(String line){
        Matcher matcher = JOB_LOG_PATTERN.matcher(line);
        if (matcher.find()){
            return new String[]{matcher.group(1), matcher.group(2)};
        }
        return null;
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

    public static void main(String[] args){
        String testLog = "2022-01-27 19:36:26.028 INFO  - [davidhua:68f42422-a4d8-4065-9810-86013d93f153] Init to create launched job and begin generating";
        Matcher matcher = JOB_LOG_PATTERN.matcher(testLog);
        if (matcher.find()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }
}
