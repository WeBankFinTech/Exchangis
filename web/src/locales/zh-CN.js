export default {
  globalMenu: {
    projectManage: "项目管理",
    dataSourceManage: "数据源管理",
    jobManagement: "数据任务管理",
  },
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
            status: "状态",
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
    list: '任务列表',
    type: {
      offline: '离线任务',
      stream: '流式任务'
    },
    action: {
      createJob: "新增任务",
      copyJob: "复制任务",
      import: '导入',
      export: '导出',
      save: '保存',
      cancel: '取消',
      addJobSuccess: '新建任务成功',
      copyJobSuccess: '复制任务成功',
      deleteJobSuccess: '删除任务成功',
      yes: '是',
      no: '不',
      confirmDelete: '确认删除该任务？',
      manage: '管理',

    },
    jobDetail: {
      originJob: '原任务',
      name: "任务名",
      label: '业务标签',
      type: '任务类型',
      engine: '执行引擎',
      description: '任务描述',
      jobNameEmpty: '任务名不能为空',
      jobTypeEmpty: '任务类型不能为空',
      engineEmpty: '执行引擎不能为空',
    },
  },
};
