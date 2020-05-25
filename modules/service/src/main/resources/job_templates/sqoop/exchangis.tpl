{
   "tool":[{
        "@condition":{
            "reader.@name": "mysql"
        },
        "@value": "import"
   },{
        "@condition":{
            "writer.@name": "mysql"
        },
        "@value": "export"
   }
   ],
   "reader": ${reader},
   "writer": ${writer},
   "settings":{
        "-m": {
            "@require": "false",
            "@condition":{
                "mParallel": "[^0]\\d*"
            },
            "@value": #{mParallel}
        },
        "-m2": {
            "@name": "-m",
            "@condition":{
                "mParallel": "0"
            },
            "@value": 1
        },
       "mParallel": #{mParallel},
       "mMemory": #{mMemory|128m}
   },
   "command": ""
}