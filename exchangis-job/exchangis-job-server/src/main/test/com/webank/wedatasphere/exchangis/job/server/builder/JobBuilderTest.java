package com.webank.wedatasphere.exchangis.job.server.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.exchangis.job.builder.ExchangisJobBuilderContext;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisEngineJob;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.launcher.domain.LaunchableExchangisTask;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobVO;
import com.webank.wedatasphere.exchangis.job.domain.SubExchangisJob;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.builder.transform.TransformExchangisJob;

import java.util.*;

public class JobBuilderTest {

    private static SpringExchangisJobBuilderManager jobBuilderManager = new SpringExchangisJobBuilderManager();

    static {
        jobBuilderManager.init();
    }

    public static void main(String[] args) throws ExchangisJobException, JsonProcessingException {

        String code = "{\n" +
                "    \"job\": {\n" +
                "        \"content\":[\n" +
                "            {\n" +
                "                \"reader\": {\n" +
                "                    \"name\": \"txtfilereader\", \n" +
                "                    \"parameter\": {\n" +
                "                        \"path\":[\"/opt/install/datax/data/test1.csv\"],\n" +
                "                        \"encoding\":\"gbk\",\n" +
                "                        \"column\": [\n" +
                "                            {\n" +
                "                                \"index\":0,\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"index\":1,\n" +
                "                                \"type\":\"string\"\n" +
                "                            }\n" +
                "                        ], \n" +
                "                        \"fileldDelimiter\":\",\"\n" +
                "                    }\n" +
                "                }, \n" +
                "                \"writer\": {\n" +
                "                    \"name\": \"mysqlwriter\", \n" +
                "                    \"parameter\": {\n" +
                "                        \"username\": \"root\",\n" +
                "                        \"password\": \"MTIzNDU2\", \n" +
                "                        \"column\": [\n" +
                "                            \"i\",\n" +
                "                            \"j\"\n" +
                "                        ],\n" +
                "                        \"preSql\": [], \n" +
                "                        \"connection\": [\n" +
                "                            {\n" +
                "                                \"jdbcUrl\":\"jdbc:mysql://127.0.0.1:3306/test\", \n" +
                "                                \"table\": [\"testtab\"]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        ], \n" +
                "        \"setting\": {\n" +
                "            \"speed\": {\n" +
                "                \"channel\": \"4\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
//        System.out.println(code);

        ExchangisJobInfo job = getSqoopJob();
        System.out.println(job.getName());
        ExchangisJobBuilderContext ctx = new ExchangisJobBuilderContext();
        ctx.putEnv("USER_NAME", "xxxxyyyyzzzz");
        ctx.setOriginalJob(job);
        TransformExchangisJob transformJob = jobBuilderManager.doBuild(job, TransformExchangisJob.class, ctx);
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

    public static ExchangisJobInfo getSqoopJob() {
        ExchangisJobVO job = new ExchangisJobVO();
        job.setId(22L);
        job.setProjectId(1456173825011081218L);
        job.setJobName("T_SQOOP");
        job.setJobType("OFFLINE");
        job.setEngineType("DATAX");
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
        return new ExchangisJobInfo(job);
    }

    public static Map<String, Object> getDsParam(String id) {
        Map<String, Object> params = new HashMap<>();
        if (id.equals("29")) {
            params.put("host", "192.168.0.66");
            params.put("port", "3306");
            params.put("username", "scm");
            params.put("password", "scm_@casc2f");
        }
        if (id.equals("34")) {
            params.put("host", "192.168.0.111");
            params.put("port", "9083");
            params.put("uris", "thrift://192.168.0.111:9083");
        }
        return params;
    }

}
