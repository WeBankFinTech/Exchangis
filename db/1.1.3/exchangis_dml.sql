INSERT INTO `exchangis_job_param_config` (config_key,config_name,config_direction,`type`,ui_type,ui_field,ui_label,unit,required,value_type,value_range,default_value,validate_type,validate_range,validate_msg,is_hidden,is_advanced,source,`level`,treename,sort,description,status,ref_id) VALUES
,('writeMode','写入方式','DATAX-SINK','STARROCKS','OPTION','writeMode','写入方式','',1,'OPTION','["insert"]','insert','','','写入方式输入错误',0,0,'',1,'',1,'',1,NULL)
,('batchSize','批量字节数大小','DATAX-SINK','STARROCKS','INPUT','maxBatchSize','批量字节数大小','',0,'NUMBER','','','REGEX','^[1-9]\\d*$','批量大小输入错误',0,0,'',1,'',2,'',1,NULL);

UPDATE exchangis_engine_settings
SET engine_direction='mysql->hive,hive->mysql,mysql->oracle,oracle->mysql,oracle->hive,hive->oracle,mongodb->hive,hive->mongodb,mysql->elasticsearch,oracle->elasticsearch,mongodb->elasticsearch,mysql->mongodb,mongodb->mysql,oracle->mongodb,mongodb->oracle,hive->starrocks'
WHERE engine_name='datax';