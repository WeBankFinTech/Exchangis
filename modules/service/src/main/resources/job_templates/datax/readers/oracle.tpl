{
  "name": "oraclereader",
  "parameter": {
    "datasource":#{datasourceId},
    "username": #{username},
    "password": #{password},
    "proxyHost": #{proxyHost},
    "proxyPort": #{proxyPort|0},
    "column_i": ${column},
    "table": #{table},
    "where": #{where},
    "connection": [{
    "querySql": [
       #{querySql}
    ],
    "jdbcUrl":[
    {
       "host":#{host|host},
       "port":#{port|port},
       "serviceName": #{serviceName|serviceName},
       "sid": #{sid|sid},
       "database":#{database|database}
     }
     ]
   }]
  }
}