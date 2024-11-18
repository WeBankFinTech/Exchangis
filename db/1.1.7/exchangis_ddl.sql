ALTER TABLE exchangis_launched_task_entity ADD INDEX `job_exec_id_relation`(`job_execution_id`);
ALTER TABLE exchangis_launched_job_entity ADD COLUMN `job_params`` text AFTER `job_execution_id``;