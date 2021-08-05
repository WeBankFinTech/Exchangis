DROP TABLE IF EXISTS `exchangis_job_param_config`;
CREATE TABLE `exchangis_job_param_config`  (
    id bigint auto_increment,
    config_key varchar(64) not null unique,
    config_name varchar(64) not null,
    type varchar(32) not null,
    ui_type varchar(32),
    ui_field varchar(32),
    ui_label varchar(32),
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
    id bigint auto_increment,
    job_name varchar(64) not null unique,
    job_desc varchar(255),
    engine_type varchar(32) not null,
    alarm_user varchar(32),
    alarm_level int,
    content text,
    label_map varchar(255),
    create_time datetime,
    modify_time datetime,
    create_user varchar(64),
    modify_user varchar(64),
    to_proxy_user varchar(64),
    exec_nodes varchar(255),
    last_launch_time datetime,
    project_id int,
    parent_id bigint,
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