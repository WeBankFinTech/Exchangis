ALTER TABLE exchangis_data_source_model_type_key DROP COLUMN ds_type_id;

ALTER TABLE exchangis_launchable_task ADD COLUMN rate_params text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launchable_task ADD COLUMN source_type varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launchable_task ADD COLUMN sink_type varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launchable_task ADD COLUMN source_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launchable_task ADD COLUMN sink_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launchable_task ADD COLUMN content text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;

ALTER TABLE exchangis_launched_task_entity ADD COLUMN source_type varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launched_task_entity ADD COLUMN sink_type varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launched_task_entity ADD COLUMN source_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launched_task_entity ADD COLUMN sink_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launched_task_entity ADD COLUMN content text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;

ALTER TABLE exchangis_launched_job_entity ADD COLUMN job_params text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;
ALTER TABLE exchangis_launched_job_entity ADD COLUMN instance varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;

ALTER TABLE exchangis_launched_task_entity ADD COLUMN `commit_version` int(13) COLLATE utf8mb4_bin DEFAULT 0;
ALTER TABLE exchangis_launched_task_entity ADD COLUMN `instance` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL;

CREATE UNIQUE INDEX IDX_model_name USING BTREE ON exchangis_data_source_model (model_name);
CREATE INDEX job_exec_id_relation USING BTREE ON exchangis_launched_task_entity (job_execution_id);
