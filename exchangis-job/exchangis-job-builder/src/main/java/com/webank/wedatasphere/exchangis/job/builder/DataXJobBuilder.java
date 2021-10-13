package com.webank.wedatasphere.exchangis.job.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.job.datax.domain.LaunchCode;
import com.webank.wedatasphere.exchangis.job.datax.handler.DataxJobHandler;
import com.webank.wedatasphere.exchangis.job.datax.reader.Reader;
import com.webank.wedatasphere.exchangis.job.datax.writer.Writer;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;
import com.webank.wedatasphere.exchangis.job.utils.Utils;
import com.webank.wedatasphere.linkis.common.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataXJobBuilder implements ExchangisJobBuilder {

    protected static final String HANDLER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.datax.handler";
    protected static final String READER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.datax.reader";
    protected static final String WRITER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.datax.writer";

    @Override
    public List<ExchangisLaunchTask> buildJob(ExchangisJob job) throws ExchangisDataSourceException {

        String content = job.getContent();
        List<ExchangisSubJob> exchangisSubJobs;
        try {
            exchangisSubJobs = JsonUtils.jackson().readValue(content, new TypeReference<List<ExchangisSubJob>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(31101, e.getMessage());
        }

        Long jobId = job.getId();
        List<ExchangisLaunchTask> launchTasks = new ArrayList<>();
        for (ExchangisSubJob subjob : exchangisSubJobs) {
            LaunchCode code = new LaunchCode();
            code.setContent(generateContents(subjob, jobId));
            code.setSettings(generateSettings(subjob));

            ExchangisLaunchTask task = new ExchangisLaunchTask();
            try {
                task.setContent(JsonUtils.jackson().writeValueAsString(code));
            } catch (JsonProcessingException e) {
                throw new ExchangisDataSourceException(31101, e.getMessage());
            }
            task.setJobId(jobId);
            task.setEngineType(job.getEngineType());
//            task.setRunType(subjob.getTransforms().getType());
            task.setCreateUser(job.getCreateUser());
            task.setProxyUser(job.getProxyUser());

            launchTasks.add(task);
        }

        return launchTasks;
    }

    private List generateContents(ExchangisSubJob subjob, Long jobId) throws ExchangisDataSourceException {
        String source = getDataSourceType(subjob.getDataSources().getSourceId());
        String sink = getDataSourceType(subjob.getDataSources().getSinkId());

        List<String> classList = Utils.getClazzName(HANDLER_PACKAGE_NAME, true);
        List<String> newClassList = new ArrayList<>(classList.size());
        for (int i = 0; i < classList.size(); i++) {
            newClassList.add(classList.get(i).replace(HANDLER_PACKAGE_NAME + ".", "").replace("JobHandler", ""));
        }

        JobHandler jobhandler;
        Reader reader;
        Writer writer;
        List contents = new ArrayList();

        try {
            if (newClassList.stream().anyMatch(c -> c.equalsIgnoreCase(source))) {
                String name = newClassList.stream().filter(f -> f.equalsIgnoreCase(source)).findAny().get();
                jobhandler = (JobHandler) Class.forName(HANDLER_PACKAGE_NAME + "." + name + "JobHandler").newInstance();
                reader = (Reader) Class.forName(READER_PACKAGE_NAME + "." + name + "Reader").newInstance();
            } else {
                jobhandler = new DataxJobHandler();
                reader = new Reader();
            }
            jobhandler.handleReader(subjob, jobId, reader);

            if (newClassList.stream().anyMatch(c -> c.equalsIgnoreCase(sink))) {
                String name = newClassList.stream().filter(f -> f.equalsIgnoreCase(sink)).findAny().get();
                jobhandler = (JobHandler) Class.forName(HANDLER_PACKAGE_NAME + "." + name + "JobHandler").newInstance();
                writer = (Writer) Class.forName(WRITER_PACKAGE_NAME + "." + name + "Writer").newInstance();

            } else {
                jobhandler = new DataxJobHandler();
                writer = new Writer();
            }
            jobhandler.handleWriter(subjob, jobId, writer);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ExchangisDataSourceException(23001, e.getLocalizedMessage());
        }

        contents.add(reader);
        contents.add(writer);

        return contents;
    }

    private Map generateSettings(ExchangisSubJob subjob) {
        ObjectNode dataXSetting = JsonUtils.jackson().createObjectNode();

        List<ExchangisJobParamsContent.ExchangisJobParamsItem> subjobSettings = subjob.getSettings();
        subjobSettings.forEach(subjobSetting -> {
            String configKey = subjobSetting.getConfigKey();
            // exchangis.datax.setting.speed.channel
            if (!configKey.startsWith("exchangis.datax.setting")
                    || StringUtils.isBlank(subjobSetting.getConfigValue())) {
                return;
            }
            String[] keys = StringUtils.split(configKey, ".");

            ObjectNode node = dataXSetting;
            for (int i = 2; i < keys.length - 1; i++) {
                ObjectNode subNode;
                if (node.has(keys[i])) {
                    subNode = (ObjectNode) node.get(keys[i]);
                } else {
                    subNode = JsonUtils.jackson().createObjectNode();
                    node.set(keys[i], subNode);
                }
                node = subNode;
            }
            node.put(keys[keys.length - 1], Double.valueOf(subjobSetting.getConfigValue()));
        });

        Map settingsMap = JsonUtils.jackson().convertValue(dataXSetting, Map.class);
        return settingsMap;
    }

    private String getDataSourceType(String id) {
        return id.substring(0, id.indexOf("."));
    }

}
