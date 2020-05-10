{
    "name": "hdfswriter",
    "parameter":{
        "datasource": #{datasourceId},
        "authType": #{authType},
        "path": #{hiveDataPath},
        "column": ${column|[]},
        "fileType": #{fileType|TEXT},
        "encoding": #{encoding|UTF-8},
        "fileName": #{fileName|exchangis_hive_w},
        "compress": #{compress},
        "hivePrincipal": "hadoop/_HOST@EXAMPLE.COM",
        "writeMode": #{writeMode|truncate},
        "fieldDelimiter": #{fieldDelimiter|\u0001},
        "partitionValues": #{partitions},
        "hiveTable": #{table},
        "hiveDatabase": #{database},
        "defaultFS": #{defaultFS|<defaultFS>},
        "hadoopConfig": ${hadoopConfig|"<hadoopConfig>"},
        "haveKerberos":#{haveKerberos|false},
        "kerberosPrincipal": #{kerberosPrincipal},
        "kerberosKeytabFilePath": #{kerberosKeytabFilePath},
        "ldapUserName": #{ldapUserName},
        "ldapUserPassword": #{ldapUserPassword},
        "hiveMetastoreUris": #{hiveMetastoreUris},
        "nullFormat": #{nullFormat|\\N}
    }
}