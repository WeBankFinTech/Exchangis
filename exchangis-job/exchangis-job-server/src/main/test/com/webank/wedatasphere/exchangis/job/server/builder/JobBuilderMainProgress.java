package com.webank.wedatasphere.exchangis.job.server.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.DataxExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.SqoopExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;

import java.security.SecureRandom;
import java.util.*;

public class JobBuilderMainProgress {

    public static void main(String[] args) throws Exception{
//        System.setProperty("log4j.configurationFile", "C:\\Users\\hadoop\\IdeaProjects\\Exchangis\\assembly-package\\config\\log4j2.xml");
        System.setProperty("log4j.configurationFile", "C:\\Users\\davidhua\\IdeaProjects\\Exchangis\\assembly-package\\config\\log4j2.xml");
        SpringExchangisJobBuilderManager jobBuilderManager = new SpringExchangisJobBuilderManager();
        jobBuilderManager.init();
        ExchangisJobInfo jobInfo = getDemoSqoopJobInfo();
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
        ctx.setOriginalJob(jobInfo);
        // ExchangisJob -> ExchangisTransformJob(SubExchangisJob)
        TransformExchangisJob transformJob = jobBuilderManager.doBuild(jobInfo, TransformExchangisJob.class, ctx);
        List<ExchangisEngineJob> engineJobs = new ArrayList<>();
        // ExchangisTransformJob(SubExchangisJob) -> List<ExchangisEngineJob>
        for(SubExchangisJob subExchangisJob : transformJob.getSubJobSet()){
            Optional.ofNullable(jobBuilderManager.doBuild(subExchangisJob,
                    SubExchangisJob.class, ExchangisEngineJob.class, ctx)).ifPresent(engineJobs::add);
        }
        engineJobs.forEach(engineJob -> {
            if(engineJob instanceof DataxExchangisEngineJob){
                Map<String, Object> code = Json.fromJson(((DataxExchangisEngineJob)engineJob).getCode(), Map.class);
                try {
                    System.out.println(Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(code));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else if(engineJob instanceof SqoopExchangisEngineJob){
                try {
                    System.out.println(Json.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(engineJobs));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static ExchangisJobInfo getDemoSqoopJobInfo(){
        ExchangisJobVO job = new ExchangisJobVO();
        job.setId((long) new SecureRandom().nextInt(100));
        job.setJobName("Job-Builder-Main");
        job.setContent("[{\"subjobName\":\"subjob1\",\"dataSources\":{\"source_id\":\"MYSQL.10002.db_mask.table_source\",\"sink_id\":\"MYSQL.10002.db_mask.table_sink\"},\"params\":{\"sources\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}],\"sinks\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}]},\"transforms\":{\"type\":\"MAPPING\",\"sql\":\"\",\"mapping\":[{\"source_field_name\":\"field1\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field2\",\"sink_field_type\":\"varchar\",\"validator\":[\">100\",\"<200\"],\"transformer\":{\"name\":\"ex_substr\",\"params\":[\"1\",\"3\"]}},{\"source_field_name\":\"field3\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field4\",\"sink_field_type\":\"varchar\",\"validator\":[\"like'%example'\"],\"transformer\":{\"name\":\"ex_replace\",\"params\":[\"1\",\"3\",\"***\"]}}]},\"settings\":[{\"config_key\":\"errorlimit_percentage\",\"config_name\":\"脏数据占比阈值\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"errorlimit_record\",\"config_name\":\"脏数据最大记录数\",\"config_value\":\"10\",\"sort\":2}]}]");
        job.setEngineType("sqoop");
        return new ExchangisJobInfo(job);
    }
}
