export default {
  projectManage: {
    topLine: {
      searchBar: {
        searchInputPlaceholder: "输入项目名搜索",
        searchButtonText: "搜索",
      },
      title: "项目列表",
    },
    createCardText: "创建项目",
    viewCard: {
      actionTitle: "操作",
      action: {
        edit: "编辑",
        delete: "删除",
      },
    },
    editModal: {
      title: {
        create: "创建项目",
        edit: "修改项目",
      },
      form: {
        fields: {
          projectName: {
            label: "项目名",
            placeholder: "请输入项目名",
          },
          tags: {
            label: "标签",
            placeholder: "请选择标签",
          },
          description: {
            label: "描述",
            placeholder: "请填写描述",
          },
          viewUsers: {
            label: "查看权限",
            placeholder: "请选择查看权限",
          },
          execUsers: {
            label: "执行权限",
            placeholder: "请选择执行权限",
          },
          editUsers: {
            label: "编辑权限",
            placeholder: "请选择编辑权限",
          },
        },
      },
    },
  },
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
          dataSourceName: {
            label: "数据源名称",
            placeholder: "请输入数据源名称",
          },
          dataSourceDesc: {
            label: "描述",
            placeholder: "请输入描述",
          },
        },
      },
    },
    sourceTypeModal: {
      title: "DataSourceType",
      searchInputPlaceholder: "输入关键字搜索数据源",
    },
  },
  job: {
    action: {
      createJob: "新增任务",
      copyJob: "复制任务",
    },
    jobDetail: {
      name: "任务名",
    },
  },
};
