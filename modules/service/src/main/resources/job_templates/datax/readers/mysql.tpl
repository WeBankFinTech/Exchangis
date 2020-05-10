{
    "name": "mysqlreader",
    "parameter": {
        "datasource":#{datasourceId},
        "username": #{username},
        "password": #{password},
        "proxyHost": #{proxyHost},
        "proxyPort": #{proxyPort|0},
        "column_i": ${column},
        "alias": #{alias},
        "table": #{table},
        "where": #{where},
        "join": #{join},
        "connection": [{
            "querySql": [
            #{querySql}
            ],
            "jdbcUrl":[
             {
              "host":#{host|host},
              "port":#{port|port},
              "database": #{database|database},
              "connParams": ${connParams|""}
             }
            ]
        }]
    }
}