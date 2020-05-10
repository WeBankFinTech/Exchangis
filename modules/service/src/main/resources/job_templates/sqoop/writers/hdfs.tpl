{
    "@name":"hdfs",
    "datasource": #{datasourceId},
    "--target-dir": {
        "@require": true,
        "value": #{path}
    },
    "--fields-terminated-by":{
        "@require": false,
        "value": #{fieldDelimiter|\u0001}
    },
    "--compress":{
        "@require": true,
        "@condition":{
            "compress": "[\\s\\S]+"
        }
    },
    "--compression-codec":{
        "@require": false,
        "@condition":{
            "compress": "[\\s\\S]+"
        },
        "@value": #{compress}
    },
    "writeMode": #{writeMode|insert},
    "path": #{path},
    "column": ${column},
    "compress": #{compress},
    "fileType": #{fileType}
}