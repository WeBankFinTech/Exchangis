{
    "name": "ftpwriter",
    "parameter":{
        "datasource": #{datasourceId},
         "protocol": "sftp",
         "column": ${column|""},
         "host": #{host|127.0.0.1},
         "port": #{port|20},
         "username": #{username},
         "password": #{password},
         "path": #{path},
         "fileName": #{fileName},
         "writeMode": #{writeMode|truncate},
         "fieldDelimiter": #{fieldDelimiter|,},
         "compress": #{compress},
         "encoding": #{encoding|UTF-8},
         "nullFormat": "\\N",
         "transit": ${transit|true}
    }
}