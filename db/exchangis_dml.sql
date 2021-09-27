-- 插入 job_param_config 记录
INSERT INTO `exchangis_job_param_config`(`config_key`, `config_name`, `config_direction`, `type`, `ui_type`, `ui_field`, `ui_label`, `unit`, `required`, `value_type`, `value_range`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`, `treename`, `sort`, `description`, `status`) VALUES ( 'exchangis.job.ds.params.mysq1l.transform_type', '传输方式', 'SOURCE', 'MYSQL', 'OPTION', 'transform', '传输方式', NULL, '1', 'ARRAY', '[\"Record\", \"二进制\"]', 'Record', '', '', b'0', b'0', 1, '', 1, '', 1);
INSERT INTO `exchangis_job_param_config`(`config_key`, `config_name`, `config_direction`, `type`, `ui_type`, `ui_field`, `ui_label`, `unit`, `required`, `value_type`, `value_range`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`, `treename`, `sort`, `description`, `status`) VALUES ( 'exchangis.job.ds.params.mysql.partitioned_by', '分区信息', 'SOURCE', 'MYSQL', 'INPUT', 'transform', '分区信息', NULL, '1', 'VARCHAR', '', '', '', '', b'0', b'0', 1, '', 2, '', 1);
INSERT INTO `exchangis_job_param_config`(`config_key`, `config_name`, `config_direction`, `type`, `ui_type`, `ui_field`, `ui_label`, `unit`, `required`, `value_type`, `value_range`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`, `treename`, `sort`, `description`, `status`) VALUES ( 'exchangis.job.ds.params.mysql.write_type', '写入方式', 'SINK', 'MYSQL', 'OPTION', 'transform', '写入方式', NULL, '1', 'ARRAY', '[\"insert\"]', '', '', '', b'0', b'0', 1, '', 1, '', 1);
INSERT INTO `exchangis_job_param_config`(`config_key`, `config_name`, `config_direction`, `type`, `ui_type`, `ui_field`, `ui_label`, `unit`, `required`, `value_type`, `value_range`, `default_value`, `validate_type`, `validate_range`, `is_hidden`, `is_advanced`, `level`, `treename`, `sort`, `description`, `status`) VALUES ( 'exchangis.job.ds.params.mysql.batch_size', '批量大小', 'SINK', 'MYSQL', 'INPUT', 'transform', '批量大小', NULL, '1', 'NUMBER', '[\"insert\"]', '1000', '', '', b'0', b'0', 1, '', 2, '', 1);

INSERT INTO `exchangis_job_param_config`(config_key, config_name, config_direction, type, ui_type, ui_field, ui_label, unit, required, value_type, value_range, default_value, validate_type, validate_range, is_hidden, is_advanced, level, treename, sort, description, status) VALUES ('exchangis.datax.setting.speed.channel', '并发数', NULL, 'DATAX', 'INPUT', 'channel', '并发数', NULL, 0, 'NUMBER', '1', '1', '', '', 0, 0, 1, '', 1, '', 1);
INSERT INTO `exchangis_job_param_config`(config_key, config_name, config_direction, type, ui_type, ui_field, ui_label, unit, required, value_type, value_range, default_value, validate_type, validate_range, is_hidden, is_advanced, level, treename, sort, description, status) VALUES ('exchangis.datax.setting.speed.byte', '传输速率', NULL, 'DATAX', 'INPUT', 'channel', '传输速率', 'MB/s', 0, 'NUMBER', '', '', '', '', 0, 0, 1, '', 2, '', 1);
INSERT INTO `exchangis_job_param_config`(config_key, config_name, config_direction, type, ui_type, ui_field, ui_label, unit, required, value_type, value_range, default_value, validate_type, validate_range, is_hidden, is_advanced, level, treename, sort, description, status) VALUES ('exchangis.datax.setting.errorlimit.record', '脏数据最大记录数', NULL, 'DATAX', 'INPUT', 'errorlimit_record', '脏数据最大记录数', NULL, 0, 'NUMBER', '', '', '', '', 0, 0, 1, '', 3, '', 1);
INSERT INTO `exchangis_job_param_config`(config_key, config_name, config_direction, type, ui_type, ui_field, ui_label, unit, required, value_type, value_range, default_value, validate_type, validate_range, is_hidden, is_advanced, level, treename, sort, description, status) VALUES ('exchangis.datax.setting.errorlimit.percentage', '脏数据占比阈值', NULL, 'DATAX', 'INPUT', 'errorlimit_percentage', '脏数据占比阈值', NULL, 0, 'DOUBLE', '', '', '', '', 0, 0, 1, '', 4, '', 1);


-- 插入 job_info 数据
INSERT INTO `exchangis_job_info`(`project_id`, `job_name`, `job_type`, `engine_type`, `job_labels`, `job_desc`, `content`, `alarm_user`, `alarm_level`, `proxy_user`, `execute_node`, `sync_type`, `job_params`, `create_time`, `create_user`, `modify_time`, `modify_user`) VALUES (1, 'test_batch_job', 'OFFLINE', 'DATAX', 'dev', 'test_batch_job_desc', '[{"subJobName":"job0001","dataSources":{"source_id":"MYSQL.1.test_db.test_table","sink_id":"MYSQL.1.mask_db.mask_table"},"params":{"sources":[{"config_key":"exchangis.job.ds.params.mysql.transform_type","config_name":"传输方式","config_value":"二进制","sort":1},{"config_key":"exchangis.job.ds.params.mysql.partitioned_by","config_name":"分区信息","config_value":"2021-07-30","sort":2}],"sinks":[{"config_key":"exchangis.job.ds.params.mysql.write_type","config_name":"写入方式","config_value":"insert","sort":1},{"config_key":"exchangis.job.ds.params.mysql.batch_size","config_name":"批量大小","config_value":1000,"sort":2}]},"transforms":{"type":"MAPPING","mapping":[{"source_field_name":"name","source_field_type":"VARCHAR","sink_field_name":"c_name","sink_field_type":"VARCHAR"},{"source_field_name":"year","source_field_type":"VARCHAR","sink_field_name":"d_year","sink_field_type":"VARCHAR"}]},"settings":[{"config_key":"exchangis.datax.setting.speed.byte","config_name":"传输速率","config_value":102400,"sort":1},{"config_key":"exchangis.datax.setting.errorlimit.record","config_name":"脏数据最大记录数","config_value":10000,"sort":2},{"config_key":"exchangis.datax.setting.errorlimit.percentage","config_name":"脏数据占比阈值","config_value":100,"sort":3}]}]', 'gujian', 1, 'hdfs', '127.0.0.1', NULL, NULL, '2021-08-01 17:00:00', 'gujian', '2021-08-01 17:00:00', 'gujian');
