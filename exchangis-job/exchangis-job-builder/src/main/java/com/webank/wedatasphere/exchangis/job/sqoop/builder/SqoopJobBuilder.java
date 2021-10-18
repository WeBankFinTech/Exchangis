package com.webank.wedatasphere.exchangis.job.sqoop.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilder;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisLaunchTask;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisSubJob;
import com.webank.wedatasphere.exchangis.job.handler.JobHandler;
import com.webank.wedatasphere.exchangis.job.sqoop.handler.SqoopJobHandler;
import com.webank.wedatasphere.exchangis.job.sqoop.reader.SqoopReader;
import com.webank.wedatasphere.exchangis.job.sqoop.writer.SqoopWriter;
import com.webank.wedatasphere.exchangis.job.utils.Utils;
import com.webank.wedatasphere.linkis.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class SqoopJobBuilder extends ExchangisJobBuilder {

    protected static final String HANDLER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.sqoop.handler";
    protected static final String READER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.sqoop.reader";
    protected static final String WRITER_PACKAGE_NAME = "com.webank.wedatasphere.exchangis.job.sqoop.writer";

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
            ExchangisLaunchTask task = new ExchangisLaunchTask();

            String code = generateCode(subjob, jobId);
            task.setContent(code);

            task.setJobId(jobId);
            task.setEngineType(job.getEngineType());
            task.setCreateUser(job.getCreateUser());
            task.setProxyUser(job.getProxyUser());

            launchTasks.add(task);

        }

        return launchTasks;
    }

    private String generateCode(ExchangisSubJob subjob, Long jobId) throws ExchangisDataSourceException {
        String source = getDataSourceType(subjob.getDataSources().getSourceId());
        String sink = getDataSourceType(subjob.getDataSources().getSinkId());

        List<String> classList = Utils.getClazzName(HANDLER_PACKAGE_NAME, true);
        List<String> newClassList = new ArrayList<>(classList.size());
        for (int i = 0; i < classList.size(); i++) {
            newClassList.add(classList.get(i).replace(HANDLER_PACKAGE_NAME + ".", "").replace("JobHandler", ""));
        }

        JobHandler jobhandler;
        SqoopReader reader;
        SqoopWriter writer;

        try {
            if (newClassList.stream().anyMatch(c -> c.equalsIgnoreCase(source))) {
                String name = newClassList.stream().filter(f -> f.equalsIgnoreCase(source)).findAny().get();
                jobhandler = (JobHandler) Class.forName(HANDLER_PACKAGE_NAME + "." + name + "JobHandler").newInstance();
                reader = (SqoopReader) Class.forName(READER_PACKAGE_NAME + "." + name + "Reader").newInstance();
            } else {
                jobhandler = new SqoopJobHandler();
                reader = new SqoopReader();
            }
            jobhandler.handleReader(subjob, jobId, reader);

            if (newClassList.stream().anyMatch(c -> c.equalsIgnoreCase(sink))) {
                String name = newClassList.stream().filter(f -> f.equalsIgnoreCase(sink)).findAny().get();
                jobhandler = (JobHandler) Class.forName(HANDLER_PACKAGE_NAME + "." + name + "JobHandler").newInstance();
                writer = (SqoopWriter) Class.forName(WRITER_PACKAGE_NAME + "." + name + "Writer").newInstance();

            } else {
                jobhandler = new SqoopJobHandler();
                writer = new SqoopWriter();
            }
            jobhandler.handleWriter(subjob, jobId, writer);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ExchangisDataSourceException(23001, e.getLocalizedMessage());
        }

        String code = makeCodeString(reader, writer);
        return code;
    }

    private String makeCodeString(SqoopReader reader, SqoopWriter writer) {
        String readerString = reader.getReaderString();
        String writerString = writer.getWriterString();
        String head = "import";
        String settings = "--delete-target-dir --num-mappers 1";
        String all = head + " " + readerString + " " + writerString + " " + settings;
        return all;
    }

    private String getDataSourceType(String id) {
        return id.substring(0, id.indexOf("."));
    }

}
