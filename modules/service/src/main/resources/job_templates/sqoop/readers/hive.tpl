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
    "--export-dir":{
        "@require": false,
        "@condition":{
            "fileType": "TEXT"
        },
        "value": #{hiveDataPath}
    },
    "--input-fields-terminated-by":{
        "@require": false,
        "@value": "\'{fieldDelimiter}\'",
        "params":{
            "fieldDelimiter": #{fieldDelimiter|\u0001}
        }
    },
    "--input-null-string":{
        "@require": false,
        "@value": "\'{nullFormat}\'",
        "params":{
            "nullFormat": #{nullFormat|\\\\\\\\N}
        }
    },
    "--input-null-non-string":{
        "@require": false,
        "@value": "\'{nullFormat}\'",
        "params":{
            "nullFormat": #{nullFormat|\\\\\\\\N}
        }
    },
    "column": ${column}
}