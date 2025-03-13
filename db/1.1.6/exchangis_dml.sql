INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(30, 'tdsql', 'host', 'Host', NULL, 'LIST', '', 'MAP', '{"host":"HOST", "port":"PORT", "dcn": "DCN", "set": "SET"}', 1, 'ENV', 0, '数据服务地址', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(30, 'tdsql', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(2, 'tidb', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(27, 'elasticsearch', 'elasticUrls', 'ES连接URL', NULL, 'LIST', '', 'TEXT', '', 1, 'ENV', 0, '数据服务地址', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(18, 'oracle', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(17, 'mongo', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(22, 'sqlserver', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(32, 'starrocks', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);
INSERT INTO `exchangis_data_source_model_type_key`
(ds_type_id, ds_type, `key`, name, default_value, value_type, value_regex, nest_type, nest_fields, is_serialize, `scope`, `require`, description, ref_id, ref_value, data_source)
VALUES(23, 'db2', 'params', '连接参数', NULL, 'LIST', '', 'MAP', '', 1, 'ENV', 0, '输入JSON格式: {"param":"value"}', NULL, NULL, NULL);