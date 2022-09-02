-- 插入 job_param_config 记录
INSERT INTO exchangis_job_param_config (config_key,config_name,config_direction,`type`,ui_type,ui_field,ui_label,unit,required,value_type,value_range,default_value,validate_type,validate_range,validate_msg,is_hidden,is_advanced,source,`level`,treename,sort,description,status,ref_id) VALUES
('setting.speed.bytes','作业速率限制','','DATAX','INPUT','setting.speed.bytes','作业速率限制','Mb/s',1,'NUMBER','','','REGEX','^[1-9]\\d*$','作业速率限制输入错误',0,0,'',1,'',1,'',1,'')
,('setting.speed.records','作业记录数限制','','DATAX','INPUT','setting.speed.records','作业记录数限制','条/s',1,'NUMBER','','','REGEX','^[1-9]\\d*$','作业记录数限制输入错误',0,0,'',1,'',2,'',1,'')
,('setting.max.parallelism','作业最大并行度','','DATAX','INPUT','setting.max.parallelism','作业最大并行度','个',1,'NUMBER','','1','REGEX','^[1-9]\\d*$','作业最大并行度输入错误',0,0,'',1,'',3,'',1,'')
,('setting.max.memory','作业最大使用内存','','DATAX','INPUT','setting.max.memory','作业最大使用内存','Mb',1,'NUMBER','','1024','REGEX','^[1-9]\\d*$','作业最大使用内存输入错误',0,0,'',1,'',4,'',1,'')
,('setting.errorlimit.record','最多错误记录数','','DATAX','INPUT','setting.errorlimit.record','最多错误记录数','条',1,'NUMBER','','','REGEX','^[1-9]\\d*$','最多错误记录数输入错误',0,0,'',1,'',5,'',1,'')
,('setting.max.parallelism','作业最大并行数','','SQOOP','INPUT','setting.max.parallelism','作业最大并行数','个',1,'NUMBER','','1','REGEX','^[1-9]\\d*$','作业最大并行数输入错误',0,0,'',1,'',1,'',1,'')
,('setting.max.memory','作业最大内存','','SQOOP','INPUT','setting.max.memory','作业最大内存','Mb',1,'NUMBER','','1024','REGEX','^[1-9]\\d*$','作业最大内存输入错误',0,0,'',1,'',2,'',1,'')
,('where','WHERE条件','SOURCE','MYSQL','INPUT','where','WHERE条件','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,500}$','WHERE条件输入过长',0,0,'',1,'',2,'',1,'')
,('writeMode','写入方式','SQOOP-SINK','HIVE','OPTION','writeMode','写入方式(OVERWRITE只对TEXT类型表生效)','',1,'OPTION','["OVERWRITE","APPEND"]','OVERWRITE','','','写入方式输入错误',0,0,'',1,'',1,'',1,'')
,('partition','分区信息','SINK','HIVE','MAP','partition','分区信息(文本)','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,50}$','分区信息过长',0,0,'/api/rest_j/v1/dss/exchangis/main/datasources/render/partition/element/map',1,'',2,'',1,'')
,('batchSize','批量大小','DATAX-SINK','ELASTICSEARCH','INPUT','batchSize','批量大小','',0,'NUMBER','','','REGEX','^[1-9]\\d*$','批量大小输入错误',0,0,'',1,'',1,'',1,'')
,('nullCharacter','空值字符','DATAX-SINK','ELASTICSEARCH','INPUT','nullCharacter','空值字符','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,50}$','空值字符输入错误',0,0,'',1,'',2,'',1,'')
,('query','query条件','DATAX-SOURCE','MONGODB','INPUT','query','query条件','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,500}$','query条件输入过长',0,0,'',1,'',2,'',1,'')
,('writeMode','写入方式','DATAX-SINK','MONGODB','OPTION','writeMode','写入方式','',1,'OPTION','["INSERT","REPLACE"]','INSERT','','','写入方式输入错误',0,0,'',1,'',1,'',1,'')
,('batchSize','批量大小','DATAX-SINK','MONGODB','INPUT','batchSize','批量大小','',0,'NUMBER','','','REGEX','^[1-9]\\d*$','批量大小输入错误',0,0,'',1,'',2,'',1,'')
;
INSERT INTO exchangis_job_param_config (config_key,config_name,config_direction,`type`,ui_type,ui_field,ui_label,unit,required,value_type,value_range,default_value,validate_type,validate_range,validate_msg,is_hidden,is_advanced,source,`level`,treename,sort,description,status,ref_id) VALUES
('partition','分区信息','SOURCE','HIVE','MAP','partition','分区信息(文本)',NULL,0,'VARCHAR',NULL,NULL,'REGEX','^[\\s\\S]{0,50}$','分区信息过长',0,0,'/api/rest_j/v1/dss/exchangis/main/datasources/render/partition/element/map',1,NULL,1,NULL,1,'')
,('writeMode','写入方式','SQOOP-SINK','MYSQL','OPTION','writeMode','写入方式',NULL,1,'OPTION','["INSERT","UPDATE"]','INSERT',NULL,NULL,'写入方式输入错误',0,0,NULL,1,NULL,1,NULL,1,'')
;


-- 插入 job_func 记录
INSERT INTO exchangis_job_func (func_type,func_name,tab_name,name_dispaly,param_num,ref_name,description,modify_time,create_time) VALUES
('TRANSFORM','dx_substr','DATAX',NULL,2,NULL,NULL,NULL,'2020-04-21 17:35:06.000')
,('TRANSFORM','dx_pad','DATAX',NULL,3,NULL,NULL,NULL,'2020-04-21 17:35:06.000')
,('TRANSFORM','dx_replace','DATAX',NULL,3,NULL,NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','like','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','not like','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','>','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','<','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','=','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','!=','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
,('VERIFY','>=','DATAX',NULL,1,'dx_filter',NULL,NULL,'2020-04-21 17:35:06.000')
;
INSERT INTO exchangis_job_func (func_type,func_name,tab_name,name_dispaly,param_num,ref_name,description,modify_time,create_time) VALUES
('TRANSFORM','dx_precision','DATAX',NULL,1,NULL,NULL,NULL,'2020-04-21 17:35:06.000')
;


-- 插入 job_func_params 记录
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(1, 'startIndex', 'startIndex', 0) ON DUPLICATE KEY UPDATE `name_display` = 'startIndex';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(1, 'length', 'length', 1) ON DUPLICATE KEY UPDATE `name_display` = 'length';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(2, 'padType', 'padType(r or l)', 0) ON DUPLICATE KEY UPDATE `name_display` = 'padType(r or l)';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(2, 'length', 'length', 1) ON DUPLICATE KEY UPDATE `name_display` = 'length';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(2, 'padString', 'padString', 2) ON DUPLICATE KEY UPDATE `name_display` = 'padString';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(3, 'startIndex', 'startIndex', 0) ON DUPLICATE KEY UPDATE `name_display` = 'startIndex';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(3, 'length', 'length', 1) ON DUPLICATE KEY UPDATE `name_display` = 'length';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`, `order`) VALUES(3, 'replaceString', 'replaceString', 2) ON DUPLICATE KEY UPDATE `name_display` = 'replaceString';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(4, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(5, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(6, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(7, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(8, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(9, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';
INSERT INTO `exchangis_job_func_params`(`func_id`, `param_name`, `name_display`) VALUES(10, 'value', 'value') ON DUPLICATE KEY UPDATE `name_display` = 'value';


-- 插入 engine_settings 记录
INSERT INTO `exchangis_engine_settings` (id, engine_name, engine_desc, engine_settings_value, engine_direction, res_loader_class, res_uploader_class, modify_time, create_time) VALUES
(1, 'datax', 'datax sync engine', '{}', 'mysql->hive,hive->mysql,mongodb->hive,hive->mongodb,mysql->elasticsearch,mysql->elasticsearch,mongodb->elasticsearch,mongodb->mysql,mysql->mongodb', 'com.webank.wedatasphere.exchangis.engine.resource.loader.datax.DataxEngineResourceLoader', NULL, NULL, '2022-08-09 18:20:51.0'),
(2, 'sqoop', 'hadoop tool', '{}', 'mysql->hive,hive->mysql', '', NULL, NULL, '2022-08-09 18:20:51.0');