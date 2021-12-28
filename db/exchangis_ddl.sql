SET NAMES utf8mb4;

DROP TABLE IF EXISTS `exchangis_data_source`;

CREATE TABLE `exchangis_data_source` (
                                         `id` bigint(13) NOT NULL AUTO_INCREMENT,
                                         `source_name` varchar(100) NOT NULL COMMENT 'Data Source Name',
                                         `source_type` varchar(50) DEFAULT NULL COMMENT 'Data Source Type',
                                         `source_desc` varchar(200) DEFAULT NULL,
                                         `owner` varchar(50) DEFAULT 'Exchangis' COMMENT 'Data Source Owner',
                                         `create_user` varchar(50) DEFAULT NULL COMMENT 'Create User',
                                         `parameter` text COMMENT 'Parameters',
                                         `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `modify_user` varchar(50) DEFAULT NULL COMMENT 'Modify User',
                                         `modify_time` datetime DEFAULT NULL COMMENT 'Modify Time',
                                         `model_id` int(11) DEFAULT NULL,
                                         `auth_entity` varchar(200) DEFAULT NULL COMMENT 'Auth Entity',
                                         `auth_creden` varchar(200) DEFAULT NULL COMMENT 'Auth Credential',
                                         `project_id` bigint(13) DEFAULT '0',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_data_source_model
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_data_source_model`;

CREATE TABLE `exchangis_data_source_model` (
                                               `id` bigint(13) NOT NULL AUTO_INCREMENT,
                                               `model_name` varchar(100) NOT NULL COMMENT 'Model Name',
                                               `source_type` varchar(50) DEFAULT NULL COMMENT 'Data Source Type',
                                               `model_desc` varchar(200) DEFAULT NULL COMMENT 'Model Description',
                                               `create_owner` varchar(50) DEFAULT '' COMMENT 'Create Owner',
                                               `create_user` varchar(50) DEFAULT NULL COMMENT 'Create User',
                                               `parameter` text NOT NULL COMMENT 'Parameters',
                                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               `modify_user` varchar(50) DEFAULT NULL COMMENT 'Modify User',
                                               `modify_time` datetime DEFAULT NULL COMMENT 'Modify Time',
                                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_data_source_owner
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_data_source_owner`;

CREATE TABLE `exchangis_data_source_owner` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT,
                                               `owner_name` varchar(50) NOT NULL COMMENT 'Owner Name',
                                               `owner_desc` varchar(200) DEFAULT NULL COMMENT 'Owner Description',
                                               `create_user` varchar(20) DEFAULT NULL COMMENT 'Create User',
                                               `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
                                               `modify_user` varchar(20) DEFAULT NULL COMMENT 'Modify User',
                                               `modify_time` datetime DEFAULT NULL COMMENT 'Modify Time',
                                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# 转储表 exchangis_data_source_permissions
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_data_source_permissions`;

CREATE TABLE `exchangis_data_source_permissions` (
                                                     `data_source_id` bigint(13) NOT NULL,
                                                     `access_readable` tinyint(1) DEFAULT '0',
                                                     `access_writable` tinyint(1) DEFAULT '0',
                                                     `access_editable` tinyint(1) DEFAULT '0',
                                                     `access_executable` tinyint(1) DEFAULT '0',
                                                     `modify_time` datetime DEFAULT NULL COMMENT 'Modify Time',
                                                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                                                     PRIMARY KEY (`data_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_executor_node
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_executor_node`;

CREATE TABLE `exchangis_executor_node` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `address` varchar(20) NOT NULL COMMENT 'Address',
                                           `heartbeat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           `status` int(11) NOT NULL DEFAULT '0' COMMENT 'Status:0-Up，1-Down',
                                           `mem_rate` float DEFAULT '0' COMMENT 'Memory Usage',
                                           `cpu_rate` float DEFAULT '0' COMMENT 'CPU Usage',
                                           `default_node` tinyint(2) DEFAULT NULL COMMENT 'Default Node',
                                           PRIMARY KEY (`id`),
                                           KEY `addres_index` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_executor_node_tab
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_executor_node_tab`;

CREATE TABLE `exchangis_executor_node_tab` (
                                               `exec_node_id` int(11) NOT NULL COMMENT 'Excutor Node ID',
                                               `tab_id` int(11) NOT NULL COMMENT 'Tab ID',
                                               `tab_name` varchar(200) NOT NULL COMMENT 'Tab Name',
                                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                                               PRIMARY KEY (`exec_node_id`,`tab_id`),
                                               KEY `tab_name` (`tab_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_executor_node_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_executor_node_user`;

CREATE TABLE `exchangis_executor_node_user` (
                                                `exec_node_id` int(11) NOT NULL COMMENT 'Excutor Node ID',
                                                `exec_user` varchar(50) NOT NULL COMMENT 'Executive User',
                                                `user_type` varchar(50) DEFAULT '' COMMENT 'User Type',
                                                `relation_state` int(2) NOT NULL DEFAULT '0' COMMENT 'Relation State : 0-UnRelated, 1-Relate Success, 2-Relate Fail',
                                                `uid` int(4) DEFAULT NULL COMMENT 'Machine User ID',
                                                `gid` int(4) DEFAULT NULL COMMENT 'Machine Group ID',
                                                `mark_del` tinyint(4) DEFAULT '0' COMMENT 'Mark Delete',
                                                `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
                                                PRIMARY KEY (`exec_node_id`,`exec_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_executor_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_executor_user`;

CREATE TABLE `exchangis_executor_user` (
                                           `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                           `exec_user` varchar(50) NOT NULL COMMENT 'Executive User',
                                           `description` varchar(200) DEFAULT '' COMMENT 'Description',
                                           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `exec_user` (`exec_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_group`;

CREATE TABLE `exchangis_group` (
                                   `id` bigint(13) NOT NULL AUTO_INCREMENT,
                                   `group_name` varchar(50) NOT NULL COMMENT 'Group Name',
                                   `group_desc` varchar(100) DEFAULT NULL,
                                   `ref_project_id` bigint(13) DEFAULT '0',
                                   `create_user` varchar(50) DEFAULT NULL,
                                   `create_time` datetime NOT NULL,
                                   `modify_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_job_ds_bind
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_ds_bind`;

CREATE TABLE `exchangis_job_ds_bind` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `job_id` bigint(20) NOT NULL,
                                         `task_index` int(11) NOT NULL,
                                         `source_ds_id` bigint(20) NOT NULL,
                                         `sink_ds_id` bigint(20) NOT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


# 转储表 exchangis_job_exec
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_exec`;

CREATE TABLE `exchangis_job_exec` (
                                      `job_id` bigint(20) NOT NULL COMMENT 'Job ID',
                                      `exec_id` int(11) NOT NULL COMMENT 'Executor Node ID',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`job_id`,`exec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_job_func
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_func`;

CREATE TABLE `exchangis_job_func` (
                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                      `func_type` varchar(50) NOT NULL,
                                      `func_name` varchar(100) NOT NULL,
                                      `tab_name` varchar(50) NOT NULL COMMENT 'Tab',
                                      `name_dispaly` varchar(100) DEFAULT NULL,
                                      `param_num` int(11) DEFAULT '0',
                                      `ref_name` varchar(100) DEFAULT NULL,
                                      `description` varchar(200) DEFAULT NULL,
                                      `modify_time` datetime DEFAULT NULL,
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `job_func_tab_name_idx` (`tab_name`,`func_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# 转储表 exchangis_job_func_params
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_func_params`;

CREATE TABLE `exchangis_job_func_params` (
                                             `func_id` int(11) NOT NULL,
                                             `param_name` varchar(100) NOT NULL,
                                             `order` int(11) DEFAULT '0',
                                             `name_display` varchar(100) DEFAULT NULL,
                                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             PRIMARY KEY (`func_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# 转储表 exchangis_job_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_info`;

CREATE TABLE `exchangis_job_info` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `project_id` bigint(20) DEFAULT NULL,
                                      `dss_project_id` bigint(20) DEFAULT NULL,
                                      `dss_project_name` varchar(64) DEFAULT NULL,
                                      `node_id` varchar(64) DEFAULT NULL,
                                      `node_name` varchar(64) DEFAULT NULL,
                                      `job_name` varchar(100) DEFAULT NULL,
                                      `job_type` varchar(50) DEFAULT NULL,
                                      `task_name` varchar(255) DEFAULT NULL,
                                      `engine_type` varchar(50) DEFAULT NULL,
                                      `job_labels` varchar(255) DEFAULT NULL,
                                      `job_desc` varchar(255) DEFAULT NULL,
                                      `content` text,
                                      `alarm_user` varchar(50) DEFAULT NULL,
                                      `alarm_level` int(255) DEFAULT NULL,
                                      `proxy_user` varchar(50) DEFAULT NULL,
                                      `execute_node` varchar(255) DEFAULT NULL,
                                      `sync_type` varchar(50) DEFAULT NULL,
                                      `job_params` text,
                                      `domain` varchar(32) DEFAULT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      `create_user` varchar(50) DEFAULT NULL,
                                      `modify_time` datetime DEFAULT NULL,
                                      `modify_user` varchar(50) DEFAULT NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_job_info_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# 转储表 exchangis_job_info_params
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_info_params`;

CREATE TABLE `exchangis_job_info_params` (
                                             `job_id` bigint(13) NOT NULL,
                                             `param_name` varchar(100) NOT NULL,
                                             `param_val` varchar(200) DEFAULT '',
                                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             PRIMARY KEY (`job_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_job_param_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_param_config`;

CREATE TABLE `exchangis_job_param_config` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                              `config_key` varchar(64) NOT NULL,
                                              `config_name` varchar(64) NOT NULL,
                                              `config_direction` varchar(16) DEFAULT NULL,
                                              `type` varchar(32) NOT NULL,
                                              `ui_type` varchar(32) DEFAULT NULL,
                                              `ui_field` varchar(64) DEFAULT NULL,
                                              `ui_label` varchar(32) DEFAULT NULL,
                                              `unit` varchar(32) DEFAULT NULL,
                                              `required` bit(1) DEFAULT b'0',
                                              `value_type` varchar(32) DEFAULT NULL,
                                              `value_range` varchar(255) DEFAULT NULL,
                                              `default_value` varchar(255) DEFAULT NULL,
                                              `validate_type` varchar(64) DEFAULT NULL,
                                              `validate_range` varchar(64) DEFAULT NULL,
                                              `validate_msg` varchar(255) DEFAULT NULL,
                                              `is_hidden` bit(1) DEFAULT NULL,
                                              `is_advanced` bit(1) DEFAULT NULL,
                                              `level` tinyint(4) DEFAULT NULL,
                                              `treename` varchar(32) DEFAULT NULL,
                                              `sort` int(11) DEFAULT NULL,
                                              `description` varchar(255) DEFAULT NULL,
                                              `status` tinyint(4) DEFAULT NULL,
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `exchangis_job_param_config` WRITE;
/*!40000 ALTER TABLE `exchangis_job_param_config` DISABLE KEYS */;


/*!40000 ALTER TABLE `exchangis_job_param_config` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 exchangis_job_proc
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_proc`;

CREATE TABLE `exchangis_job_proc` (
                                      `job_id` bigint(13) NOT NULL,
                                      `proc_src_code` text,
                                      `language` varchar(20) NOT NULL DEFAULT 'java',
                                      PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_job_report
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_report`;

CREATE TABLE `exchangis_job_report` (
                                        `id` bigint(13) NOT NULL,
                                        `job_id` bigint(13) DEFAULT NULL,
                                        `total_costs` double DEFAULT NULL COMMENT 'Cost Time',
                                        `byte_speed_per_second` bigint(20) DEFAULT NULL,
                                        `record_speed_per_second` bigint(20) DEFAULT NULL,
                                        `total_read_records` bigint(20) DEFAULT NULL,
                                        `total_read_bytes` bigint(20) DEFAULT NULL,
                                        `total_error_records` bigint(20) DEFAULT NULL,
                                        `transformer_total_records` bigint(20) DEFAULT NULL,
                                        `transformer_failed_records` bigint(20) DEFAULT NULL,
                                        `transformer_filter_records` bigint(20) DEFAULT NULL,
                                        `create_time` datetime DEFAULT NULL,
                                        PRIMARY KEY (`id`),
                                        KEY `job_id_index` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_job_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_task`;

CREATE TABLE `exchangis_job_task` (
                                      `id` bigint(13) NOT NULL,
                                      `executer_address` varchar(100) DEFAULT NULL COMMENT 'Executor Address',
                                      `job_id` bigint(13) DEFAULT NULL,
                                      `job_name` varchar(100) DEFAULT NULL,
                                      `job_create_user` varchar(50) DEFAULT NULL,
                                      `job_alarm_user` varchar(255) DEFAULT NULL,
                                      `trigger_type` varchar(20) DEFAULT NULL COMMENT 'Trigger Type',
                                      `trigger_time` datetime NOT NULL COMMENT 'Trigger Time',
                                      `trigger_status` varchar(20) DEFAULT NULL COMMENT 'Trigger Status',
                                      `trigger_msg` varchar(255) DEFAULT NULL COMMENT 'Trigger Log',
                                      `operater` varchar(50) DEFAULT NULL COMMENT 'Operator',
                                      `status` varchar(50) DEFAULT NULL COMMENT 'Status ,such as: kill,sucess,failed',
                                      `run_times` int(11) DEFAULT NULL COMMENT 'Run Times',
                                      `execute_msg` varchar(1000) DEFAULT NULL COMMENT 'Execute Msg',
                                      `complete_time` datetime DEFAULT NULL COMMENT 'Complete Time',
                                      `disposable` tinyint(2) DEFAULT '0',
                                      `version` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      `exec_user` varchar(50) DEFAULT '',
                                      `project_id` bigint(13) DEFAULT '0' COMMENT 'Project Related',
                                      `state_speed` bigint(20) DEFAULT NULL,
                                      `speed_limit_mb` int(12) DEFAULT '0',
                                      PRIMARY KEY (`id`),
                                      KEY `job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_job_task_params
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_job_task_params`;

CREATE TABLE `exchangis_job_task_params` (
                                             `task_id` bigint(11) NOT NULL,
                                             `param_name` varchar(100) NOT NULL,
                                             `param_val` varchar(100) DEFAULT '',
                                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             PRIMARY KEY (`task_id`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_launch_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_launch_task`;

CREATE TABLE `exchangis_launch_task` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `task_name` varchar(100) DEFAULT NULL COMMENT '子任务名称',
                                         `job_id` bigint(20) DEFAULT NULL COMMENT '所属任务id',
                                         `job_name` varchar(100) DEFAULT NULL COMMENT '所属任务名称',
                                         `content` text COMMENT '子任务执行内容',
                                         `execute_node` varchar(255) DEFAULT NULL COMMENT '执行节点',
                                         `create_time` datetime DEFAULT NULL COMMENT '子任务创建时间',
                                         `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
                                         `launch_time` datetime DEFAULT NULL COMMENT '触发时间',
                                         `proxy_user` varchar(50) DEFAULT NULL COMMENT '执行/代理用户',
                                         `params_json` text COMMENT '作业参数',
                                         `launch_id` varchar(64) DEFAULT NULL,
                                         `status` varchar(50) DEFAULT NULL COMMENT '状态：SUCCESS | FAILED | RUNNING | BUSY | IDLE | UNLOCK',
                                         `complete_time` datetime DEFAULT NULL COMMENT '完成/中止时间',
                                         `engine_type` varchar(64) DEFAULT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_metric
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_metric`;

CREATE TABLE `exchangis_metric` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `title` varchar(128) DEFAULT NULL COMMENT '指标名称',
                                    `norm` varchar(128) DEFAULT NULL COMMENT '指标key',
                                    `value` text COMMENT '指标值',
                                    `ts` datetime DEFAULT NULL COMMENT '采集时间',
                                    `version` bigint(20) NOT NULL DEFAULT '1',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `norm` (`norm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# 转储表 exchangis_project
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_project`;

CREATE TABLE `exchangis_project` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `dss_project_id` bigint(20) DEFAULT NULL,
                                     `workspace_name` varchar(64) DEFAULT NULL,
                                     `dss_name` varchar(64) DEFAULT NULL,
                                     `name` varchar(64) NOT NULL,
                                     `description` varchar(255) DEFAULT NULL,
                                     `create_time` datetime DEFAULT NULL,
                                     `last_update_time` datetime DEFAULT NULL,
                                     `create_by` varchar(64) DEFAULT NULL,
                                     `last_update_by` varchar(64) DEFAULT NULL,
                                     `tags` varchar(255) DEFAULT NULL,
                                     `domain` varchar(32) DEFAULT NULL,
                                     `exec_users` varchar(255) DEFAULT NULL,
                                     `view_users` varchar(255) DEFAULT NULL,
                                     `edit_users` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `name_UNIQUE` (`name`),
                                     UNIQUE KEY `uk_project_dss_project_id` (`dss_project_id`),
                                     UNIQUE KEY `workspace_name_UNIQUE` (`workspace_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_project_relation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_project_relation`;

CREATE TABLE `exchangis_project_relation` (
                                              `id` int(11) NOT NULL AUTO_INCREMENT,
                                              `project_id` bigint(20) DEFAULT NULL,
                                              `node_id` bigint(20) DEFAULT NULL,
                                              `project_version` varchar(32) COLLATE utf8_bin DEFAULT NULL,
                                              `flow_version` varchar(32) COLLATE utf8_bin DEFAULT NULL,
                                              `resource_id` bigint(20) DEFAULT NULL,
                                              `version` varchar(32) COLLATE utf8_bin DEFAULT NULL,
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `uq_project_relation_project_id_node_id` (`project_id`,`node_id`),
                                              UNIQUE KEY `uq_project_relation_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# 转储表 EXCHANGIS_QRTZ_BLOB_TRIGGERS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_BLOB_TRIGGERS`;

CREATE TABLE `EXCHANGIS_QRTZ_BLOB_TRIGGERS` (
                                                `SCHED_NAME` varchar(120) NOT NULL,
                                                `TRIGGER_NAME` varchar(190) NOT NULL,
                                                `TRIGGER_GROUP` varchar(190) NOT NULL,
                                                `BLOB_DATA` blob,
                                                PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                                KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                                CONSTRAINT `EXCHANGIS_QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `EXCHANGIS_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_CALENDARS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_CALENDARS`;

CREATE TABLE `EXCHANGIS_QRTZ_CALENDARS` (
                                            `SCHED_NAME` varchar(120) NOT NULL,
                                            `CALENDAR_NAME` varchar(190) NOT NULL,
                                            `CALENDAR` blob NOT NULL,
                                            PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_CRON_TRIGGERS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_CRON_TRIGGERS`;

CREATE TABLE `EXCHANGIS_QRTZ_CRON_TRIGGERS` (
                                                `SCHED_NAME` varchar(120) NOT NULL,
                                                `TRIGGER_NAME` varchar(190) NOT NULL,
                                                `TRIGGER_GROUP` varchar(190) NOT NULL,
                                                `CRON_EXPRESSION` varchar(120) NOT NULL,
                                                `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
                                                PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                                CONSTRAINT `EXCHANGIS_QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `EXCHANGIS_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_FIRED_TRIGGERS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_FIRED_TRIGGERS`;

CREATE TABLE `EXCHANGIS_QRTZ_FIRED_TRIGGERS` (
                                                 `SCHED_NAME` varchar(120) NOT NULL,
                                                 `ENTRY_ID` varchar(95) NOT NULL,
                                                 `TRIGGER_NAME` varchar(190) NOT NULL,
                                                 `TRIGGER_GROUP` varchar(190) NOT NULL,
                                                 `INSTANCE_NAME` varchar(190) NOT NULL,
                                                 `FIRED_TIME` bigint(13) NOT NULL,
                                                 `SCHED_TIME` bigint(13) NOT NULL,
                                                 `PRIORITY` int(11) NOT NULL,
                                                 `STATE` varchar(16) NOT NULL,
                                                 `JOB_NAME` varchar(190) DEFAULT NULL,
                                                 `JOB_GROUP` varchar(190) DEFAULT NULL,
                                                 `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
                                                 `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
                                                 PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
                                                 KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
                                                 KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
                                                 KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
                                                 KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
                                                 KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                                 KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_JOB_DETAILS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_JOB_DETAILS`;

CREATE TABLE `EXCHANGIS_QRTZ_JOB_DETAILS` (
                                              `SCHED_NAME` varchar(120) NOT NULL,
                                              `JOB_NAME` varchar(190) NOT NULL,
                                              `JOB_GROUP` varchar(190) NOT NULL,
                                              `DESCRIPTION` varchar(250) DEFAULT NULL,
                                              `JOB_CLASS_NAME` varchar(250) NOT NULL,
                                              `IS_DURABLE` varchar(1) NOT NULL,
                                              `IS_NONCONCURRENT` varchar(1) NOT NULL,
                                              `IS_UPDATE_DATA` varchar(1) NOT NULL,
                                              `REQUESTS_RECOVERY` varchar(1) NOT NULL,
                                              `JOB_DATA` blob,
                                              PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
                                              KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
                                              KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_LOCKS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_LOCKS`;

CREATE TABLE `EXCHANGIS_QRTZ_LOCKS` (
                                        `SCHED_NAME` varchar(120) NOT NULL,
                                        `LOCK_NAME` varchar(40) NOT NULL,
                                        PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_PAUSED_TRIGGER_GRPS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_PAUSED_TRIGGER_GRPS`;

CREATE TABLE `EXCHANGIS_QRTZ_PAUSED_TRIGGER_GRPS` (
                                                      `SCHED_NAME` varchar(120) NOT NULL,
                                                      `TRIGGER_GROUP` varchar(190) NOT NULL,
                                                      PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_SCHEDULER_STATE
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_SCHEDULER_STATE`;

CREATE TABLE `EXCHANGIS_QRTZ_SCHEDULER_STATE` (
                                                  `SCHED_NAME` varchar(120) NOT NULL,
                                                  `INSTANCE_NAME` varchar(190) NOT NULL,
                                                  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
                                                  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
                                                  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_SIMPLE_TRIGGERS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_SIMPLE_TRIGGERS`;

CREATE TABLE `EXCHANGIS_QRTZ_SIMPLE_TRIGGERS` (
                                                  `SCHED_NAME` varchar(120) NOT NULL,
                                                  `TRIGGER_NAME` varchar(190) NOT NULL,
                                                  `TRIGGER_GROUP` varchar(190) NOT NULL,
                                                  `REPEAT_COUNT` bigint(7) NOT NULL,
                                                  `REPEAT_INTERVAL` bigint(12) NOT NULL,
                                                  `TIMES_TRIGGERED` bigint(10) NOT NULL,
                                                  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                                  CONSTRAINT `EXCHANGIS_QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `EXCHANGIS_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_SIMPROP_TRIGGERS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_SIMPROP_TRIGGERS`;

CREATE TABLE `EXCHANGIS_QRTZ_SIMPROP_TRIGGERS` (
                                                   `SCHED_NAME` varchar(120) NOT NULL,
                                                   `TRIGGER_NAME` varchar(190) NOT NULL,
                                                   `TRIGGER_GROUP` varchar(190) NOT NULL,
                                                   `STR_PROP_1` varchar(512) DEFAULT NULL,
                                                   `STR_PROP_2` varchar(512) DEFAULT NULL,
                                                   `STR_PROP_3` varchar(512) DEFAULT NULL,
                                                   `INT_PROP_1` int(11) DEFAULT NULL,
                                                   `INT_PROP_2` int(11) DEFAULT NULL,
                                                   `LONG_PROP_1` bigint(20) DEFAULT NULL,
                                                   `LONG_PROP_2` bigint(20) DEFAULT NULL,
                                                   `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
                                                   `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
                                                   `BOOL_PROP_1` varchar(1) DEFAULT NULL,
                                                   `BOOL_PROP_2` varchar(1) DEFAULT NULL,
                                                   PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                                   CONSTRAINT `EXCHANGIS_QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `EXCHANGIS_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 EXCHANGIS_QRTZ_TRIGGERS
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EXCHANGIS_QRTZ_TRIGGERS`;

CREATE TABLE `EXCHANGIS_QRTZ_TRIGGERS` (
                                           `SCHED_NAME` varchar(120) NOT NULL,
                                           `TRIGGER_NAME` varchar(190) NOT NULL,
                                           `TRIGGER_GROUP` varchar(190) NOT NULL,
                                           `JOB_NAME` varchar(190) NOT NULL,
                                           `JOB_GROUP` varchar(190) NOT NULL,
                                           `DESCRIPTION` varchar(250) DEFAULT NULL,
                                           `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
                                           `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
                                           `PRIORITY` int(11) DEFAULT NULL,
                                           `TRIGGER_STATE` varchar(16) NOT NULL,
                                           `TRIGGER_TYPE` varchar(8) NOT NULL,
                                           `START_TIME` bigint(13) NOT NULL,
                                           `END_TIME` bigint(13) DEFAULT NULL,
                                           `CALENDAR_NAME` varchar(190) DEFAULT NULL,
                                           `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
                                           `JOB_DATA` blob,
                                           PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                           KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
                                           KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
                                           KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
                                           KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
                                           KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
                                           KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
                                           KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
                                           KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
                                           KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
                                           KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
                                           KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
                                           KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
                                           CONSTRAINT `EXCHANGIS_QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `EXCHANGIS_QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_queue_elements
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_queue_elements`;

CREATE TABLE `exchangis_queue_elements` (
                                            `sid` bigint(11) NOT NULL COMMENT 'Seq ID',
                                            `qid` int(11) NOT NULL COMMENT 'Queue ID',
                                            `status` int(11) NOT NULL COMMENT 'Element Status',
                                            `enq_time` datetime DEFAULT NULL COMMENT 'Enque Time',
                                            `poll_time` datetime DEFAULT NULL COMMENT 'Poll Time',
                                            `enq_count` int(1) DEFAULT '1',
                                            `delay_time` datetime DEFAULT NULL,
                                            `delay_count` int(1) DEFAULT '0',
                                            `version` int(2) DEFAULT '0' COMMENT 'Version',
                                            `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`sid`),
                                            KEY `enq_time` (`enq_time`),
                                            KEY `poll_time` (`poll_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_queue_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_queue_info`;

CREATE TABLE `exchangis_queue_info` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `qname` varchar(50) NOT NULL COMMENT 'Queue Name',
                                        `description` varchar(200) DEFAULT '',
                                        `priority` int(11) NOT NULL DEFAULT '-1',
                                        `is_lock` tinyint(1) DEFAULT '0',
                                        `lock_host` varchar(50) DEFAULT NULL,
                                        `lock_time` datetime DEFAULT NULL,
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_tab
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_tab`;

CREATE TABLE `exchangis_tab` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `name` varchar(200) NOT NULL COMMENT 'Tab Name',
                                 `description` varchar(200) DEFAULT '' COMMENT 'Desc',
                                 `type` int(4) DEFAULT NULL COMMENT 'Tab Type',
                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_user_exec
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_user_exec`;

CREATE TABLE `exchangis_user_exec` (
                                       `app_user` varchar(50) NOT NULL COMMENT 'APP User',
                                       `exec_user` varchar(50) NOT NULL COMMENT 'Executive User',
                                       PRIMARY KEY (`app_user`,`exec_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_user_exec_node
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_user_exec_node`;

CREATE TABLE `exchangis_user_exec_node` (
                                            `app_user` varchar(50) NOT NULL COMMENT 'APP User',
                                            `exec_node_id` int(11) NOT NULL COMMENT 'Executor Node ID',
                                            `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`app_user`,`exec_node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_user_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_user_group`;

CREATE TABLE `exchangis_user_group` (
                                        `user_name` varchar(50) NOT NULL COMMENT 'User Name',
                                        `group_id` int(11) NOT NULL COMMENT 'Group ID',
                                        `join_role` int(4) DEFAULT '0' COMMENT 'Join Role',
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                                        PRIMARY KEY (`user_name`,`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 exchangis_user_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `exchangis_user_info`;

CREATE TABLE `exchangis_user_info` (
                                       `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                       `username` varchar(50) NOT NULL,
                                       `password` varchar(200) DEFAULT '',
                                       `user_type` int(11) DEFAULT '0',
                                       `org_code` varchar(50) DEFAULT '',
                                       `dept_code` varchar(50) DEFAULT '',
                                       `update_time` datetime NOT NULL,
                                       `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
