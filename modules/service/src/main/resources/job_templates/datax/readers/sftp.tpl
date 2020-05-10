{
    "name": "ftpreader",
    "parameter":{
        "datasource": #{datasourceId},
        "protocol": "sftp",
        "host": #{host|127.0.0.1},
        "port": #{port|20},
        "username": #{username},
        "password": #{password},
        "path": #{path},
        "column": ["*"],
        "fieldDelimiter": #{fieldDelimiter|,},
        "compress": #{compress},
        "encoding": #{encoding|UTF-8},
        "nullFormat": "\\N",
        "transit": ${transit|true}
    }
}