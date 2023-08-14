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