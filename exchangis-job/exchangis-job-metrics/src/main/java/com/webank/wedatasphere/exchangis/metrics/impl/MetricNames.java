package com.webank.wedatasphere.exchangis.metrics.impl;

import com.webank.wedatasphere.exchangis.metrics.api.MetricName;

public class MetricNames {

    public static final MetricName TASK_RUNNING_COUNT_METRIC_NAME = MetricName.build("RUNNING").withTitle("运行中的任务数");
    public static final MetricName TASK_SUCCESS_COUNT_METRIC_NAME = MetricName.build("SUCCESS").withTitle("成功的任务数");
    public static final MetricName TASK_FAILED_COUNT_METRIC_NAME = MetricName.build("FAILED").withTitle("失败的任务数");
    public static final MetricName TASK_IDLE_COUNT_METRIC_NAME = MetricName.build("IDLE").withTitle("等待中的任务数");
    public static final MetricName TASK_BUSY_COUNT_METRIC_NAME = MetricName.build("BUSY").withTitle("慢任务任务数");
    public static final MetricName TASK_UNLOCK_COUNT_METRIC_NAME = MetricName.build("UNLOCK").withTitle("等待重试的任务数");

}
