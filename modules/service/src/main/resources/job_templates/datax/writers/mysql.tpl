{
 "name": "mysqlwriter",
 "parameter": {
    "datasource": #{datasourceId},
    "writeMode": #{writeMode|insert},
    "batchSize": #{batchSize|1000},
    "username": #{username},
    "password": #{password},
    "proxyHost": #{proxyHost},
    "proxyPort": #{proxyPort|0},
    "column_i": ${column},
    "column": ${sqlColumn|["*"]},
    "connection": [{
       "table": [#{table}],
       "jdbcUrl":{
             "host":#{host|host},
             "port":#{port|port},
             "database": #{database},
             "connParams": ${connParams|""}
       }
    }]
 }
}