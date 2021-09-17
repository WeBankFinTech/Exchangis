export default {
  globalMenu: {
    projectManage: "project",
    dataSourceManage: "data source",
    jobManagement: "job management",
  },
  projectManage: {
    topLine: {
      searchBar: {
        searchInputPlaceholder: "search by name",
        searchButtonText: "Search",
      },
      title: "Project List",
    },
    createCardText: "Create Project",
    viewCard: {
      actionTitle: "action",
      action: {
        edit: "edit",
        delete: "delete",
      },
    },
    editModal: {
      title: {
        create: "Create Project",
        edit: "Edit Project",
      },
      form: {
        fields: {
          projectName: {
            label: "Project Name",
            placeholder: "please enter Project Name",
          },
          tags: {
            label: "Tags",
            placeholder: "please select Project Tags",
          },
          description: {
            label: "describe",
            placeholder: "please enter describe",
          },
          viewUsers: {
            label: "viewUsers",
            placeholder: "please select viewUsers",
          },
          execUsers: {
            label: "execUsers",
            placeholder: "please select execUsers",
          },
          editUsers: {
            label: "editUsers",
            placeholder: "please select editUsers",
          },
        },
      },
    },
  },
  dataSource: {
    topLine: {
      searchBar: {
        dataTypePlaceholder: "DataSourceType",
        creatorPlaceholder: "creator",
        namePlaceholder: "DataSourceName",
        searchButtonText: "Search",
      },
      createDataSourceButton: "CreateDataSource",
      importsDataSource: "Batch import DataSource",
      exportsDataSource: "Batch export DataSource",
    },
    table: {
      list: {
        columns: {
          title: {
            name: "Name",
            type: "Type",
            status: "status",
            tags: "tags",
            version: "version",
            describe: "describe",
            updatetim: "updatetim",
            creator: "creator",
            updater: "updater",
            action: "action",
          },
          actions: {
            testConnectButton: "TestConnect",
            editButton: "Edit",
            expireButton: "Expire",
            deleteButton: "Delete",
          },
        },
      },
    },
    editModal: {
      title: {
        create: "CreateDataSource",
        edit: "EditDataSource",
      },
      form: {
        fields: {
          dataSourceName: {
            label: "Name",
            placeholder: "please enter DataSourceName",
          },
          dataSourceDesc: {
            label: "describe",
            placeholder: "please enter describe",
          },
        },
      },
    },
    sourceTypeModal: {
      title: "DataSourceType",
      searchInputPlaceholder: "Search Type By Name",
    },
  },

  overview: "Overview",
  i18n: {
    internationalization: "internationalization，base on",
    achieve: "to achieve.",
    ui: "UI components",
  },
  job: {
    list: 'Job List',
    type: {
      offline: 'Offline Job',
      stream: 'Stream Job'
    },
    action: {
      createJob: "Create Job",
      copyJob: "Copy Job",
      import: 'Import',
      export: 'Export',
      save: 'Save',
      cancel: 'Cancel',
    },
    jobDetail: {
      originJob: 'Original Job',
      name: "Job name",
      label: 'Job label',
      type: 'Job type',
      engine: 'Engine',
      description: 'Description',
    },
  },
};
