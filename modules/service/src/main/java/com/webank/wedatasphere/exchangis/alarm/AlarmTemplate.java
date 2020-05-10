/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.alarm;


/**
 * @author davidhua
 * 2019/4/18
 */
public class AlarmTemplate {

    public static final String TITLE_TASK_RUN_TIMEOUT = "EXCHANGIS任务作业超时";

    public static final String TITLE_TASK_RUN_FAID = "EXCHANGIS任务作业执行失败";

    public static final String TITLE_TASK_SCHEDULER_ERROR = "EXCHANGIS任务作业调度失败";

    public static final String TITLE_JOB_SCHEDULER_ERROR = "EXCHANGIS任务调度失败";

    public static final String TITLE_TASK_RPC_TIMEOUT = "EXCHANGIS任务作业RPC请求超时";

    public static final String TASK_RUN_TIMEOUT = "任务名称:${jobName}, JOB_ID: ${jobId}, TASK_ID: ${taskId}, 运行超时，设定超时时间(秒/s):${time}，[#{message}] 请登录EXCHANGIS查看详细日志。";

    public static final String TASK_RUN_FAID = "任务名称:${jobName}, JOB_ID: ${jobId}, TASK_ID: ${taskId}, 运行失败，请登录EXCHANGIS查看详细日志。";

    public static final String TASK_SCHEDULER_ERROR = "任务名称:${jobName}, JOB_ID: ${jobId}, TASK_ID: ${taskId}, 调度失败，请联系管理员处理。";

    public static final String JOB_SCHEDULER_ERROR = "任务名称:[${jobName}] 调度失败，具体信息:[${message}], 请联系管理员处理";

    public static final String TASK_RPC_TIMEOUT = "RPC请求远端服务: ${remoteAddress} 超时, 影响任务: ${jobName}, JOB_ID: ${jobId}, TASK_ID: ${taskId}, 请联系管理员核查";

}
