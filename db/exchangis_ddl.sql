-- exchangis_v4.exchangis_job_ds_bind definition

CREATE TABLE `exchangis_job_ds_bind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `task_index` int(11) NOT NULL,
  `source_ds_id` bigint(20) NOT NULL,
  `sink_ds_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5043 DEFAULT CHARSET=utf8 COLLATE=utf8_bin


-- exchangis_v4.exchangis_job_entity definition

CREATE TABLE `exchangis_job_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8


-- exchangis_v4.exchangis_job_param_config definition

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
  `source` varchar(255) DEFAULT NULL,
  `level` tinyint(4) DEFAULT NULL,
  `treename` varchar(32) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8

-- exchangis_v4.exchangis_project_info definition

CREATE TABLE `exchangis_project_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `create_user` varchar(64) DEFAULT NULL,
  `last_update_user` varchar(64) DEFAULT NULL,
  `project_labels` varchar(255) DEFAULT NULL,
  `domain` varchar(32) DEFAULT NULL,
  `exec_users` varchar(255) DEFAULT NULL,
  `view_users` varchar(255) DEFAULT NULL,
  `edit_users` varchar(255) DEFAULT NULL,
  `source` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1497870871035973652 DEFAULT CHARSET=utf8

-- exchangis_v4.exchangis_job_entity definition


CREATE TABLE `exchangis_job_entity` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `engine_type` varchar(45) DEFAULT '',
  `job_labels` varchar(64) DEFAULT NULL,
  `create_user` varchar(100) DEFAULT NULL,
  `job_content` text NOT NULL,
  `execute_user` varchar(100) DEFAULT '',
  `job_params` text NOT NULL,
  `project_id` bigint(13) DEFAULT NULL,
  `source` varchar(255) NOT NULL,
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

-- exchangis_v4.exchangis_launchable_task definition

CREATE TABLE `exchangis_launchable_task` (
  `id` bigint(13) NOT NULL,
  `name` varchar(100) NOT NULL,
  `job_execution_id` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `engine_type` varchar(45) DEFAULT '',
  `execute_user` varchar(50) DEFAULT '',
  `linkis_job_name` varchar(100) NOT NULL,
  `linkis_job_content` text NOT NULL,
  `linkis_params` varchar(255) DEFAULT NULL,
  `linkis_source` varchar(64) DEFAULT NULL,
  `labels` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

-- exchangis_v4.exchangis_launched_job_entity definition

CREATE TABLE `exchangis_launched_job_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
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
  `job_execution_id` varchar(255) DEFAULT NULL,
  `log_path` varchar(255) DEFAULT NULL,
  `create_user` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_execution_id_UNIQUE` (`job_execution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=708 DEFAULT CHARSET=utf8

-- exchangis_v4.exchangis_launched_task_entity definition

CREATE TABLE `exchangis_launched_task_entity` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `job_id` bigint(20) DEFAULT NULL,
  `engine_type` varchar(100) DEFAULT NULL,
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8