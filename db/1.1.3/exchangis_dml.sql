UPDATE exchangis_engine_settings SET engine_direction=
'mysql->hive,hive->mysql,mysql->oracle,oracle->mysql,oracle->hive,hive->oracle,mongodb->hive,hive->mongodb,mysql->elasticsearch,oracle->elasticsearch,mongodb->elasticsearch,mysql->mongodb,mongodb->mysql,oracle->mongodb,mongodb->oracle,mysql->starrocks'
WHERE engine_name='datax';