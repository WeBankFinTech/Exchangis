package com.webank.wedatasphere.exchangis.job.server.builder;

import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestDataXJobBuilder {

    private static SpringExchangisJobBuilderManager jobBuilderManager = new SpringExchangisJobBuilderManager();

    static {
        jobBuilderManager.init();
    }

    public static void main(String[] args) throws ExchangisJobException {
        ExchangisJobVO job = getDataxJob();
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
        ctx.putEnv("USER_NAME", "xxxxyyyyzzzz");
        ExchangisJobInfo jobInfo = new ExchangisJobInfo(job);
        ctx.setOriginalJob(jobInfo);
        TransformExchangisJob transformJob = jobBuilderManager.doBuild(jobInfo, TransformExchangisJob.class, ctx);
        List<ExchangisEngineJob> engineJobs = new ArrayList<>();


        for (SubExchangisJob subExchangisJob : transformJob.getSubJobSet()) {
            String sourceDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE).get("datasource").getValue().toString();
            String sinkDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK).get("datasource").getValue().toString();
            //            if (!ctx.containsDatasourceParam(sourceDsId)) {
            //                Map<String, Object> sourceDsParam = getDsParam(sourceDsId);
            //                ctx.putDatasourceParam(sourceDsId, sourceDsParam);
            //            }
            //            if (!ctx.containsDatasourceParam(sinkDsId)) {
            //                Map<String, Object> sinkDsParam = getDsParam(sinkDsId);
            //                ctx.putDatasourceParam(sinkDsId, sinkDsParam);
            //            }
            // connectParams
            Optional.ofNullable(jobBuilderManager.doBuild(subExchangisJob,
                    SubExchangisJob.class, ExchangisEngineJob.class, ctx)).ifPresent(engineJobs::add);
        }

        //  List<ExchangisEngineJob> -> List<ExchangisLauncherJob>
        List<LaunchableExchangisTask> launchableTasks = new ArrayList<>();
        for (ExchangisEngineJob engineJob : engineJobs) {
            Optional.ofNullable(jobBuilderManager.doBuild(engineJob,
                    ExchangisEngineJob.class, LaunchableExchangisTask.class, ctx)).ifPresent(launchableTasks::add);
        }
        if (launchableTasks.isEmpty()) {
            throw new ExchangisJobException(ExchangisJobExceptionCode.TASK_BUILDER_ERROR.getCode(),
                    "The result set of launcher job is empty, please examine your job entity, [ 生成LauncherJob为空 ]", null);
        }

        for (LaunchableExchangisTask launchableTask : launchableTasks) {
            System.out.println(launchableTask.getName());
        }
    }

    public static ExchangisJobVO getDataxJob() {
        ExchangisJobVO job = new ExchangisJobVO();
        job.setId(22L);
        job.setProjectId(1456173825011081218L);
//        job.setName("T_DATAX");
        job.setJobType("OFFLINE");
        job.setEngineType("DATAX");
        job.setJobLabels("");
        job.setJobDesc("");
        job.setProxyUser("hdfs");
        job.setSyncType("FULL");
        job.setJobParams("{}");
        job.setContent("[{\"subJobName\":\"TjpQkiAeGfTe\",\"dataSources\":{\"source_id\":\"HIVE.22.test.psn\",\"sink_id\":\"MYSQL.29.test.t_psn\"},\"params\":{\"sources\":[{\"config_key\":\"exchangis.job.ds.params.datax.hive.r.trans_proto\",\"config_name\":\"传输方式\",\"config_value\":\"记录\",\"sort\":1},{\"config_key\":\"exchangis.job.ds.params.datax.hive.r.partition\",\"config_name\":\"分区信息\",\"config_value\":\"\",\"sort\":2},{\"config_key\":\"exchangis.job.ds.params.datax.hive.r.row_format\",\"config_name\":\"字段格式\",\"config_value\":\"\",\"sort\":3}],\"sinks\":[{\"config_key\":\"exchangis.job.ds.params.datax.mysql.w.write_type\",\"config_name\":\"写入方式\",\"config_value\":\"INSERT\",\"sort\":1},{\"config_key\":\"exchangis.job.ds.params.datax.mysql.w.batch_size\",\"config_name\":\"批量大小\",\"config_value\":\"1000\",\"sort\":2}]},\"transforms\":{\"type\":\"MAPPING\",\"mapping\":[{\"sink_field_name\":\"id\",\"sink_field_type\":\"INT\",\"sink_field_index\":0,\"sink_field_editable\":true,\"validator\":[\"> 1\"],\"transformer\":{\"name\":\"ex_pad\",\"params\":[\"r\",\"3\",\"0\"]},\"deleteEnable\":false,\"source_field_name\":\"id\",\"source_field_type\":\"int\",\"source_field_index\":0,\"source_field_editable\":false},{\"sink_field_name\":\"age\",\"sink_field_type\":\"INT\",\"sink_field_index\":1,\"sink_field_editable\":true,\"validator\":[\"> 0\"],\"transformer\":{\"name\":\"ex_pad\",\"params\":[\"l\",\"3\",\"0\"]},\"deleteEnable\":false,\"source_field_name\":\"age\",\"source_field_type\":\"int\",\"source_field_index\":1,\"source_field_editable\":false}],\"addEnable\":false},\"settings\":[{\"config_key\":\"exchangis.datax.setting.speed.bytes\",\"config_name\":\"作业速率限制\",\"config_value\":\"1000\",\"sort\":1},{\"config_key\":\"exchangis.datax.setting.speed.records\",\"config_name\":\"作业记录数限制\",\"config_value\":\"1000\",\"sort\":2},{\"config_key\":\"exchangis.datax.setting.max.parallelism\",\"config_name\":\"作业最大并行度\",\"config_value\":\"1\",\"sort\":3},{\"config_key\":\"exchangis.datax.setting.max.memory\",\"config_name\":\"作业最大使用内存\",\"config_value\":\"2000\",\"sort\":4},{\"config_key\":\"exchangis.datax.setting.errorlimit.record\",\"config_name\":\"最多错误记录数\",\"config_value\":\"1000\",\"sort\":5}]}]");
        return job;
    }
}
