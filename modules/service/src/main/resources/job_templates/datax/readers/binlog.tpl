{
  "name": "binlogreader",
  "parameter": {
      "datasource": #{datasourceId},
      "username": #{username},
      "password": #{password},
      "dbTableList": ${dbTableList|[]},
      "master":[{
        "host": #{host|host},
        "port": #{port|port}
      }],
      "slaves": ${slaves|[]}
  }
}