package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.launcher.builder.ExchangisLauncherJob;
import com.webank.wedatasphere.exchangis.job.server.builder.SpringExchangisJobBuilderManager;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.ExchangisTransformJob;
import com.webank.wedatasphere.linkis.datasourcemanager.common.util.json.Json;

import java.util.*;

public class Test {

    private static SpringExchangisJobBuilderManager jobBuilderManager = new SpringExchangisJobBuilderManager();

    static {
        jobBuilderManager.init();
    }

    public static void main(String[] args) throws ExchangisJobException, JsonProcessingException {
        ExchangisJob job = getJob();
        System.out.println(job.getJobName());
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
        ctx.putEnv("USER_NAME", "xxxxyyyyzzzz");
        ctx.setOriginalJob(job);
        ExchangisTransformJob transformJob = jobBuilderManager.doBuild(job, ExchangisTransformJob.class, ctx);
        List<ExchangisEngineJob> engineJobs = new ArrayList<>();


        for (SubExchangisJob subExchangisJob : transformJob.getSubJobSet()) {
            String sourceDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SOURCE).get("datasource").getValue().toString();
            String sinkDsId = subExchangisJob.getRealmParams(SubExchangisJob.REALM_JOB_CONTENT_SINK).get("datasource").getValue().toString();
            if (!ctx.containsDatasourceParam(sourceDsId)) {
                Map<String, Object> sourceDsParam = getDsParam(sourceDsId);
                ctx.putDatasourceParam(sourceDsId, sourceDsParam);
            }
            if (!ctx.containsDatasourceParam(sinkDsId)) {
                Map<String, Object> sinkDsParam = getDsParam(sinkDsId);
                ctx.putDatasourceParam(sinkDsId, sinkDsParam);
            }
            // connectParams
            Optional.ofNullable(jobBuilderManager.doBuild(subExchangisJob,
                    SubExchangisJob.class, ExchangisEngineJob.class, ctx)).ifPresent(engineJobs::add);
        }

        //  List<ExchangisEngineJob> -> List<ExchangisLauncherJob>
        List<ExchangisLauncherJob> launcherJobs = new ArrayList<>();
        for (ExchangisEngineJob engineJob : engineJobs) {
            Optional.ofNullable(jobBuilderManager.doBuild(engineJob,
                    ExchangisEngineJob.class, ExchangisLauncherJob.class, ctx)).ifPresent(launcherJobs::add);
        }
        if (launcherJobs.isEmpty()) {
            throw new ExchangisJobException(ExchangisJobExceptionCode.JOB_BUILDER_ERROR.getCode(),
                    "The result set of launcher job is empty, please examine your job entity, [ 生成LauncherJob为空 ]", null);
        }

        for (ExchangisLauncherJob launcherJob : launcherJobs) {
            String launchName = launcherJob.getLaunchName();
            System.out.println(launcherJob.getJobName());
            System.out.println(launchName);
        }

    }

    public static ExchangisJob getJob() {
        ExchangisJob job = new ExchangisJob();
        job.setId(22L);
        job.setProjectId(1456173825011081218L);
        job.setJobName("T_SQOOP");
        job.setJobType("OFFLINE");
        job.setEngineType("SQOOP");
        job.setJobLabels("");
        job.setJobDesc("");
        job.setContent("[{\n" +
                "    \"subJobName\": \"new\",\n" +
                "    \"dataSources\": {\n" +
                "        \"source_id\": \"HIVE.34.test.psn\",\n" +
                "        \"sink_id\": \"MYSQL.29.test.t_psn\"\n" +
                "    },\n" +
                "    \"params\": {\n" +
                "        \"sources\": [{\n" +
                "            \"config_key\": \"exchangis.job.ds.params.sqoop.hive.r.trans_proto\",\n" +
                "            \"config_name\": \"传输方式\",\n" +
                "            \"config_value\": \"记录\",\n" +
                "            \"sort\": 1\n" +
                "        }, {\n" +
                "            \"config_key\": \"exchangis.job.ds.params.sqoop.hive.r.partition\",\n" +
                "            \"config_name\": \"分区信息\",\n" +
                "            \"config_value\": \"year,month=2018,09\",\n" +
                "            \"sort\": 2\n" +
                "        }, {\n" +
                "            \"config_key\": \"exchangis.job.ds.params.sqoop.hive.r.row_format\",\n" +
                "            \"config_name\": \"字段格式\",\n" +
                "            \"config_value\": \"1\",\n" +
                "            \"sort\": 3\n" +
                "        }],\n" +
                "        \"sinks\": [{\n" +
                "            \"config_key\": \"exchangis.job.ds.params.sqoop.mysql.w.write_type\",\n" +
                "            \"config_name\": \"写入方式\",\n" +
                "            \"config_value\": \"UPDATEONLY\",\n" +
                "            \"sort\": 1\n" +
                "        }, {\n" +
                "            \"config_key\": \"exchangis.job.ds.params.sqoop.mysql.w.batch_size\",\n" +
                "            \"config_name\": \"批量大小\",\n" +
                "            \"config_value\": \"1\",\n" +
                "            \"sort\": 2\n" +
                "        }]\n" +
                "    },\n" +
                "    \"transforms\": {\n" +
                "        \"addEnable\": false,\n" +
                "        \"type\": \"MAPPING\",\n" +
                "        \"sql\": null,\n" +
                "        \"mapping\": [\n" +
                "            {\n" +
                "                \"sink_field_name\": \"id\",\n" +
                "                \"sink_field_type\": \"INT\",\n" +
                "                \"validator\": [],\n" +
                "                \"transformer\": {},\n" +
                "                \"source_field_name\": \"id\",\n" +
                "                \"source_field_type\": \"INT\"\n" +
                "            }, {\n" +
                "                \"sink_field_name\": \"age\",\n" +
                "                \"sink_field_type\": \"INT\",\n" +
                "                \"validator\": [],\n" +
                "                \"transformer\": {},\n" +
                "                \"source_field_name\": \"age\",\n" +
                "                \"source_field_type\": \"INT\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"settings\": [{\n" +
                "        \"config_key\": \"exchangis.sqoop.setting.max.parallelism\",\n" +
                "        \"config_name\": \"作业最大并行数\",\n" +
                "        \"config_value\": \"1\",\n" +
                "        \"sort\": 1\n" +
                "    }, {\n" +
                "        \"config_key\": \"exchangis.sqoop.setting.max.memory\",\n" +
                "        \"config_name\": \"作业最大内存\",\n" +
                "        \"config_value\": \"1\",\n" +
                "        \"sort\": 2\n" +
                "    }]\n" +
                "}]");
        job.setProxyUser("hdfs");
        job.setSyncType("FULL");
        job.setJobParams("{}");
        return job;
    }

    public static Map<String, Object> getDsParam(String id) {
        Map<String, Object> params = new HashMap<>();
        if (id.equals("29")) {
            params.put("host", "192.168.0.66");
            params.put("port", "3306");
            params.put("username", "scm");
            params.put("password", "pass");
        }
        if (id.equals("34")) {
            params.put("host", "192.168.0.111");
            params.put("port", "9083");
            params.put("uris", "thrift://192.168.0.111:9083");
        }
        return params;
    }

}
