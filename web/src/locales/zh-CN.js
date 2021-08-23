export default {
  dataSource: {
    topLine: {
      searchBar: {
        dataTypePlaceholder: "数据源类型",
        creatorPlaceholder: "创建者",
        namePlaceholder: "数据源名称",
        searchButtonText: "搜索",
      },
      createDataSourceButton: "创建数据源",
      importsDataSource: "批量导入数据源",
      exportsDataSource: "批量导出数据源",
    },
    table: {
      list: {
        columns: {
          title: {
            name: "数据源名称",
            type: "类型",
            colony: "集群",
            status: "状态",
            power: "权限",
            tags: "标签",
            version: "版本",
            describe: "描述",
            updatetim: "更新时间",
            creator: "创建者",
            updater: "更新者",
            action: "操作",
          },
          actions: {
            testConnectButton: "测试连接",
            editButton: "编辑",
            expireButton: "过期",
            deleteButton: "删除",
          },
        },
      },
    },
    editModal: {
      title: {
        create: "创建数据源",
        edit: "修改数据源",
      },
      form: {
        fields: {
          name: "",
        },
      },
    },
    sourceTypeModal: {
      title: "请选择数据源类型",
      searchInputPlaceholder: "输入关键字搜索数据源",
    },
  },
};
