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
  `ds_type_id` int(11) NOT NULL,
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

