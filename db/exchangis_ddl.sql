-- exchangis_v3.exchangis_job_ds_bind definition

CREATE TABLE `exchangis_job_ds_bind`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `job_id`       bigint(20) NOT NULL,
    `task_index`   int(11)    NOT NULL,
    `source_ds_id` bigint(20) NOT NULL,
    `sink_ds_id`   bigint(20) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;


-- exchangis_v3.exchangis_job_info definition

CREATE TABLE `exchangis_job_info`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `project_id`       bigint(20)   DEFAULT NULL,
    `dss_project_id`   bigint(20)   DEFAULT NULL,
    `dss_project_name` varchar(64)  DEFAULT NULL,
    `node_id`          varchar(64)  DEFAULT NULL,
    `node_name`        varchar(64)  DEFAULT NULL,
    `job_name`         varchar(100) DEFAULT NULL,
    `job_type`         varchar(50)  DEFAULT NULL,
    `task_name`        varchar(255) DEFAULT NULL,
    `engine_type`      varchar(50)  DEFAULT NULL,
    `job_labels`       varchar(255) DEFAULT NULL,
    `job_desc`         varchar(255) DEFAULT NULL,
    `content`          text,
    `alarm_user`       varchar(50)  DEFAULT NULL,
    `alarm_level`      int(255)     DEFAULT NULL,
    `proxy_user`       varchar(50)  DEFAULT NULL,
    `execute_node`     varchar(255) DEFAULT NULL,
    `sync_type`        varchar(50)  DEFAULT NULL,
    `job_params`       text,
    `domain`           varchar(32)  DEFAULT NULL,
    `create_time`      datetime     DEFAULT NULL,
    `create_user`      varchar(50)  DEFAULT NULL,
    `modify_time`      datetime     DEFAULT NULL,
    `modify_user`      varchar(50)  DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_job_info_node_id` (`node_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- exchangis_v3.exchangis_job_param_config definition

CREATE TABLE `exchangis_job_param_config`
(
    `id`               bigint(20)  NOT NULL AUTO_INCREMENT,
    `config_key`       varchar(64) NOT NULL,
    `config_name`      varchar(64) NOT NULL,
    `config_direction` varchar(16)  DEFAULT NULL,
    `type`             varchar(32) NOT NULL,
    `ui_type`          varchar(32)  DEFAULT NULL,
    `ui_field`         varchar(64)  DEFAULT NULL,
    `ui_label`         varchar(32)  DEFAULT NULL,
    `unit`             varchar(32)  DEFAULT NULL,
    `required`         bit(1)       DEFAULT b'0',
    `value_type`       varchar(32)  DEFAULT NULL,
    `value_range`      varchar(255) DEFAULT NULL,
    `default_value`    varchar(255) DEFAULT NULL,
    `validate_type`    varchar(64)  DEFAULT NULL,
    `validate_range`   varchar(64)  DEFAULT NULL,
    `validate_msg`     varchar(255) DEFAULT NULL,
    `is_hidden`        bit(1)       DEFAULT NULL,
    `is_advanced`      bit(1)       DEFAULT NULL,
    `level`            tinyint(4)   DEFAULT NULL,
    `treename`         varchar(32)  DEFAULT NULL,
    `sort`             int(11)      DEFAULT NULL,
    `description`      varchar(255) DEFAULT NULL,
    `status`           tinyint(4)   DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `config_key` (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- exchangis_v3.exchangis_launch_task definition

CREATE TABLE `exchangis_launch_task`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `task_name`     varchar(100) DEFAULT NULL COMMENT '子任务名称',
    `job_id`        bigint(20)   DEFAULT NULL COMMENT '所属任务id',
    `job_name`      varchar(100) DEFAULT NULL COMMENT '所属任务名称',
    `content`       text COMMENT '子任务执行内容',
    `execute_node`  varchar(255) DEFAULT NULL COMMENT '执行节点',
    `create_time`   datetime     DEFAULT NULL COMMENT '子任务创建时间',
    `create_user`   varchar(50)  DEFAULT NULL COMMENT '创建用户',
    `launch_time`   datetime     DEFAULT NULL COMMENT '触发时间',
    `proxy_user`    varchar(50)  DEFAULT NULL COMMENT '执行/代理用户',
    `params_json`   text COMMENT '作业参数',
    `launch_id`     varchar(64)  DEFAULT NULL,
    `status`        varchar(50)  DEFAULT NULL COMMENT '状态：SUCCESS | FAILED | RUNNING | BUSY | IDLE | UNLOCK',
    `complete_time` datetime     DEFAULT NULL COMMENT '完成/中止时间',
    `engine_type`   varchar(64)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- exchangis_v3.exchangis_metric definition

CREATE TABLE `exchangis_metric`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `title`   varchar(128)        DEFAULT NULL COMMENT '指标名称',
    `norm`    varchar(128)        DEFAULT NULL COMMENT '指标key',
    `value`   text COMMENT '指标值',
    `ts`      datetime            DEFAULT NULL COMMENT '采集时间',
    `version` bigint(20) NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `norm` (`norm`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- exchangis_v3.exchangis_project definition

CREATE TABLE `exchangis_project`
(
    `id`               bigint(20)  NOT NULL AUTO_INCREMENT,
    `dss_project_id`   bigint(20)   DEFAULT NULL,
    `workspace_name`   varchar(64)  DEFAULT NULL,
    `dss_name`         varchar(64)  DEFAULT NULL,
    `name`             varchar(64) NOT NULL,
    `description`      varchar(255) DEFAULT NULL,
    `create_time`      datetime     DEFAULT NULL,
    `last_update_time` datetime     DEFAULT NULL,
    `create_by`        varchar(64)  DEFAULT NULL,
    `last_update_by`   varchar(64)  DEFAULT NULL,
    `tags`             varchar(255) DEFAULT NULL,
    `domain`           varchar(32)  DEFAULT NULL,
    `exec_users`       varchar(255) DEFAULT NULL,
    `view_users`       varchar(255) DEFAULT NULL,
    `edit_users`       varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name_UNIQUE` (`name`),
    UNIQUE KEY `uk_project_dss_project_id` (`dss_project_id`),
    UNIQUE KEY `workspace_name_UNIQUE` (`workspace_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- exchangis_v3.exchangis_project_relation definition

CREATE TABLE `exchangis_project_relation`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `project_id`      bigint(20)                   DEFAULT NULL,
    `node_id`         bigint(20)                   DEFAULT NULL,
    `project_version` varchar(32) COLLATE utf8_bin DEFAULT NULL,
    `flow_version`    varchar(32) COLLATE utf8_bin DEFAULT NULL,
    `resource_id`     bigint(20)                   DEFAULT NULL,
    `version`         varchar(32) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_project_relation_project_id_node_id` (`project_id`, `node_id`),
    UNIQUE KEY `uq_project_relation_node_id` (`node_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;