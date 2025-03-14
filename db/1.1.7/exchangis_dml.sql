UPDATE exchangis_engine_settings SET engine_direction=
'tdsql->hive,hive->tdsql,elasticsearch->hive,hive->elasticsearch,hive->starrocks,starrocks->hive,tdsql->starrocks,starrocks->tdsql,tdsql->elasticsearch,elasticsearch->tdsql,oracle->hive,hive->oracle,mongodb->hive,hive->mongodb,oracle->elasticsearch,mongodb->elasticsearch'
WHERE engine_name='datax' LIMIT 1;
