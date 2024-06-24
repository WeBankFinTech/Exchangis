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

ALTER TABLE exchangis_launchable_task ADD COLUMN delay_time datetime;
ALTER TABLE exchangis_launchable_task ADD COLUMN delay_count int(3) DEFAULT 0;
ALTER TABLE exchangis_launchable_task ADD COLUMN instance varchar(100);

ALTER TABLE exchangis_launched_task_entity ADD COLUMN instance varchar(100);
ALTER TABLE exchangis_launched_task_entity ADD COLUMN commit_version int(13) DEFAULT 0;

ALTER TABLE exchangis_launched_job_entity ADD COLUMN instance varchar(100);
