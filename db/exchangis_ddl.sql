-- exchangis_job_ds_bind definition
CREATE TABLE IF NOT EXISTS `exchangis_job_ds_bind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `task_index` int(11) NOT NULL,
  `source_ds_id` bigint(20) NOT NULL,
  `sink_ds_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_job_entity definition
CREATE TABLE IF NOT EXISTS `exchangis_job_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
  `engine_type` varchar(45) DEFAULT '',
  `job_labels` varchar(255) DEFAULT NULL,
  `create_user` varchar(100) DEFAULT NULL,
  `job_content` mediumtext,
  `execute_user` varchar(100) DEFAULT '',
  `job_params` text,
  `job_desc` varchar(255) DEFAULT NULL,
  `job_type` varchar(50) DEFAULT NULL,
  `project_id` bigint(13) DEFAULT NULL,
  `source` text,
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_job_param_config definition
CREATE TABLE IF NOT EXISTS `exchangis_job_param_config` (
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
  `source` varchar(255) DEFAULT NULL,
  `level` tinyint(4) DEFAULT NULL,
  `treename` varchar(32) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_project_info definition
CREATE TABLE IF NOT EXISTS `exchangis_project_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_user` varchar(64) DEFAULT NULL,
  `last_update_user` varchar(64) DEFAULT NULL,
  `project_labels` varchar(255) DEFAULT NULL,
  `domain` varchar(32) DEFAULT NULL,
  `exec_users` varchar(255) DEFAULT '',
  `view_users` varchar(255) DEFAULT '',
  `edit_users` varchar(255) DEFAULT '',
  `source` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_project_user definition
CREATE TABLE IF NOT EXISTS `exchangis_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `priv_user` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `exchangis_project_user_un` (`project_id`,`priv_user`,`priv`),
  KEY `idx_priv_user` (`priv_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_launchable_task definition
CREATE TABLE IF NOT EXISTS `exchangis_launchable_task` (
  `id` bigint(13) NOT NULL,
  `name` varchar(100) NOT NULL,
  `job_execution_id` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
  `engine_type` varchar(45) DEFAULT '',
  `execute_user` varchar(50) DEFAULT '',
  `linkis_job_name` varchar(100) NOT NULL,
  `linkis_job_content` mediumtext NOT NULL,
  `linkis_params` text DEFAULT NULL,
  `linkis_source` varchar(64) DEFAULT NULL,
  `rate_params` text DEFAULT NULL,
  `source_type` varchar(63) DEFAULT NULL,
  `sink_type` varchar(63) DEFAULT NULL,
  `source_id` varchar(255) DEFAULT NULL,
  `sink_id` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `labels` varchar(64) DEFAULT NULL,
  `delay_time` datetime DEFAULT NULL,
  `delay_count` int(3) DEFAULT 0,
  `instance` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_launched_job_entity definition
CREATE TABLE IF NOT EXISTS `exchangis_launched_job_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
  `job_id` bigint(20) DEFAULT NULL,
  `launchable_task_num` int(20) DEFAULT '0',
  `engine_type` varchar(100) DEFAULT NULL,
  `execute_user` varchar(100) DEFAULT NULL,
  `job_name` varchar(100) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `progress` varchar(100) DEFAULT NULL,
  `error_code` varchar(64) DEFAULT NULL,
  `error_msg` varchar(255) DEFAULT NULL,
  `retry_num` bigint(10) DEFAULT NULL,
  `job_execution_id` varchar(100) DEFAULT NULL,
  `log_path` varchar(255) DEFAULT NULL,
  `create_user` varchar(100) DEFAULT NULL,
  `instance` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_execution_id_UNIQUE` (`job_execution_id`),
  KEY `idx_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT;

-- exchangis_launched_task_entity definition
CREATE TABLE IF NOT EXISTS `exchangis_launched_task_entity` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
  `job_id` bigint(20) DEFAULT NULL,
  `engine_type` varchar(100) DEFAULT NULL,
  `source_type` varchar(63) DEFAULT NULL,
  `sink_type` varchar(63) DEFAULT NULL,
  `source_id` varchar(255) DEFAULT NULL,
  `sink_id` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `execute_user` varchar(100) DEFAULT NULL,
  `job_name` varchar(100) DEFAULT NULL,
  `progress` varchar(64) DEFAULT NULL,
  `error_code` varchar(64) DEFAULT NULL,
  `error_msg` varchar(255) DEFAULT NULL,
  `retry_num` bigint(10) DEFAULT NULL,
  `task_id` varchar(64) DEFAULT NULL,
  `linkis_job_id` varchar(200) DEFAULT NULL,
  `linkis_job_info` varchar(1000) DEFAULT NULL,
  `job_execution_id` varchar(100) DEFAULT NULL,
  `launch_time` datetime DEFAULT NULL,
  `running_time` datetime DEFAULT NULL,
  `metrics` text,
  `status` varchar(64) DEFAULT NULL,
  `commit_version` int(13) DEFAULT 0,
  `instance` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_job_exec_id` (`job_execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_job_func definition
CREATE TABLE IF NOT EXISTS `exchangis_job_func` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_job_func_params definition
CREATE TABLE IF NOT EXISTS `exchangis_job_func_params`(
    `func_id` INT(11) NOT NULL,
    `param_name` VARCHAR(100) NOT NULL,
    `order`  INT(11) DEFAULT 0,
    `name_display` VARCHAR(100),
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(`func_id`, `param_name`)
)Engine=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_engine_resources definition
CREATE TABLE IF NOT EXISTS `exchangis_engine_resources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `engine_type` varchar(50) NOT NULL,
  `resource_name` varchar(100) NOT NULL,
  `resource_type` varchar(10) NOT NULL DEFAULT 'file' COMMENT 'resource type',
  `resource_path` varchar(190) NOT NULL,
  `store_uri` varchar(500) NOT NULL,
  `create_user` varchar(50) NOT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `engine_res_idx` (`engine_type`,`resource_path`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- exchangis_engine_settings definition
CREATE TABLE IF NOT EXISTS `exchangis_engine_settings` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `engine_name` varchar(50) NOT NULL,
   `engine_desc` varchar(500) NOT NULL,
   `engine_settings_value` text,
   `engine_direction` text NOT NULL,
   `res_loader_class` varchar(255),
   `res_uploader_class` varchar(255),
   `modify_time` datetime DEFAULT NULL,
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`),
   UNIQUE KEY `engine_setting_idx` (`engine_name`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_job_transform_rule
CREATE TABLE IF NOT EXISTS `exchangis_job_transform_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_name` varchar(100) NOT NULL DEFAULT 'transform_rule',
  `rule_type` varchar(64) NOT NULL DEFAULT 'DEF',
  `rule_source` varchar(600) DEFAULT '{}',
  `data_source_type` varchar(64) NOT NULL,
  `engine_type` varchar(32),
  `direction` varchar(32) NOT NULL DEFAULT 'NONE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_job_transform_processor
CREATE TABLE IF NOT EXISTS `exchangis_job_transform_processor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `code_content` text DEFAULT NULL,
  `code_language` varchar(32) NOT NULL DEFAULT 'java',
  `code_bml_resourceId` varchar(255) COMMENT 'BML resource id',
  `code_bml_version` varchar(255) COMMENT 'BML version',
  `creator` varchar(50) NOT NULL COMMENT 'Owner of processor',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- exchangis_project_ds
CREATE TABLE IF NOT EXISTS `exchangis_project_ds` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `data_source_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `data_source_id` bigint(20) DEFAULT NULL,
  `data_source_type` varchar(50) COLLATE utf8_bin NOT NULL,
  `data_source_creator` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `exchangis_project_ds_un` (`project_id`,`data_source_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `exchangis_rate_limit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `limit_realm_id` bigint(20) NOT NULL COMMENT '关联领域ID',
  `limit_realm` varchar(31) NOT NULL DEFAULT '关联领域，默认为数据源模板MODEL',
  `flow_rate_limit` int(11) DEFAULT NULL COMMENT '速率限制数',
  `flow_rate_limit_unit` varchar(15) DEFAULT 'M' COMMENT '速率限制单位',
  `record_rate_limit` int(11) DEFAULT NULL COMMENT '记录数限速数',
  `parallel_limit` int(11) DEFAULT NULL COMMENT '并行度限制数',
  `open_limit` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否开启限制,1 with open,0 with close',
  `create_user` varchar(63) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` varchar(63) DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `limit_realm_idx` (`limit_realm`,`limit_realm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='速率限制表';

CREATE TABLE IF NOT EXISTS `exchangis_rate_limit_used` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rate_limit_id` bigint(20) NOT NULL COMMENT '限速信息ID',
  `rate_limit_type` varchar(31) NOT NULL COMMENT '限速类型',
  `rate_limit_used` int(11) NOT NULL DEFAULT '0' COMMENT '已使用数量',
  `rate_limit_total` int(11) NOT NULL COMMENT '可使用总量',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `rate_limit_idx` (`rate_limit_id`,`rate_limit_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='限速使用表';

CREATE TABLE IF NOT EXISTS `exchangis_data_source_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `model_name` varchar(100) NOT NULL COMMENT '模板名称',
  `source_type` varchar(50) DEFAULT NULL COMMENT '数据源类型',
  `model_desc` varchar(200) DEFAULT NULL COMMENT '模板描述',
  `ref_model_id` bigint(20) NOT NULL COMMENT '原始数据源模板ID',
  `is_duplicate` tinyint DEFAULT '0' COMMENT '是否重复',
  `version` int(11) NOT NULL DEFAULT 1 COMMENT '版本号',
  `create_owner` varchar(50) DEFAULT '' COMMENT '模版属主',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `parameter` text NOT NULL COMMENT '模板配置参数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_model_name` (`model_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据源模板表';

CREATE TABLE IF NOT EXISTS `exchangis_data_source_model_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` bigint(20) NOT NULL COMMENT '模板ID',
  `ds_id` bigint(20) NOT NULL COMMENT '数据源ID',
  `ds_name` varchar(100) DEFAULT NULL COMMENT '数据源名称',
  `ds_version` bigint(20) NOT NULL COMMENT '数据源版本',
  `create_user` varchar(63) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` varchar(63) DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_model_ds` (`ds_id`,`ds_version`,`model_id`),
  KEY `idx_model_id` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据源模板绑定表';

CREATE TABLE IF NOT EXISTS `exchangis_data_source_model_type_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ds_type` varchar(32) NOT NULL,
  `key` varchar(32) COLLATE utf8_bin NOT NULL,
  `name` varchar(32) COLLATE utf8_bin NOT NULL,
  `default_value` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `value_type` varchar(50) COLLATE utf8_bin NOT NULL,
  `value_regex` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `nest_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `nest_fields` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `is_serialize` tinyint DEFAULT '0',
  `scope` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `require` tinyint(1) DEFAULT '0',
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `ref_value` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `data_source` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `data_source_type_key` (`ds_type`,`key`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据源模板类型属性定义';

CREATE TABLE IF NOT EXISTS `exchangis_user_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(200) DEFAULT '',
  `user_type` int(11) DEFAULT '0',
  `org_code` varchar(50) DEFAULT '',
  `dept_code` varchar(50) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `exchangis_proxy_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `proxy_username` varchar(64) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_proxy_relation` (`username`,`proxy_username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
