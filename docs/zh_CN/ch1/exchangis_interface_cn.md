# Exchangis接口文档

## Exchangis数据源模块

### 1、获取数据源类型

接口描述：根据request的信息来获取数据源类型

请求URL：/dss/exchangis/main/datasources/type

请求方式：GET

请求参数： 

| 名称    | 类型               | 备注 | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | ---- | -------- | ------ | ----------- |
| request | HttpServletRequest |      | 是       | /      | request请求 |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       |        | 调用的方法(请求路径) |
| status  | int    | 是       |        | 响应状态码           |
| message | String | 否       |        | 响应的信息           |
| data    | List   | 是       |        | 返回的数据           |

### 2、查询数据源

接口描述：根据vo查询所需的数据源

请求URL：/dss/exchangis/main/datasources/query

请求方式：GET、POST

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| vo      | DataSourceQueryVO  | 是       | /      |             |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | List   | 是       | /      | 返回的数据           |

### 3、查询数据源

接口描述：根据request信息查询数据源

请求URL：/dss/exchangis/main/datasources

请求方式：GET

请求参数： 

| 名称     | 类型               | 是否必须 | 默认值 | 备注         |
| -------- | ------------------ | -------- | ------ | ------------ |
| request  | HttpServletRequest | 是       | /      | request请求  |
| typeId   | Long               | 是       | /      | 数据源类型ID |
| typeName | String             | 是       | /      | 数据源类型   |
| page     | Integer            | 是       | /      | 页数         |
| size     | Integer            | 是       | /      | 每页大小     |

返回参数： 

| 名称    | 类型                  | 是否必须 | 默认值 | 备注                 |
| ------- | --------------------- | -------- | ------ | -------------------- |
| method  | String                | 是       | /      | 调用的方法(请求路径) |
| status  | int                   | 是       | /      | 响应状态码           |
| message | String                | 否       | /      | 响应的信息           |
| data    | List\<DataSourceDTO\> | 是       | /      | 返回的数据           |

### 4、查询数据源关键定义

接口描述：根据数据源类型ID查询数据源关键定义

请求URL：/dss/exchangis/main/datasources/types/{dataSourceTypeId}/keydefines

请求方式：GET

请求参数： 

| 名称             | 类型               | 是否必须 | 默认值 | 备注         |
| ---------------- | ------------------ | -------- | ------ | ------------ |
| request          | HttpServletRequest | 是       | /      | request请求  |
| dataSourceTypeId | Long               | 是       | /      | 数据源类型ID |

返回参数： 

| 名称    | 类型                   | 是否必须 | 默认值 | 备注                 |
| ------- | ---------------------- | -------- | ------ | -------------------- |
| method  | String                 | 是       | /      | 调用的方法(请求路径) |
| status  | int                    | 是       | /      | 响应状态码           |
| message | String                 | 否       | /      | 响应的信息           |
| data    | List[Map[String, Any]] | 是       | /      | 返回的数据           |

### 5、获取数据源版本

接口描述：根据数据源ID获取数据源版本

请求URL：/dss/exchangis/main/datasources/{id}/versions

请求方式：GET

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| id      | Long               | 是       | /      | 数据源ID    |

返回参数： 

| 名称    | 类型                      | 是否必须 | 默认值 | 备注                 |
| ------- | ------------------------- | -------- | ------ | -------------------- |
| method  | String                    | 是       | /      | 调用的方法(请求路径) |
| status  | int                       | 是       | /      | 响应状态码           |
| message | String                    | 否       | /      | 响应的信息           |
| data    | List<Map<String, Object>> | 是       | /      | 返回的数据           |

### 6、创建数据源

接口描述：根据数据源创建VO创建数据源

请求URL：/dss/exchangis/main/datasources

请求方式：POST

请求参数： 

| 名称               | 类型               | 是否必须 | 默认值 | 备注         |
| ------------------ | ------------------ | -------- | ------ | ------------ |
| request            | HttpServletRequest | 是       | /      | request请求  |
| dataSourceCreateVO | DataSourceCreateVO | 是       | /      | 数据源创建VO |
| bindingResult      | BindingResult      | 是       | /      | 绑定结果     |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Long   | 是       | /      | 返回的数据           |

### 7、获取数据源信息

接口描述：根据request信息、数据源ID和版本ID获取数据源信息

请求URL：/dss/exchangis/main/datasources/{id}

请求方式：GET

请求参数： 

| 名称      | 类型               | 是否必须 | 默认值 | 备注        |
| --------- | ------------------ | -------- | ------ | ----------- |
| request   | HttpServletRequest | 是       | /      | request请求 |
| id        | Long               | 是       | /      | 数据源ID    |
| versionId | String             | 是       | /      | 版本ID      |

返回参数： 

| 名称    | 类型              | 是否必须 | 默认值 | 备注                 |
| ------- | ----------------- | -------- | ------ | -------------------- |
| method  | String            | 是       | /      | 调用的方法(请求路径) |
| status  | int               | 是       | /      | 响应状态码           |
| message | String            | 否       | /      | 响应的信息           |
| data    | DataSourceItemDTO | 是       | /      | 返回的数据           |

### 8、获取数据源连接参数

接口描述：根据request信息和数据源ID获取数据源连接参数

请求URL：/dss/exchangis/main/datasources/{id}/connect_params

请求方式：GET

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| Long    | id                 | 是       | /      | 数据源ID    |

返回参数： 

| 名称    | 类型                       | 是否必须 | 默认值 | 备注                 |
| ------- | -------------------------- | -------- | ------ | -------------------- |
| method  | String                     | 是       | /      | 调用的方法(请求路径) |
| status  | int                        | 是       | /      | 响应状态码           |
| message | String                     | 否       | /      | 响应的信息           |
| data    | java.util.Map[String, Any] | 是       | /      | 返回的数据           |

### 9、更新数据源

接口描述：根据request信息、数据源类型ID和数据源创建VO更新数据源

请求URL：/dss/exchangis/main/datasources/{id}

请求方式：PUT

请求参数： 

| 名称               | 类型               | 是否必须 | 默认值 | 备注         |
| ------------------ | ------------------ | -------- | ------ | ------------ |
| request            | HttpServletRequest | 是       | /      | request请求  |
| id                 | Long               | 是       | /      | 数据源类型ID |
| dataSourceCreateVO | DataSourceCreateVO | 是       | /      | 数据源创建VO |
| bindingResult      | BindingResult      | 是       | /      | 绑定结果     |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Map    | 是       | /      | 返回的数据           |

### 10、发布数据源

接口描述：根据request请求、数据源ID和版本ID发布数据源

请求URL：/dss/exchangis/main/datasources/{id}/{version}/publish

请求方式：PUT

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| id      | Long               | 是       | /      | 数据源ID    |
| version | Long               | 是       | /      | 版本ID      |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Map    | 是       | /      | 返回的数据           |

### 11、过期数据源

接口描述：根据request信息和数据源ID过期数据源

请求URL：/dss/exchangis/main/datasources/{id}/expire

请求方式：PUT

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| id      | Long               | 是       | /      | 数据源ID    |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Map    | 是       | /      | 返回的数据           |

### 12、连接数据源

接口描述：根据request信息、数据源ID和版本ID连接数据源

请求URL：/dss/exchangis/main/datasources/{id}/{version}/connect

请求方式：PUT

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| id      | Long               | 是       | /      | 数据源ID    |
| version | Long               | 是       | /      | 版本ID      |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Map    | 是       | /      | 返回的数据           |

### 13、连接数据源

接口描述：根据request信息和数据源ID连接数据源

请求URL：/dss/exchangis/main/datasources/op/connect

请求方式：POST

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| id      | Long               | 是       | /      | 数据源ID    |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Map    | 是       | /      | 返回的数据           |

### 14、删除数据源

接口描述：根据request信息和数据源类型ID连接数据源

请求URL：/dss/exchangis/main/datasources/{id}

请求方式：DELETE

请求参数： 

| 名称             | 类型               | 是否必须 | 默认值 | 备注         |
| ---------------- | ------------------ | -------- | ------ | ------------ |
| request          | HttpServletRequest | 是       | /      | request请求  |
| dataSourceTypeId | Long               | 是       | /      | 数据源类型ID |

返回参数： 

| 名称    | 类型   | 是否必须 | 默认值 | 备注                 |
| ------- | ------ | -------- | ------ | -------------------- |
| method  | String | 是       | /      | 调用的方法(请求路径) |
| status  | int    | 是       | /      | 响应状态码           |
| message | String | 否       | /      | 响应的信息           |
| data    | Long   | 是       | /      | 返回的数据           |

### 15、根据数据源ID查询数据源

接口描述：根据request信息、数据源类型和数据源ID查询数据源

请求URL：/dss/exchangis/main/datasources/{type}/{id}/dbs

请求方式：GET

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| type    | String             | 是       | /      | 数据源类型  |
| id      | Long               | 是       | /      | 数据源ID    |

返回参数： 

| 名称    | 类型           | 是否必须 | 默认值 | 备注                 |
| ------- | -------------- | -------- | ------ | -------------------- |
| method  | String         | 是       | /      | 调用的方法(请求路径) |
| status  | int            | 是       | /      | 响应状态码           |
| message | String         | 否       | /      | 响应的信息           |
| data    | List\<String\> | 是       | /      | 返回的数据           |

### 16、根据数据源ID和数据库获取数据表

接口描述：根据request信息、数据源类型、数据源ID和数据库获取数据表

请求URL：/dss/exchangis/main/datasources/{type}/{id}/dbs/{dbName}/tables

请求方式：GET

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| type    | String             | 是       | /      | 数据源类型  |
| id      | Long               | 是       | /      | 数据源ID    |
| dbName  | String             | 是       | /      | 数据库名    |

返回参数： 

| 名称    | 类型           | 是否必须 | 默认值 | 备注                 |
| ------- | -------------- | -------- | ------ | -------------------- |
| method  | String         | 是       | /      | 调用的方法(请求路径) |
| status  | int            | 是       | /      | 响应状态码           |
| message | String         | 否       | /      | 响应的信息           |
| data    | List\<String\> | 是       | /      | 返回的数据           |

### 17、根据数据源ID、数据库和数据表获取表字段

接口描述：根据request信息、数据源类型、数据源ID和数据库名和数据表获取表字段

请求URL：/dss/exchangis/main/datasources/{type}/{id}/dbs/{dbName}/tables/{tableName}/fields

请求方式：GET

请求参数： 

| 名称      | 类型               | 是否必须 | 默认值 | 备注        |
| --------- | ------------------ | -------- | ------ | ----------- |
| request   | HttpServletRequest | 是       | /      | request请求 |
| type      | String             | 是       | /      | 数据源类型  |
| id        | Long               | 是       | /      | 数据源ID    |
| dbName    | String             | 是       | /      | 数据库名    |
| tableName | String             | 是       | /      | 数据表      |

返回参数： 

| 名称    | 类型                               | 是否必须 | 默认值 | 备注                 |
| ------- | ---------------------------------- | -------- | ------ | -------------------- |
| method  | String                             | 是       | /      | 调用的方法(请求路径) |
| status  | int                                | 是       | /      | 响应状态码           |
| message | String                             | 否       | /      | 响应的信息           |
| data    | List\<DataSourceDbTableColumnDTO\> | 是       | /      | 返回的数据           |

### 18、获取表字段列表信息

接口描述：根据request信息和字段映射VO获取表字段列表信息

请求URL：/dss/exchangis/main/datasources/fieldsmapping

请求方式：POST

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注        |
| ------- | ------------------ | -------- | ------ | ----------- |
| request | HttpServletRequest | 是       | /      | request请求 |
| vo      | FieldMappingVO     | 是       | /      | 字段映射VO  |

返回参数： 

| 名称    | 类型                      | 是否必须 | 默认值 | 备注                 |
| ------- | ------------------------- | -------- | ------ | -------------------- |
| method  | String                    | 是       | /      | 调用的方法(请求路径) |
| status  | int                       | 是       | /      | 响应状态码           |
| message | String                    | 否       | /      | 响应的信息           |
| data    | List<Map<String, Object>> | 是       | /      | 返回的数据           |

### 19、根据数据源类型获取参数

接口描述：根据request信息、引擎、数据源类型和文件系统路径获取参数

请求URL：/dss/exchangis/main/datasources/{engine}/{type}/params/ui

请求方式：POST

请求参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注         |
| ------- | ------------------ | -------- | ------ | ------------ |
| request | HttpServletRequest | 是       | /      | request请求  |
| engine  | String             | 是       | /      | 引擎         |
| type    | String             | 是       | /      | 数据源类型   |
| dir     | String             | 是       | /      | 文件系统路径 |

返回参数： 

| 名称    | 类型               | 是否必须 | 默认值 | 备注                 |
| ------- | ------------------ | -------- | ------ | -------------------- |
| method  | String             | 是       | /      | 调用的方法(请求路径) |
| status  | int                | 是       | /      | 响应状态码           |
| message | String             | 否       | /      | 响应的信息           |
| data    | List<ElementUI<?>> | 是       | /      | 返回的数据           |



## Exchangis作业执行模块

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

