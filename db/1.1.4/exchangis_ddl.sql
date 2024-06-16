DROP TABLE IF EXISTS `exchangis_project_ds`;
CREATE TABLE `exchangis_project_ds` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `project_id` bigint(20) NOT NULL,
    `data_source_name` varchar(255) NOT NULL,
    `data_source_id` bigint(20),
    `data_source_type` varchar(50) NOT NULL,
    `data_source_creator` varchar(100),
    `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `exchangis_project_ds_un`(`project_id`, `data_source_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE  exchangis_engine_settings CHANGE engine_direction engine_direction text NOT NULL;