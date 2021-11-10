package com.webank.wedatasphere.exchangis.job.server.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.DataxExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.server.builder.engine.SqoopExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.ExchangisTransformJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JobBuilderMainProgress {

    public static void main(String[] args) throws Exception{
        ExchangisJob job = new ExchangisJob();
        SpringExchangisJobBuilderManager jobBuilderManager = new SpringExchangisJobBuilderManager();
        jobBuilderManager.init();
        job.setContent("[{\"subjobName\":\"subjob1\",\"dataSources\":{\"source_id\":\"MYSQL.10002.db_mask.table_source\",\"sink_id\":\"MYSQL.10002.db_mask.table_sink\"},\"params\":{\"sources\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}],\"sinks\":[{\"config_key\":\"exchangis.job.mysql.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"exchangis.job.mysql.batch_size\",\"config_name\":\"批量大小\",\"config_value\":1000,\"sort\":2}]},\"transforms\":{\"type\":\"MAPPING\",\"sql\":\"\",\"mapping\":[{\"source_field_name\":\"field1\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field2\",\"sink_field_type\":\"varchar\",\"validator\":[\">100\",\"<200\"],\"transformer\":{\"name\":\"ex_substr\",\"params\":[\"1\",\"3\"]}},{\"source_field_name\":\"field3\",\"source_field_type\":\"varchar\",\"sink_field_name\":\"field4\",\"sink_field_type\":\"varchar\",\"validator\":[\"like'%example'\"],\"transformer\":{\"name\":\"ex_replace\",\"params\":[\"1\",\"3\",\"***\"]}}]},\"settings\":[{\"config_key\":\"errorlimit_percentage\",\"config_name\":\"脏数据占比阈值\",\"config_value\":\"insert\",\"sort\":1},{\"config_key\":\"errorlimit_record\",\"config_name\":\"脏数据最大记录数\",\"config_value\":\"10\",\"sort\":2}]}]");
        job.setEngineType("datax");
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
        ctx.setOriginalJob(job);
        // ExchangisJob -> ExchangisTransformJob(SubExchangisJob)
        ExchangisTransformJob transformJob = jobBuilderManager.doBuild(job, ExchangisTransformJob.class, ctx);
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
}
