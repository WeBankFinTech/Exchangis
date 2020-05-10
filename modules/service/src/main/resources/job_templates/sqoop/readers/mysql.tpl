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
    "--username": {
        "@require": true,
        "@value": "{username}",
        "params": {
            "username": #{username|username}
        }
    },
    "--password": {
        "@require": true,
        "@value": "{password}",
        "params":{
            "password": #{password|password}
        }
    },
    "--query": {
        "@require": true,
        "@condition":{
            "where": "[\\s\\S]+"
        },
        "@value": "\'{querySql} AND $CONDITIONS\'",
        "params":{
            "querySql": #{querySql}
        }
    },
    "--query2": {
        "@name": "--query",
        "@require": true,
        "@condition":{
            "where": ""
        },
        "@value": "\'{querySql} WHERE $CONDITIONS\'",
        "params":{
            "querySql": #{querySql}
        }
    },
    "--verbose": {
        "@require": true
    },
    "--split-by":{
        "@require": false,
        "@value": "{primaryKeys}",
        "@params":{
            "primaryKeys": ${primaryKeys|""}
        }
    },
    "column": ${column},
    "alias": #{alias},
    "where": #{where},
    "table": #{table},
    "join": #{join}
}