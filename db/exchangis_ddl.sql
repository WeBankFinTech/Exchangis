-- exchangis_v4.exchangis_job_ds_bind definition
DROP TABLE IF EXISTS `exchangis_job_ds_bind`;
CREATE TABLE `exchangis_job_ds_bind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `task_index` int(11) NOT NULL,
  `source_ds_id` bigint(20) NOT NULL,
  `sink_ds_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59575 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- exchangis_v4.exchangis_job_entity definition
DROP TABLE IF EXISTS `exchangis_job_entity`;
CREATE TABLE `exchangis_job_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
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


-- exchangis_v4.exchangis_job_param_config definition
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

-- exchangis_v4.exchangis_project_info definition
DROP TABLE IF EXISTS `exchangis_project_info`;
CREATE TABLE `exchangis_project_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime(3) DEFAULT NULL,
  `create_user` varchar(64) DEFAULT NULL,
  `last_update_user` varchar(64) DEFAULT NULL,
  `project_labels` varchar(255) DEFAULT NULL,
  `domain` varchar(32) DEFAULT NULL,
  `exec_users` varchar(255) DEFAULT NULL,
  `view_users` varchar(255) DEFAULT NULL,
  `edit_users` varchar(255) DEFAULT NULL,
  `source` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1497870871035973934 DEFAULT CHARSET=utf8;

-- exchangis_v4.exchangis_project_user definition
DROP TABLE IF EXISTS `exchangis_project_user`;
CREATE TABLE `exchangis_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `priv_user` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `priv` int(20) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

-- exchangis_v4.exchangis_launchable_task definition
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
  `linkis_params` varchar(255) DEFAULT NULL,
  `linkis_source` varchar(64) DEFAULT NULL,
  `labels` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- exchangis_v4.exchangis_launched_job_entity definition
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

-- exchangis_v4.exchangis_launched_task_entity definition
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8

DROP TABLE IF EXISTS `exchangis_job_func_params`;
CREATE TABLE IF NOT EXISTS `exchangis_job_func_params`(
    `func_id` INT(11) NOT NULL,
    `param_name` VARCHAR(100) NOT NULL,
    `order`  INT(11) DEFAULT 0,
    `name_display` VARCHAR(100),
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(`func_id`, `param_name`)
)Engine=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exchangis_engine_settings`;
CREATE TABLE IF NOT EXISTS `exchangis_engine_settings`(
    `id` bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `engine_name` VARCHAR(255) NOT NULL,
    `direct_source`  VARCHAR(255) NOT NULL,
    `direct_sink` VARCHAR(255)  NOT NULL,
    `func_id` bigint(20),
    `use_func` BOOL
)Engine=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO exchangis_job_param_config (config_key,config_name,config_direction,`type`,ui_type,ui_field,ui_label,unit,required,value_type,value_range,default_value,validate_type,validate_range,validate_msg,is_hidden,is_advanced,source,`level`,treename,sort,description,status,ref_id) VALUES
('setting.speed.bytes','作业速率限制','','DATAX','INPUT','setting.speed.bytes','作业速率限制','Mb/s',1,'NUMBER','','','REGEX','^[1-9]\\d*$','作业速率限制输入错误',0,0,'',1,'',1,'',1,null)
,('setting.speed.records','作业记录数限制','','DATAX','INPUT','setting.speed.records','作业记录数限制','条/s',1,'NUMBER','','','REGEX','^[1-9]\\d*$','作业记录数限制输入错误',0,0,'',1,'',2,'',1,null)
,('setting.max.parallelism','作业最大并行度','','DATAX','INPUT','setting.max.parallelism','作业最大并行度','个',1,'NUMBER','','1','REGEX','^[1-9]\\d*$','作业最大并行度输入错误',0,0,'',1,'',3,'',1,null)
,('setting.max.memory','作业最大使用内存','','DATAX','INPUT','setting.max.memory','作业最大使用内存','Mb',1,'NUMBER','','1024','REGEX','^[1-9]\\d*$','作业最大使用内存输入错误',0,0,'',1,'',4,'',1,null)
,('setting.errorlimit.record','最多错误记录数','','DATAX','INPUT','setting.errorlimit.record','最多错误记录数','条',1,'NUMBER','','','REGEX','^[1-9]\\d*$','最多错误记录数输入错误',0,0,'',1,'',5,'',1,null)
,('setting.max.parallelism','作业最大并行数','','SQOOP','INPUT','setting.max.parallelism','作业最大并行数','个',1,'NUMBER','','1','REGEX','^[1-9]\\d*$','作业最大并行数输入错误',0,0,'',1,'',1,'',1,null)
,('setting.max.memory','作业最大内存','','SQOOP','INPUT','setting.max.memory','作业最大内存','Mb',1,'NUMBER','','1024','REGEX','^[1-9]\\d*$','作业最大内存输入错误',0,0,'',1,'',2,'',1,null)
,('where','WHERE条件','SOURCE','MYSQL','INPUT','where','WHERE条件','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,500}$','WHERE条件输入过长',0,0,'',1,'',2,'',1,null)
,('writeMode','写入方式','SQOOP-SINK','HIVE','OPTION','writeMode','写入方式(OVERWRITE只对TEXT类型表生效)','',1,'OPTION','["OVERWRITE","APPEND"]','OVERWRITE','','','写入方式输入错误',0,0,'',1,'',1,'',1,null)
,('partition','分区信息','SINK','HIVE','MAP','partition','分区信息(文本)','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,50}$','分区信息过长',0,0,'/api/rest_j/v1/dss/exchangis/main/datasources/render/partition/element/map',1,'',2,'',1,null)
,('batchSize','批量大小','DATAX-SINK','ELASTICSEARCH','INPUT','batchSize','批量大小','',0,'NUMBER','','','REGEX','^[1-9]\\d*$','批量大小输入错误',0,0,'',1,'',1,'',1,null)
,('nullCharacter','空值字符','DATAX-SINK','ELASTICSEARCH','INPUT','nullCharacter','空值字符','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,50}$','空值字符输入错误',0,0,'',1,'',2,'',1,null)
,('query','query条件','DATAX-SOURCE','MONGODB','INPUT','query','query条件','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,500}$','query条件输入过长',0,0,'',1,'',2,'',1,null)
,('writeMode','写入方式','DATAX-SINK','MONGODB','OPTION','writeMode','写入方式','',1,'OPTION','["INSERT","REPLACE"]','INSERT','','','写入方式输入错误',0,0,'',1,'',1,'',1,null)
,('batchSize','批量大小','DATAX-SINK','MONGODB','INPUT','batchSize','批量大小','',0,'NUMBER','','','REGEX','^[1-9]\\d*$','批量大小输入错误',0,0,'',1,'',2,'',1,null)
,('transferMode','传输方式','DATAX-SOURCE','HIVE','OPTION','transferMode','传输方式','',1,'OPTION','["二进制","记录"]','记录','','','该传输方式不可用',0,0,'',1,'',1,'',1,null)
;
INSERT INTO exchangis_job_param_config (config_key,config_name,config_direction,`type`,ui_type,ui_field,ui_label,unit,required,value_type,value_range,default_value,validate_type,validate_range,validate_msg,is_hidden,is_advanced,source,`level`,treename,sort,description,status,ref_id) VALUES
('partition','分区信息','SOURCE','HIVE','MAP','partition','分区信息(文本)',NULL,0,'VARCHAR',NULL,NULL,'REGEX','^[\\s\\S]{0,50}$','分区信息过长',0,0,'/api/rest_j/v1/dss/exchangis/main/datasources/render/partition/element/map',1,NULL,1,NULL,1,null)
,('writeMode','写入方式','SQOOP-SINK','MYSQL','OPTION','writeMode','写入方式',NULL,1,'OPTION','["INSERT","UPDATE"]','INSERT',NULL,NULL,'写入方式输入错误',0,0,NULL,1,NULL,1,NULL,1,null)
;