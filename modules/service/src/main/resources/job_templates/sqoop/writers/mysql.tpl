{
    "@name": "mysql",
    "datasource": #{datasourceId},
    "--connect": {
        "@require": true,
        "@value": "jdbc:mysql://{host}:{port}/{database}",
        "params":{
            "host": #{host|localhost},
            "port": #{port|3306},
            "database": #{database},
            "connParams": ${connParams|""}
        }
    },
    "--username":{
        "@require": true,
        "@value": "{username}",
        "params":{
            "username": #{username|username}
        }
    },
    "--password":{
        "@require": true,
        "@value": "{password}",
        "params":{
            "password": #{password|password}
        }
    },
    "--table":{
        "@require": true,
        "value": #{table}
    },
    "--update-key":{
        "@require": false,
        "@value": "{primaryKeys}",
        "@params":{
            "primaryKeys": ${primaryKeys|""}
        }
    },
      "--columns":{
          "@require": true,
          "@value": "{column}",
          "@params":{
              "column": ${sqlOrderColumn|""}
          }
      },
    "--update-mode":{
        "@require": false,
        "value": #{writeMode|allowinsert}
    },
    "column": ${column}
}