DROP TABLE IF EXISTS `exchangis_job_param_config`;
CREATE TABLE `exchangis_job_param_config`  (
    id bigint auto_increment,
    config_key varchar(64) not null unique,
    config_name varchar(64) not null,
    config_direction varchar(16),
    type varchar(32) not null,
    ui_type varchar(32),
    ui_field varchar(32),
    ui_label varchar(32),
    unit varchar(32),
    required bit default 0,
    value_type varchar(32),
    value_range varchar(255),
    default_value varchar(255),
    validate_type varchar(64),
    validate_range varchar(64),
    is_hidden bit,
    is_advanced bit,
    level tinyint,
    treename varchar(32),
    sort int,
    description varchar(255),
    status tinyint,
    primary key (id)
) ENGINE = InnoDB CHARACTER SET = utf8;

DROP TABLE IF EXISTS `exchangis_job_info`;
CREATE TABLE `exchangis_job_info`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `project_id` bigint(20),
    `job_name` varchar(100),
    `job_type` varchar(50),
    `engine_type` varchar(50),
    `job_labels` varchar(255),
    `job_desc` varchar(255),
    `content` text,
    `alarm_user` varchar(50),
    `alarm_level` int(255),
    `proxy_user` varchar(50),
    `execute_node` varchar(255),
    `sync_type` varchar(50),
    `job_params` text,
    `create_time` datetime(0) NULL DEFAULT NULL,
    `create_user` varchar(50),
    `modify_time` datetime(0) NULL DEFAULT NULL,
    `modify_user` varchar(50),
    primary key (id)
) ENGINE = InnoDB CHARACTER SET = utf8;

DROP TABLE IF EXISTS `exchangis_project`;
CREATE TABLE `exchangis_project`  (
    id bigint auto_increment,
    dss_project_id bigint,
    workspace_name varchar(64),
    name varchar(64) not null,
    description varchar(255),
    create_time datetime,
    last_update_time datetime,
    create_by varchar(64),
    last_update_by varchar(64),
    tags varchar(255),
    exec_users varchar(255),
    view_users varchar(255),
    edit_users varchar(255),
    primary key (id)
) ENGINE = InnoDB CHARACTER SET = utf8;

DROP TABLE IF EXISTS `exchangis_launch_task`;
CREATE TABLE `exchangis_launch_task`  (
    id bigint auto_increment,
    job_id bigint,
    engine_type varchar(64),
    run_type varchar(64),
    code text,
    creator varchar(64),
    execute_user varchar(64),
    primary key (id)
) ENGINE = InnoDB CHARACTER SET = utf8;