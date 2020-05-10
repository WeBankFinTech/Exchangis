{
    "name": "hdfswriter",
    "parameter":{
        "datasource": #{datasourceId},
        "authType": #{authType},
        "path": #{path},
        "column": ${column|""},
        "defaultFS": #{defaultFS},
        "fileName": #{fileName},
        "fileType": #{fileType},
        "writeMode": #{writeMode|truncate},
        "fieldDelimiter": #{fieldDelimiter},
        "compress": #{compress},
        "encoding": #{encoding|UTF-8},
        "nullFormat": #{nullFormat|\\N},
        "hadoopConfig": ${hadoopConfig|"<hadoopConfig>"},
        "haveKerberos": #{haveKerberos|false},
        "kerberosKeytabFilePath": #{kerberosKeytabFilePath},
        "kerberosPrincipal": #{kerberosPrincipal},
        "ldapUserName": #{ldapUserName},
        "ldapUserPassword": #{ldapUserPassword}
    }
}