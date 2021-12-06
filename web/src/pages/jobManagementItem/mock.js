const jobInfo = {
  id: 1, // 任务id
  projectId: 1, // 所属项目id
  jobName: "任务名",
  jobType: "OFFLINE | STREAM",
  engineType: "DataX", // 执行引擎
  jobLabels: "",
  jobDesc: "任务描述",
  content: {
    subJobs: [
      {
        subjobName: "subjob1",
        dataSourceIds: {
          source: {
            type: "HIVE",
            id: "10001",
            db: "db_test",
            table: "table_test",
          },
          sink: {
            type: "MYSQL",
            id: "10002",
            db: "db_mask",
            table: "table_mask",
          },
        },
        params: {
          sources: [
            {
              field: "exchangis.job.hive.transform_type", // key
              label: "传输方式",
              values: ["Record", "二进制"],
              value: "二进制",
              unit: "",
              sort: 1,
              type: "OPTION",
              required: true,
            },
            {
              field: "exchangis.job.hive.partition",
              label: "分区信息",
              value: "2021-08-17",
              unit: "",
              sort: 2,
              type: "INPUT",
              required: false,
            },
          ],
          sinks: [],
        },
        transforms: {
          type: "MAPPING",
          sql: "", // type为SQL时，使用该字段
          mapping: [
            // type为MAPPING时，使用该字段
            {
              source_field_name: "field1",
              source_field_type: "varchar",
              sink_field_name: "field2",
              sink_field_type: "varchar",
              validator: ["> 100", "< 200"],
              transformer: {
                name: "ex_substr",
                params: ["1", "3"],
              },
            },
            {
              source_field_name: "field3",
              source_field_type: "varchar",
              sink_field_name: "field4",
              sink_field_type: "varchar",
              validator: ["like '%example'"],
              transformer: {
                name: "ex_replace",
                params: ["1", "4"],
              },
            },
          ],
        },
        settings: [
          {
            field: "errorlimit_percentage",
            label: "脏数据占比阈值",
            sort: 1,
            value: "",
            unit: "",
            type: "INPUT",
            required: true,
          },
          {
            field: "errorlimit_record",
            label: "脏数据最大记录数",
            sort: 2,
            value: "",
            unit: "条",
            type: "INPUT",
            required: true,
          },
          {
            field: "channel",
            label: "传输速率",
            sort: 3,
            value: "",
            unit: "Mb/s",
            type: "INPUT",
            required: true,
          },
          {
            field: "channel",
            label: "并发数",
            sort: 4,
            value: "",
            unit: "个",
            type: "INPUT",
            required: true,
          },
        ],
      },
      {
        subjobName: "subjob2",
        // ...
      },
    ],
  },
  proxyUser: "代理用户",
  executeNode: "执行节点",
  syncType: "同步类型",
  jobParams: "参数",
};

const fieldInfo = [
  {
    name: "field1",
    type: "VARCHAR",
  },
  {
    name: "field2",
    type: "VARCHAR",
  },
];

const SQLlist = [
  {
    classifier: "关系型数据库",
    name: "MYSQL",
    icon: "icon-mysql",
    description: "MYSQL description",
    id: "1",
    option: "mysql",
  },
  {
    classifier: "大数据存储",
    name: "HIVE",
    icon: "icon-hive",
    description: "Hive description",
    id: "2",
    option: "hive",
  },
];

const dbs = ["db1", "db2"];

const tables = ["table1", "table2"];

export { jobInfo, fieldInfo, SQLlist, dbs, tables };
