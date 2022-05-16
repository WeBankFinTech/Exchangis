-- 插入 job_param_config 记录
INSERT INTO udes_gzpc_pub_sit_01.exchangis_job_param_config (config_key,config_name,config_direction,`type`,ui_type,ui_field,ui_label,unit,required,value_type,value_range,default_value,validate_type,validate_range,validate_msg,is_hidden,is_advanced,source,`level`,treename,sort,description,status) VALUES
('setting.speed.bytes','作业速率限制','','DATAX','INPUT','setting.speed.bytes','作业速率限制','Mb/s',1,'NUMBER','','','REGEX','^[1-9]\\d*$','作业速率限制输入错误',0,0,'',1,'',1,'',1)
,('setting.speed.records','作业记录数限制','','DATAX','INPUT','setting.speed.records','作业记录数限制','条/s',1,'NUMBER','','','REGEX','^[1-9]\\d*$','作业记录数限制输入错误',0,0,'',1,'',2,'',1)
,('setting.max.parallelism','作业最大并行度','','DATAX','INPUT','setting.max.parallelism','作业最大并行度','个',1,'NUMBER','','1','REGEX','^[1-9]\\d*$','作业最大并行度输入错误',0,0,'',1,'',3,'',1)
,('setting.max.memory','作业最大使用内存','','DATAX','INPUT','setting.max.memory','作业最大使用内存','Mb',1,'NUMBER','','1024','REGEX','^[1-9]\\d*$','作业最大使用内存输入错误',0,0,'',1,'',4,'',1)
,('setting.errorlimit.record','最多错误记录数','','DATAX','INPUT','setting.errorlimit.record','最多错误记录数','条',1,'NUMBER','','','REGEX','^[1-9]\\d*$','最多错误记录数输入错误',0,0,'',1,'',5,'',1)
,('setting.max.parallelism','作业最大并行数','','SQOOP','INPUT','setting.max.parallelism','作业最大并行数','个',1,'NUMBER','','1','REGEX','^[1-9]\\d*$','作业最大并行数输入错误',0,0,'',1,'',1,'',1)
,('setting.max.memory','作业最大内存','','SQOOP','INPUT','setting.max.memory','作业最大内存','Mb',1,'NUMBER','','1024','REGEX','^[1-9]\\d*$','作业最大内存输入错误',0,0,'',1,'',2,'',1)
,('where','WHERE条件','SOURCE','MYSQL','INPUT','where','WHERE条件','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,500}$','WHERE条件输入过长',0,0,'',1,'',2,'',1)
,('writeMode','写入方式','SQOOP-SINK','HIVE','OPTION','writeMode','写入方式(OVERWRITE只对TEXT类型表生效)','',1,'OPTION','["OVERWRITE","APPEND"]','OVERWRITE','','','写入方式输入错误',0,0,'',1,'',1,'',1)
,('partition','分区信息','SINK','HIVE','MAP','partition','分区信息(文本)','',0,'VARCHAR','','','REGEX','^[\\s\\S]{0,50}$','分区信息过长',0,0,'/api/rest_j/v1/exchangis/datasources/render/partition/element/map',1,'',2,'',1)
,('partition','分区信息','SOURCE','HIVE','MAP','partition','分区信息(文本)',NULL,0,'VARCHAR',NULL,NULL,'REGEX','^[\\s\\S]{0,50}$','分区信息过长',0,0,'/api/rest_j/v1/exchangis/datasources/render/partition/element/map',1,NULL,1,NULL,1)
,('writeMode','写入方式','SQOOP-SINK','MYSQL','OPTION','writeMode','写入方式',NULL,1,'OPTION','["INSERT","UPDATE"]','INSERT',NULL,NULL,'写入方式输入错误',0,0,NULL,1,NULL,1,NULL,1)
;
