{
  "name": "oraclewriter",
  "parameter": {
    "datasource": #{datasourceId},
     "batchSize": #{batchSize|1000},
     "username": #{username},
     "password": #{password},
     "proxyHost": #{proxyHost},
     "proxyPort": #{proxyPort|0},
     "column_i": ${column},
     "column": ${sqlColumn|["*"]},
     "primaryKeys": ${primaryKeys},
     "connection": [{
       "table": [#{table}],
       "jdbcUrl":{
        "host":#{host|host},
        "port":#{port|port},
        "serviceName": #{serviceName|serviceName},
        "sid": #{sid|sid},
        "database":#{database|database}
        }
     }]
   }
}