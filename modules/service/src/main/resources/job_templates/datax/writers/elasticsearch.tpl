{
  "name": "elasticsearchwriter",
  "parameter":{
     "datasource": #{datasourceId},
     "elasticUrls": #{elasticUrls},
     "index": #{indexName},
     "type": #{indexType|_doc},
     "username": #{username},
     "password": #{password},
     "column":${column|""},
     "settings":{
        "index.merge.scheduler.max_merge_count": "100"
     },
     "cleanUp": #{cleanUp|false},
     "bulkPerTask": 1,
     "bulkActions": #{batchSize|1000},
     "clientConfig":{
        "maxPoolSize": 1,
        "sockTimeout": 60000,
        "connTimeout": 60000,
        "timeout": 60000,
        "masterTimeout": 60000,
        "compress": true
     }
  }
}