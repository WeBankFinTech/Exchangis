# Exchangis作业执行模块接口文档

### 1、提交配置好的job进行执行 

接口描述：提交执行ExchangisJob，后台返回jobExecutionId

请求URL：/api/rest_j/v1/exchangis/job/{id}/execute 

请求方式：POST 

请求参数： 

| 名称                  | 类型    | 备注                                                         | 是否必须 | 默认值 |
| --------------------- | ------- | ------------------------------------------------------------ | -------- | ------ |
| id                    | Long    | Exchangis的ID                                                | 是       | 无     |
| permitPartialFailures | boolean | 是否允许部分失败。如果为true，就算 部分子任务失败，整个Job还是会继续 执行，执行完成后，Job状态为 Partial_Success。该参数为 requestBody参数。 | 否       | false  |

返回参数： 

| 名称           | 类型   | 是否必须 | 默认值 | 备注                 |
| -------------- | ------ | -------- | ------ | -------------------- |
| method         | String | 是       |        | 调用的方法(请求路径) |
| status         | int    | 是       |        | 响应状态码           |
| message        | String | 否       |        | 响应的信息           |
| data           | Map    | 是       |        | 返回的数据           |
| jobExecutionId | String | 是       |        | 执行job的执行id      |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/{id}/execute",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
    "jobExecutionId": "555node1node2node3execId1"
}

```

### 2、获取Job的执行状态 

接口描述：根据jobExecutionId获取Job的状态

请求URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/status 

请求方式：GET 

请求参数： 

| 名称           | 类型   | 备注                 | 是否必须 | 默认值 |
| -------------- | ------ | -------------------- | -------- | ------ |
| jobExecutionId | String | ExchangisJob的执行ID | 是       | 无     |

返回参数： 

| 名称           | 类型   | 是否必须 | 默认值 | 备注                                                         |
| -------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| method         | String | 是       |        | 调用的方法(请求路径)                                         |
| status         | int    | 是       |        | 响应状态码                                                   |
| message        | String | 否       |        | 响应的信息                                                   |
| data           | Map    | 是       |        | 返回的数据                                                   |
| jobExecutionId | String | 是       |        | 执行job的状态，包含：Inited，Scheduled， Running，WaitForRetry，Cancelled，Failed， Partial_Success，Success，Undefined，Timeout。其 中，Running状态表示正在运行，从Cancelled开始，都 是已完成状态。 |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{id}/status",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
    "status": "Running",
    "progress": 0.1
}
```

### 3、获取本次Job执行的task列表  

接口描述：通过jobExecutionId，获取任务列表

前提条件：Job的执行状态必须为Running后，才可以拿到task列表，否则返回的task列表为空

请求URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/tasklist 

请求方式：GET 

请求参数： 

| 名称           | 类型   | 备注   | 是否必须 | 默认值 |
| -------------- | ------ | ------ | -------- | ------ |
| jobExecutionId | String | string | 是       | 无     |

返回参数： 

| 名称           | 类型   | 是否必须 | 默认值 | 备注                                                         |
| -------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| method         | String | 是       |        | 调用的方法(请求路径)                                         |
| status         | int    | 是       |        | 响应状态码                                                   |
| message        | String | 否       |        | 响应的信息                                                   |
| data           | Map    | 是       |        | 返回的数据                                                   |
| jobExecutionId | String | 是       |        | 任务列表。Job的执行状态必须为Running后，才可以 拿到task列表，否则返回的task列表为空。 请注意：task没有Partial_Success状态。 |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/tasklist",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
        "tasks": [
            {
                "taskId": 5,
                "name": "test-1",
                "status": "Inited", // task没有Partial_Success状态
                "createTime": "2022-01-03 09:00:00",
                "launchTime": null,
                "lastUpdateTime": "2022-01-03 09:00:00",
                "engineType": "sqoop",
                "linkisJobId": null,
                "linkisJobInfo": null,
                "executeUser": "enjoyyin"
            }
        ]
    }
}

```

###  4、获取Job & task的执行进度 

接口描述：通过jobExecutionId，获取执行进度

前提条件：Job的执行状态必须为Running后，才可以拿到task列表的进度

请求URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/progress  

请求方式：GET 

请求参数： 

| 名称           | 类型   | 备注                 | 是否必须 | 默认值 |
| -------------- | ------ | -------------------- | -------- | ------ |
| jobExecutionId | String | ExchangisJob的执行ID | 是       | 无     |

返回参数： 

| 名称           | 类型   | 是否必须 | 默认值 | 备注                                                         |
| -------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| method         | String | 是       |        | 调用的方法(请求路径)                                         |
| status         | int    | 是       |        | 响应状态码                                                   |
| message        | String | 否       |        | 响应的信息                                                   |
| data           | Map    | 是       |        | 返回的数据                                                   |
| jobExecutionId | String | 是       |        | 任务列表。Job的执行状态必须为Running后，才可以 拿到task列表，否则返回的task列表为空。 |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/progress",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
        "job": {
            "status": "Running",
            "progress": 0.1,
            "tasks": {
                "running": [
                    {
                        "taskId": 5,
                        "name": "test-1",
                        "status": "Running",
                        "progress": 0.1
                    }
                ],
                "Inited": [
                    {
                        "taskId": 5,
                        "name": "test-1",
                        "status": "Inited",
                        "progress": 0.1
                    }
                ],
                "Scheduled": [],
                "Success": [],
                "Failed": [], // 如果存在Failed的task，则Job会直接失败
                "WaitForRetry": [],
                "Cancelled": [], // 如果存在Cancelled的task，则Job会直接失败
                "Undefined": [], // 如果存在Undefined的task，则Job会直接失败
                "Timeout": []
            }
        }
    }
}

```

### 5、获取task运行时的各项指标信息 

接口描述：通过jobExecutionId和taskId，获取task运行时的各项指标信息

前提条件：task的执行状态必须为Running后，才可以拿到task的各项指标信息，否则返回的 为空

请求URL：/api/rest_j/v1/exchangis/task/execution/{taskId}/metrics 

请求方式：POST 

请求参数： 

| 名称           | 类型   | 备注                                     | 是否必须 | 默认值 |
| -------------- | ------ | ---------------------------------------- | -------- | ------ |
| jobExecutionId | String | ExchangisJob的执行ID，放入 requestBody中 | 是       | 无     |
| taskId         | String | task的执行ID，放入URI中                  | 是       | 无     |

返回参数： 

| 名称           | 类型   | 是否必须 | 默认值 | 备注                                                         |
| -------------- | ------ | -------- | ------ | ------------------------------------------------------------ |
| method         | String | 是       |        | 调用的方法(请求路径)                                         |
| status         | int    | 是       |        | 响应状态码                                                   |
| message        | String | 否       |        | 响应的信息                                                   |
| data           | Map    | 是       |        | 返回的数据                                                   |
| jobExecutionId | String | 是       |        | 任务各项指标信息。task的执行状态必须为 Running后，才可以拿到task的各项指标信息。 |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/task/execution/{taskId}/metrics",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
        "task": {
            "taskId": 5,
            "name": "test-1",
            "status": "running",
            "metrics": {
                "resourceUsed": {
                    "cpu": 10, // 单位：vcores
                    "memory": 20 // 单位：GB
                },
                "traffic": {
                    "source": "mysql",
                    "sink": "hive",
                    "flow": 100 // 单位：Records/S
                },
                "indicator": {
                    "exchangedRecords": 109345, // 单位：Records
                    "errorRecords": 5,
                    "ignoredRecords": 5
                }
            }
        }
    }
}
```

### 6、获取Job的实时日志 

接口描述：通过jobExecutionId，获取Job的实时日志

请求URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/log? fromLine=&pageSize=&ignoreKeywords=&onlyKeywords=&lastRows= 

请求方式：GET 

请求参数： 

| 名称           | 类型   | 备注                                                         | 是否必须 | 默认值 |
| -------------- | ------ | ------------------------------------------------------------ | -------- | ------ |
| jobExecutionId | String | ExchangisJob的执行ID                                         | 是       | 无     |
| fromLine       | int    | 读取的起始行                                                 | 否       | 0      |
| pageSize       | int    | 本次读取日志行数                                             | 否       | 100    |
| ignoreKeywords | String | 忽略哪些关键字所在的行，多个关键字以英 文,分隔               | 否       | 无     |
| onlyKeywords   | String | 只取哪些关键字所在的行，多个关键字以英 文,分隔               | 否       | 无     |
| lastRows       | int    | 只读取最后多少行的日志，相当于tail -f log。该参数大于0时，上面所有的参数都会 失效。 | 否       | 无     |

返回参数： 

| 名称    | 类型    | 是否必须 | 默认值 | 备注                                                   |
| ------- | ------- | -------- | ------ | ------------------------------------------------------ |
| method  | String  | 是       |        | 调用的方法(请求路径)                                   |
| status  | int     | 是       |        | 响应状态码                                             |
| message | String  | 否       |        | 响应的信息                                             |
| data    | Map     | 是       |        | 返回的数据                                             |
| endLine | int     | 是       |        | 本次读取的结束行号,下次可以从 endLine + 1 继续读取日志 |
| isEnd   | boolean |          |        | 日志是否已经全部读完                                   |
| logs    | List    | 是       |        | 返回Job的执行日志                                      |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/log",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
        "endLine": 99, // 本次读取的结束行号,下次可以从 endLine + 1 继续读取日志
        "isEnd": false, // 日志是否已经全部读完
        "logs": [
            "all": "",
            "error": "",
            "warn": "",
            "info": ""
        ]
    }
}
```

### 7、获取task的实时日志 

接口描述：通过jobExecutionId和taskId，获取task的实时日志

请求URL：/api/rest_j/v1/exchangis/task/execution/{taskId}/log? jobExecutionId=&fromLine=&pageSize=&ignoreKeywords=&onlyKeywords=&lastRows= 

请求方式：GET 

请求参数： 

| 名称           | 类型   | 备注                                                         | 是否必须 | 默认值 |
| -------------- | ------ | ------------------------------------------------------------ | -------- | ------ |
| taskId         | String | task的执行Id                                                 | 是       | 无     |
| jobExecutionId | String | ExchangisJob的执行ID                                         | 是       | 无     |
| fromLine       | int    | 读取的起始行                                                 | 否       | 0      |
| pageSize       | int    | 本次读取日志行数                                             | 否       | 100    |
| ignoreKeywords | String | 忽略哪些关键字所在的行，多个关键字以英 文,分隔               | 否       | 无     |
| onlyKeywords   | String | 只取哪些关键字所在的行，多个关键字以英 文,分隔               | 否       | 无     |
| lastRows       | int    | 只读取最后多少行的日志，相当于tail -f log。该参数大于0时，上面所有的参数都会 失效。 | 否       | 无     |

返回参数： 

| 名称    | 类型    | 是否必须 | 默认值 | 备注                                                   |
| ------- | ------- | -------- | ------ | ------------------------------------------------------ |
| method  | String  | 是       |        | 调用的方法(请求路径)                                   |
| status  | int     | 是       |        | 响应状态码                                             |
| message | String  | 否       |        | 响应的信息                                             |
| data    | Map     | 是       |        | 返回的数据                                             |
| endLine | int     | 是       |        | 本次读取的结束行号,下次可以从 endLine + 1 继续读取日志 |
| isEnd   | boolean |          |        | 日志是否已经全部读完                                   |
| logs    | List    | 是       |        | 返回Job的执行日志                                      |

返回示例：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{taskId}/log",
    "status": 0,
    "message": "Submitted succeed(提交成功)！",
    "data": {
        "endLine": 99, // 本次读取的结束行号,下次可以从 endLine + 1 继续读取日志
        "isEnd": false, // 日志是否已经全部读完
        "logs": [
            "all": "",
            "error": "",
            "warn": "",
            "info": ""
        ]
    }
}
```

### 