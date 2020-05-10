{
    "@name": "hive",
    "datasource": #{datasourceId},
    "fileType": #{fileType},
    "--hcatalog-database":{
        "@require": true,
        "@condition":{
            "fileType": "^(?!TEXT).*$"
        },
        "value": #{database}
    },
    "--hcatalog-table":{
        "@require": true,
        "@condition":{
            "fileType": "^(?!TEXT).*$"
        },
        "value": #{table}
    },
    "--hcatalog-partition-keys":{
        "@require": false,
        "@condition":{
            "fileType": "^(?!TEXT).*$"
        },
        "value": #{partitionKeys}
    },
    "--hcatalog-partition-values":{
        "@require": false,
        "@condition":{
            "fileType": "^(?!TEXT).*$"
        },
       "value": #{partitions}
    },
     "--hive-partition-key":{
         "@require": false,
         "@condition":{
             "fileType": "TEXT"
         },
         "value": #{partitionKeys}
     },
     "--hive-partition-value":{
         "@require": false,
         "@condition":{
             "fileType": "TEXT"
         },
         "value": #{partitions}
     },
    "--hive-import":{
        "@require": true,
        "@condition":{
            "fileType": "TEXT"
        }
    },
    "--hive-database":{
        "@require": true,
        "@condition":{
            "fileType": "TEXT"
        },
        "value": #{database}
    },
    "--hive-table":{
        "@require": true,
        "@condition":{
            "fileType": "TEXT"
        },
        "value": #{table}
    },
    "--fields-terminated-by":{
        "@require": false,
        "@value": "\'{fieldDelimiter}\'",
        "params":{
            "fieldDelimiter": #{fieldDelimiter|\u0001}
        }
    },
    "--null-string":{
        "@require": false,
        "@value": "\'{nullFormat}\'",
        "params":{
            "nullFormat": #{nullFormat|\\\\\\\\N}
        }
    },
    "--null-non-string":{
        "@require": false,
        "@value": "\'{nullFormat}\'",
        "params":{
            "nullFormat": #{nullFormat|\\\\\\\\N}
        }
    },
    "--delete-target-dir":{
        "@require": true,
        "@condition":{
            "fileType": "TEXT",
            "writeMode": "insert"
        }
    },
    "writeMode": #{writeMode|insert},
    "column": ${column},
    "path": #{hiveDataPath}
}