-- exchangis_job_ds_bind definition
DROP TABLE IF EXISTS `exchangis_job_ds_bind`;
CREATE TABLE `exchangis_job_ds_bind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `task_index` int(11) NOT NULL,
  `source_ds_id` bigint(20) NOT NULL,
  `sink_ds_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59575 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- exchangis_job_entity definition
DROP TABLE IF EXISTS `exchangis_job_entity`;
CREATE TABLE `exchangis_job_entity` (
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5793 DEFAULT CHARSET=utf8;


-- exchangis_job_param_config definition
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
  `source` varchar(255) DEFAULT NULL,
  `level` tinyint(4) DEFAULT NULL,
  `treename` varchar(32) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- exchangis_project_info definition
DROP TABLE IF EXISTS `exchangis_project_info`;
-- udes_gzpc_pub_sit_01.exchangis_project_info definition
CREATE TABLE `exchangis_project_info` (
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
) ENGINE=InnoDB AUTO_INCREMENT=1497870871035974171 DEFAULT CHARSET=utf8;

-- exchangis_project_user definition
DROP TABLE IF EXISTS `exchangis_project_user`;
CREATE TABLE `exchangis_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `priv_user` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `exchangis_project_user_un` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=844 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

-- exchangis_launchable_task definition
DROP TABLE IF EXISTS `exchangis_launchable_task`;
CREATE TABLE `exchangis_launchable_task` (
  `id` bigint(13) NOT NULL,
  `name` varchar(100) NOT NULL,
  `job_execution_id` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
  `engine_type` varchar(45) DEFAULT '',
  `execute_user` varchar(50) DEFAULT '',
  `linkis_job_name` varchar(100) NOT NULL,
  `linkis_job_content` text NOT NULL,
  `linkis_params` text DEFAULT NULL,
  `linkis_source` varchar(64) DEFAULT NULL,
  `labels` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_launched_job_entity definition
DROP TABLE IF EXISTS `exchangis_launched_job_entity`;
CREATE TABLE `exchangis_launched_job_entity` (
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
  `job_execution_id` varchar(255) DEFAULT NULL,
  `log_path` varchar(255) DEFAULT NULL,
  `create_user` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_execution_id_UNIQUE` (`job_execution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8380 DEFAULT CHARSET=utf8;

-- exchangis_launched_task_entity definition
DROP TABLE IF EXISTS `exchangis_launched_task_entity`;
CREATE TABLE `exchangis_launched_task_entity` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_job_func definition
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- exchangis_job_func_params definition
DROP TABLE IF EXISTS `exchangis_job_func_params`;
CREATE TABLE IF NOT EXISTS `exchangis_job_func_params`(
    `func_id` INT(11) NOT NULL,
    `param_name` VARCHAR(100) NOT NULL,
    `order`  INT(11) DEFAULT 0,
    `name_display` VARCHAR(100),
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(`func_id`, `param_name`)
)Engine=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_engine_resources definition
DROP TABLE IF EXISTS `exchangis_engine_resources`;
CREATE TABLE `exchangis_engine_resources` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `engine_type` varchar(50) NOT NULL,
   `resource_name` varchar(100) NOT NULL,
   `resource_type` varchar(50) NOT NULL COMMENT 'resource type' DEFAULT 'file',
   `resource_path` varchar(255) NOT NULL,
   `store_uri` varchar(500) NOT NULL,
   `create_user` varchar(50) NOT NULL,
   `modify_time` datetime DEFAULT NULL,
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`),
   UNIQUE KEY `engine_res_idx` (`engine_type`,`resource_path`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_engine_settings definition
DROP TABLE IF EXISTS `exchangis_engine_settings`;
CREATE TABLE `exchangis_engine_settings` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `engine_name` varchar(50) NOT NULL,
   `engine_desc` varchar(500) NOT NULL,
   `engine_settings_value` text,
   `engine_direction` varchar(255) NOT NULL,
   `res_loader_class` varchar(255),
   `res_uploader_class` varchar(255),
   `modify_time` datetime DEFAULT NULL,
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`),
   UNIQUE KEY `engine_setting_idx` (`engine_name`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_job_transform_rule
DROP TABLE IF EXISTS `exchangis_job_transform_rule`;
CREATE TABLE `exchangis_job_transform_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_name` varchar(100) NOT NULL DEFAULT 'transform_rule',
  `rule_type` varchar(64) NOT NULL DEFAULT 'DEF',
  `rule_source` varchar(600) DEFAULT '{}',
  `data_source_type` varchar(64) NOT NULL,
  `engine_type` varchar(32),
  `direction` varchar(32) NOT NULL DEFAULT 'NONE',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_job_transform_processor
DROP TABLE IF EXISTS `exchangis_job_transform_processor`;
CREATE TABLE `exchangis_job_transform_processor` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;