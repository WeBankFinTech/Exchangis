{
    "name":"txtfilewriter",
    "parameter":{
        "path": #{path},
        "column": ${column|""},
        "fileName": #{fileName},
        "writeMode": #{writeMode|truncate},
        "fieldDelimiter": #{fieldDelimiter},
        "compress": #{compress},
        "encoding": #{encoding|UTF-8},
        "nullFormat": #{nullFormat|\\N},
        "fileFormat": #{fileFormat|text}
    }
}