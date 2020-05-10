{
  "job":{
    "setting":{
      "useProcessor": #{usePostProcess|false},
      "speed":{
        "channel": #{mParallel|0},
        "byte": #{speedByte|1048576},
        "record": #{speedRecord|100000}
      },
      "errorLimit":{
        "record": #{errorRecord|10}
      },
      "transport":{
        "type": #{transportType|record}
      },
      "syncMeta": #{syncMeta|false},
      "advance":{
        "advanceOption": #{advanceOption},
        "mMemory": #{mMemory|1g}
      }
    },
    "content":[
       {
           "reader": ${reader},
           "writer": ${writer},
           "transformer": ${transformer|[]}
       }
    ]
  }
}