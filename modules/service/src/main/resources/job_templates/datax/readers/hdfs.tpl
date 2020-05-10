{
    "name": "hdfsreader",
    "parameter":{
        "datasource": #{datasourceId},
        "authType": #{authType},
        "path": #{path},
        "defaultFS": #{defaultFS},
        "column": ["*"],
        "fileType": #{fileType},
        "fieldDelimiter": #{fieldDelimiter|,},
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