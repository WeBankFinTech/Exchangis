# Exchangis interface document 

## Exchangis datasource module

### 1、Get datasource type

Interface description：Get the datasource type according to the information of request

Request URL：/dss/exchangis/main/datasources/type

Request mode：GET

Request parameters： 

| Name    | Type               | If required | Default value | Remark  |
| ------- | ------------------ | ----------- | ------------- | ------- |
| request | HttpServletRequest | yes         | /             | request |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         |               | Called method (request path) |
| status  | int    | yes         |               | Response status code         |
| message | String | no          |               | Information of the response  |
| data    | List   | yes         |               | The returned data            |

### 2、Query datasource

Interface description：Query the required datasource according to vo 

Request URL：/dss/exchangis/main/datasources/query

Request mode：GET、POST

Request parameters： 

| Name    | Type               | If required | Default value | Remark  |
| ------- | ------------------ | ----------- | ------------- | ------- |
| request | HttpServletRequest | yes         | /             | request |
| vo      | DataSourceQueryVO  | yes         | /             |         |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | List   | yes         | /             | The returned data            |

### 3、Query datasource

Interface description：Query datasource according to request information 

Request URL：/dss/exchangis/main/datasources

Request mode：GET

Request parameters： 

| Name     | Type               | If required | Default value | Remark              |
| -------- | ------------------ | ----------- | ------------- | ------------------- |
| request  | HttpServletRequest | yes         | /             | request             |
| typeId   | Long               | yes         | /             | datasource typeId   |
| typeName | String             | yes         | /             | datasource Typename |
| page     | Integer            | yes         | /             | page num            |
| size     | Integer            | yes         | /             | size per page       |

Return parameter： 

| Name    | Type                  | If required | Default value | Remark                       |
| ------- | --------------------- | ----------- | ------------- | ---------------------------- |
| method  | String                | yes         | /             | Called method (request path) |
| status  | int                   | yes         | /             | Response status code         |
| message | String                | no          | /             | Information of the response  |
| data    | List\<DataSourceDTO\> | yes         | /             | The returned data            |

### 4、Query datasource keydefines

Interface description：Query key definitions of datasource according to datasource type ID 

Request URL：/dss/exchangis/main/datasources/types/{dataSourceTypeId}/keydefines

Request mode：GET

Request parameters： 

| Name             | Type               | If required | Default value | Remark            |
| ---------------- | ------------------ | ----------- | ------------- | ----------------- |
| request          | HttpServletRequest | yes         | /             |                   |
| dataSourceTypeId | Long               | yes         | /             | dataSource typeId |

Return parameter： 

| Name    | Type                   | If required | Default value | Remark                       |
| ------- | ---------------------- | ----------- | ------------- | ---------------------------- |
| method  | String                 | yes         | /             | Called method (request path) |
| status  | int                    | yes         | /             | Response status code         |
| message | String                 | no          | /             | Information of the response  |
| data    | List[Map[String, Any]] | yes         | /             | The returned data            |

### 5、Get datasource version

Interface description：Get the datasource version according to the datasource ID 

Request URL：/dss/exchangis/main/datasources/{id}/versions

Request mode：GET

Request parameters： 

| Name    | Type               | If required | Default value | Remark        |
| ------- | ------------------ | ----------- | ------------- | ------------- |
| request | HttpServletRequest | yes         | /             | request       |
| id      | Long               | yes         | /             | datasource id |

Return parameter： 

| Name    | Type                      | If required | Default value | Remark                       |
| ------- | ------------------------- | ----------- | ------------- | ---------------------------- |
| method  | String                    | yes         | /             | Called method (request path) |
| status  | int                       | yes         | /             | Response status code         |
| message | String                    | no          | /             | Information of the response  |
| data    | List<Map<String, Object>> | yes         | /             | The returned data            |

### 6、Create datasource

Interface description：Create a datasource according to the DataSourceCreateVO

Request URL：/dss/exchangis/main/datasources

Request mode：POST

Request parameters： 

| Name               | Type               | If required | Default value | Remark             |
| ------------------ | ------------------ | ----------- | ------------- | ------------------ |
| request            | HttpServletRequest | yes         | /             | request            |
| dataSourceCreateVO | DataSourceCreateVO | yes         | /             | dataSourceCreateVO |
| bindingResult      | BindingResult      | yes         | /             | bindingResult      |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Long   | yes         | /             | The returned data            |

### 7、Get datasource information

Interface description：Get datasource information according to request information, datasource ID and version ID

Request URL：/dss/exchangis/main/datasources/{id}

Request mode：GET

Request parameters： 

| Name      | Type               | If required | Default value | Remark        |
| --------- | ------------------ | ----------- | ------------- | ------------- |
| request   | HttpServletRequest | yes         | /             | request       |
| id        | Long               | yes         | /             | datasource id |
| versionId | String             | yes         | /             | version id    |

Return parameter： 

| Name    | Type              | If required | Default value | Remark                       |
| ------- | ----------------- | ----------- | ------------- | ---------------------------- |
| method  | String            | yes         | /             | Called method (request path) |
| status  | int               | yes         | /             | Response status code         |
| message | String            | no          | /             | Information of the response  |
| data    | DataSourceItemDTO | yes         | /             | The returned data            |

### 8、Get datasource connect params

Interface description：Acquiring datasource connection parameters according to the request information and the datasource ID 

Request URL：/dss/exchangis/main/datasources/{id}/connect_params

Request mode：GET

Request parameters： 

| Name    | Type               | If required | Default value | Remark        |
| ------- | ------------------ | ----------- | ------------- | ------------- |
| request | HttpServletRequest | yes         | /             | request       |
| Long    | id                 | yes         | /             | datasource id |

Return parameter： 

| Name    | Type                       | If required | Default value | Remark                       |
| ------- | -------------------------- | ----------- | ------------- | ---------------------------- |
| method  | String                     | yes         | /             | Called method (request path) |
| status  | int                        | yes         | /             | Response status code         |
| message | String                     | no          | /             | Information of the response  |
| data    | java.util.Map[String, Any] | yes         | /             | The returned data            |

### 9、Update datasource

Interface description：Update the datasource according to the request information, datasource type ID and DataSourceCreateVO

Request URL：/dss/exchangis/main/datasources/{id}

Request mode：PUT

Request parameters： 

| Name               | Type               | If required | Default value | Remark             |
| ------------------ | ------------------ | ----------- | ------------- | ------------------ |
| request            | HttpServletRequest | yes         | /             | request            |
| id                 | Long               | yes         | /             | datasource id      |
| dataSourceCreateVO | DataSourceCreateVO | yes         | /             | dataSourceCreateVO |
| bindingResult      | BindingResult      | yes         | /             | bindingResult      |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Map    | yes         | /             | The returned data            |

### 10、Publish datasource

Interface description：Publish the datasource according to the request request, datasource ID and version ID 

Request URL：/dss/exchangis/main/datasources/{id}/{version}/publish

Request mode：PUT

Request parameters： 

| Name    | Type               | If required | Default value | Remark                |
| ------- | ------------------ | ----------- | ------------- | --------------------- |
| request | HttpServletRequest | yes         | /             | request               |
| id      | Long               | yes         | /             | datasource id         |
| version | Long               | yes         | /             | datasource version id |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Map    | yes         | /             | The returned data            |

### 11、Expire datasource

Interface description：Expire datasource according to request information and datasource ID 

Request URL：/dss/exchangis/main/datasources/{id}/expire

Request mode：PUT

Request parameters： 

| Name    | Type               | If required | Default value | Remark        |
| ------- | ------------------ | ----------- | ------------- | ------------- |
| request | HttpServletRequest | yes         | /             | request       |
| id      | Long               | yes         | /             | datasource id |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Map    | yes         | /             | The returned data            |

### 12、Connect datasource

Interface description：Connect the datasource according to the request information, datasource ID and version ID 

Request URL：/dss/exchangis/main/datasources/{id}/{version}/connect

Request mode：PUT

Request parameters： 

| Name    | Type               | If required | Default value | Remark                |
| ------- | ------------------ | ----------- | ------------- | --------------------- |
| request | HttpServletRequest | yes         | /             | request               |
| id      | Long               | yes         | /             | datasource id         |
| version | Long               | yes         | /             | datasource version id |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Map    | yes         | /             | The returned data            |

### 13、Connect datasource

Interface description：Connect the datasource according to the request information and the datasource ID 

Request URL：/dss/exchangis/main/datasources/op/connect

Request mode：POST

Request parameters： 

| Name    | Type               | If required | Default value | Remark        |
| ------- | ------------------ | ----------- | ------------- | ------------- |
| request | HttpServletRequest | yes         | /             | request       |
| id      | Long               | yes         | /             | datasource id |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Map    | yes         | /             | The returned data            |

### 14、Delete datasource

Interface description：Connect the datasource according to the request information and the datasource type ID 

Request URL：/dss/exchangis/main/datasources/{id}

Request mode：DELETE

Request parameters： 

| Name             | Type               | If required | Default value | Remark            |
| ---------------- | ------------------ | ----------- | ------------- | ----------------- |
| request          | HttpServletRequest | yes         | /             | request           |
| dataSourceTypeId | Long               | yes         | /             | dataSource typeId |

Return parameter： 

| Name    | Type   | If required | Default value | Remark                       |
| ------- | ------ | ----------- | ------------- | ---------------------------- |
| method  | String | yes         | /             | Called method (request path) |
| status  | int    | yes         | /             | Response status code         |
| message | String | no          | /             | Information of the response  |
| data    | Long   | yes         | /             | The returned data            |

### 15、Query datasource by datasourceId

Interface description：Query the datasource according to the request information, datasource type and datasource ID 

Request URL：/dss/exchangis/main/datasources/{type}/{id}/dbs

Request mode：GET

Request parameters： 

| Name    | Type               | If required | Default value | Remark          |
| ------- | ------------------ | ----------- | ------------- | --------------- |
| request | HttpServletRequest | yes         | /             | request         |
| type    | String             | yes         | /             | datasource type |
| id      | Long               | yes         | /             | datasource id   |

Return parameter： 

| Name    | Type           | If required | Default value | Remark                       |
| ------- | -------------- | ----------- | ------------- | ---------------------------- |
| method  | String         | yes         | /             | Called method (request path) |
| status  | int            | yes         | /             | Response status code         |
| message | String         | no          | /             | Information of the response  |
| data    | List\<String\> | yes         | /             | The returned data            |

### 16、Get table by datasourceId and datasourceName

Interface description：According to the request information, datasource type, datasource ID and database, a data table is obtained. 

Request URL：/dss/exchangis/main/datasources/{type}/{id}/dbs/{dbName}/tables

Request mode：GET

Request parameters： 

| Name    | Type               | If required | Default value | Remark         |
| ------- | ------------------ | ----------- | ------------- | -------------- |
| request | HttpServletRequest | yes         | /             | request        |
| type    | String             | yes         | /             | datsource type |
| id      | Long               | yes         | /             | datasource id  |
| dbName  | String             | yes         | /             | database name  |

Return parameter： 

| Name    | Type           | If required | Default value | Remark                       |
| ------- | -------------- | ----------- | ------------- | ---------------------------- |
| method  | String         | yes         | /             | Called method (request path) |
| status  | int            | yes         | /             | Response status code         |
| message | String         | no          | /             | Information of the response  |
| data    | List\<String\> | yes         | /             | The returned data            |

### 17、Get table field by datasourceId、datasourceName and table

Interface description：Get table fields according to request information, datasource type, datasource ID, database name and data table. 

Request URL：/dss/exchangis/main/datasources/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields

Request mode：GET

Request parameters： 

| Name      | Type               | If required | Default value | Remark          |
| --------- | ------------------ | ----------- | ------------- | --------------- |
| request   | HttpServletRequest | yes         | /             | request         |
| type      | String             | yes         | /             | datasource type |
| id        | Long               | yes         | /             | datasource id   |
| dbName    | String             | yes         | /             | database name   |
| tableName | String             | yes         | /             | table name      |

Return parameter： 

| Name    | Type                               | If required | Default value | Remark                       |
| ------- | ---------------------------------- | ----------- | ------------- | ---------------------------- |
| method  | String                             | yes         | /             | Called method (request path) |
| status  | int                                | yes         | /             | Response status code         |
| message | String                             | no          | /             | Information of the response  |
| data    | List\<DataSourceDbTableColumnDTO\> | yes         | /             | The returned data            |

### 18、Get fieldsmapping

Interface description：Acquiring table field list information according to the request information and field mapping VO 

Request URL：/dss/exchangis/main/datasources/fieldsmapping

Request mode：POST

Request parameters： 

| Name    | Type               | If required | Default value | Remark         |
| ------- | ------------------ | ----------- | ------------- | -------------- |
| request | HttpServletRequest | yes         | /             | request        |
| vo      | FieldMappingVO     | yes         | /             | FieldMappingVO |

Return parameter： 

| Name    | Type                      | If required | Default value | Remark                       |
| ------- | ------------------------- | ----------- | ------------- | ---------------------------- |
| method  | String                    | yes         | /             | Called method (request path) |
| status  | int                       | yes         | /             | Response status code         |
| message | String                    | no          | /             | Information of the response  |
| data    | List<Map<String, Object>> | yes         | /             | The returned data            |

### 19、Get params by datasourceType

Interface description：Get parameters according to request information, engine, datasource type and file system path

Request URL：/dss/exchangis/main/datasources/{engine}/{type}/params/ui

Request mode：POST

Request parameters： 

| Name    | Type               | If required | Default value | Remark          |
| ------- | ------------------ | ----------- | ------------- | --------------- |
| request | HttpServletRequest | yes         | /             | request         |
| engine  | String             | yes         | /             | enigne          |
| type    | String             | yes         | /             | datasource type |
| dir     | String             | yes         | /             | dir             |

Return parameter： 

| Name    | Type               | If required | Default value | Remark                       |
| ------- | ------------------ | ----------- | ------------- | ---------------------------- |
| method  | String             | yes         | /             | Called method (request path) |
| status  | int                | yes         | /             | Response status code         |
| message | String             | no          | /             | Information of the response  |
| data    | List<ElementUI<?>> | yes         | /             | The returned data            |



## Exchangis job execution module

### 1、Submit the configured job for execution. 

Interface description ： Submit ExchangisJob and return jobExecutionId in the background. 

Request URL：/api/rest_j/v1/exchangis/job/{id}/execute 

Request mode：POST 

Request parameters： 

| Name                  | Type    | Remark                                                       | If required | Default value |
| --------------------- | ------- | ------------------------------------------------------------ | ----------- | ------------- |
| id                    | Long    | Exchangis's ID                                               | yes         | /             |
| permitPartialFailures | boolean | Whether partial failure is allowed. If true, even if some subtasks fail, the whole Job will continue to execute. After the execution is completed, the Job status is Partial_Success. This parameter is the requestBody parameter. | no          | false         |

Return parameter ： 

| Name           | Type   | Remark                               | If required | Default value |
| -------------- | ------ | ------------------------------------ | ----------- | ------------- |
| method         | String | Called method (request path)         | yes         | /             |
| status         | int    | Response status code                 | yes         | /             |
| message        | String | Information of the response          | no          | /             |
| data           | Map    | The returned data                    | yes         | /             |
| jobExecutionId | String | Returns the execution log of the Job | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/{id}/execute",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
    "data": {
    "jobExecutionId": "555node1node2node3execId1"
}

```

### 2、Get the execution status of Job 

Interface description： Get the status of Job according to jobExecutionId

Request URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/status 

Request mode：GET 

Request parameters： 

| Name           | Type   | Remark                       | If required | Default value |
| -------------- | ------ | ---------------------------- | ----------- | ------------- |
| jobExecutionId | String | Execution ID of ExchangisJob | yes         | /             |

Back to example ：

| Name           | Type   | Remark                                                       | If required | Default value |
| -------------- | ------ | ------------------------------------------------------------ | ----------- | ------------- |
| method         | String | Called method (request path)                                 | yes         | /             |
| status         | int    | Response status code                                         | yes         | /             |
| message        | String | Information of the response                                  | no          | /             |
| data           | Map    | The returned data                                            | yes         | /             |
| jobExecutionId | String | The status of the executed job, including: initiated, Scheduled, Running, WaitForRetry, Cancelled, Failed, Partial_Success, Success, Undefined, Timeout. Among them, the Running state indicates that it is running, and all of them are completed since Cancelled | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{id}/status",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
    "data": {
    "status": "Running",
    "progress": 0.1
}
```

### 3、Get the task list executed by this Job

Interface description：Get the task list through jobExecutionId

Prerequisite: the task list can only be obtained after the Job's execution status is Running, otherwise the returned task list is empty. 

Request URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/tasklist 

Request mode：GET 

Request parameters： 

| Name           | Type   | Remark | If required | Default value |
| -------------- | ------ | ------ | ----------- | ------------- |
| jobExecutionId | String | string | yes         | /             |

Back to example ：

| Name           | Type   | Remark                                                       | If required | Default value |
| -------------- | ------ | ------------------------------------------------------------ | ----------- | ------------- |
| method         | String | Called method (request path)                                 | yes         | /             |
| status         | int    | Response status code                                         | yes         | /             |
| message        | String | Information of the response                                  | no          | /             |
| data           | Map    | The returned data                                            | yes         | /             |
| jobExecutionId | String | Task list. The execution status of the Job must be Running before you can get the task list, otherwise the returned task list is empty. Please note: task has no Partial_Success status | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/tasklist",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
    "data": {
        "tasks": [
            {
                "taskId": 5,
                "name": "test-1",
                "status": "Inited", // There is no task Partial_Success status.
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

###  4、Get the execution progress of Job & task

Interface description： Get the execution progress through jobExecutionId

Prerequisites: the execution status of the Job must be Running before you can get the progress of the task list. 

Request URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/progress  

Request mode：GET 

Request parameters： 

| Name           | Type   | Remark                       | If required | Default value |
| -------------- | ------ | ---------------------------- | ----------- | ------------- |
| jobExecutionId | String | Execution ID of ExchangisJob | yes         | /             |

Back to example ： 

| Name           | Type   | Remark                                                       | If required | Default value |
| -------------- | ------ | ------------------------------------------------------------ | ----------- | ------------- |
| method         | String | Called method (request path)                                 | yes         | /             |
| status         | int    | Response status code                                         | yes         | /             |
| message        | String | Information of the response                                  | no          | /             |
| data           | Map    | The returned data                                            | yes         | /             |
| jobExecutionId | String | Task list. The execution status of the Job must be Running before you can get the task list, otherwise the returned task list is empty | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/progress",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
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
                "Failed": [], // If there is a Failed task, the Job will fail directly.
                "WaitForRetry": [],
                "Cancelled": [], // If there is a Cancelled task, the Job will fail directly
                "Undefined": [], // If there is a Undefined task, the Job will fail directly
                "Timeout": []
            }
        }
    }
}

```

### 5、Get the indicator information of task runtime

Interface description：Through jobExecutionId and taskId, we can get the information of various indicators when task is running. 

Prerequisites: before you can get the indicator information of the task, the execution status of the task must be Running; otherwise, the returned information is empty. 

Request URL：/api/rest_j/v1/exchangis/task/execution/{taskId}/metrics 

Request mode：POST 

Request parameters： 

| Name           | Type   | Remark                                              | If required | Default value |
| -------------- | ------ | --------------------------------------------------- | ----------- | ------------- |
| jobExecutionId | String | Execution ID of ExchangisJob，put it in requestBody | yes         | /             |
| taskId         | String | Execution ID of task，put it in URI                 | yes         | /             |

Back to example ：

| Name           | Type   | Remark                                                       | If required | Default value |
| -------------- | ------ | ------------------------------------------------------------ | ----------- | ------------- |
| method         | String | Called method (request path)                                 | yes         | /             |
| status         | int    | Response status code                                         | yes         | /             |
| message        | String | Information of the response                                  | no          | /             |
| data           | Map    | The returned data                                            | yes         | /             |
| jobExecutionId | String | Information of each task index. The execution status of task must be Running before you can get the indicator information of task | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/task/execution/{taskId}/metrics",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
    "data": {
        "task": {
            "taskId": 5,
            "name": "test-1",
            "status": "running",
            "metrics": {
                "resourceUsed": {
                    "cpu": 10, // Unit：vcores
                    "memory": 20 // Unit：GB
                },
                "traffic": {
                    "source": "mysql",
                    "sink": "hive",
                    "flow": 100 // Unit：Records/S
                },
                "indicator": {
                    "exchangedRecords": 109345, // Unit：Records
                    "errorRecords": 5,
                    "ignoredRecords": 5
                }
            }
        }
    }
}
```

### 6、Get the real-time log of Job 

Interface description：Get the real-time log of Job through jobExecutionId. 

Request URL：/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/log? fromLine=&pageSize=&ignoreKeywords=&onlyKeywords=&lastRows= 

Request mode：GET 

Request parameters： 

| Name           | Type   | Remark                                                       | If required | Default value |
| -------------- | ------ | ------------------------------------------------------------ | ----------- | ------------- |
| jobExecutionId | String | Execution ID of ExchangisJob                                 | yes         | /             |
| fromLine       | int    | Read the starting line of                                    | no          | 0             |
| pageSize       | int    | Read the number of log lines this time                       | no          | 100           |
| ignoreKeywords | String | Ignore which keywords are in the line, and multiple keywords are separated by English | no          | /             |
| onlyKeywords   | String | Only the lines where keywords are located are selected, and multiple keywords are separated in English | no          | /             |
| lastRows       | int    | Read only the last few lines of the log, which is equivalent to tail -f log. When this parameter is greater than 0, all the above parameters will be invalid | no          | /             |

Back to example ：

| Name    | Type    | Remark                                                       | If required | Default value |
| ------- | ------- | ------------------------------------------------------------ | ----------- | ------------- |
| method  | String  | Called method (request path)                                 | yes         | /             |
| status  | int     | Response status code                                         | yes         | /             |
| message | String  | Information of the response                                  | no          | /             |
| data    | Map     | The returned data                                            | yes         | /             |
| endLine | int     | The end line number of this reading, you can continue reading the log from endLine+1 next time | yes         | /             |
| isEnd   | boolean | Have all the logs been read                                  | no          | /             |
| logs    | List    | Returns the execution log of the Job                         | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{jobExecutionId}/log",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
    "data": {
        "endLine": 99, // The end line number of this reading, you can continue reading the log from endLine+1 next time
        "isEnd": false, // Have all the logs been read
        "logs": [
            "all": "",
            "error": "",
            "warn": "",
            "info": ""
        ]
    }
}
```

### 7、Get the real-time log of task 

Interface description： Get the real-time log of task through jobExecutionId and taskId. 

Request URL：/api/rest_j/v1/exchangis/task/execution/{taskId}/log? jobExecutionId=&fromLine=&pageSize=&ignoreKeywords=&onlyKeywords=&lastRows= 

Request mode：GET 

Request parameters： 

| Name           | Type   | Remark                                                       | If required | Default value |
| -------------- | ------ | ------------------------------------------------------------ | ----------- | ------------- |
| taskId         | String | Execution ID of task                                         | yes         | /             |
| jobExecutionId | String | Execution ID of ExchangisJob                                 | yes         | /             |
| fromLine       | int    | Read the starting line of                                    | no          | 0             |
| pageSize       | int    | Read the number of log lines this time                       | no          | 100           |
| ignoreKeywords | String | Ignore which keywords are in the line, and multiple keywords are separated by English | no          | /             |
| onlyKeywords   | String | Only the lines where keywords are located are selected, and multiple keywords are separated in English | no          | /             |
| lastRows       | int    | Read only the last few lines of the log, which is equivalent to tail -f log. When this parameter is greater than 0, all the above parameters will be invalid | no          | /             |

Back to example ：

| Name    | Type    | Remark                                                       | If required | Default value |
| ------- | ------- | ------------------------------------------------------------ | ----------- | ------------- |
| method  | String  | Called method (request path)                                 | yes         | /             |
| status  | int     | Response status code                                         | yes         | /             |
| message | String  | Information of the response                                  | no          | /             |
| data    | Map     | The returned data                                            | yes         | /             |
| endLine | int     | The end line number of this reading, you can continue reading the log from endLine+1 next time. | yes         | /             |
| isEnd   | boolean | Have all the logs been read                                  | yes         | /             |
| logs    | List    | Returns the execution log of the Job                         | yes         | /             |

Back to example ：

```json
{
    "method": "/api/rest_j/v1/exchangis/job/execution/{taskId}/log",
    "status": 0,
    "message": "Submitted succeed(Submit successfully)！",
    "data": {
        "endLine": 99, // The end line number of this reading, you can continue reading the log from endLine+1 next time.
        "isEnd": false, // Have all the logs been read
        "logs": [
            "all": "",
            "error": "",
            "warn": "",
            "info": ""
        ]
    }
}
```

