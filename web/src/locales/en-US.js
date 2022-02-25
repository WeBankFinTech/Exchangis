export default {
  globalMenu: {
    projectManage: "project",
    dataSourceManage: "data source",
    jobManagement: "job management",
    synchronizationHistory: "synchronization history",
    homePage: "home page",
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
            label: "Name",
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
        read: "DataSource"
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
    list: "Job List",
    type: {
      offline: "Offline Job",
      stream: "Stream Job",
    },
    action: {
      createJob: "Create Job",
      copyJob: "Copy Job",
      modifyJob: "Modify Job",
      import: "Import",
      export: "Export",
      save: "Save",
      cancel: "Cancel",
      addJobSuccess: "Create Job Success",
      copyJobSuccess: "Copy Job Success",
      deleteJobSuccess: "Delete Job Success",
      yes: "YES",
      no: "NO",
      confirmDelete: "Are you sure to delete this task?",
      manage: "MANAGE",
      fileUpSuccess: "File uploaded successfully",
      fileUpFailed: "File upload failed.",
      jobSearch: "Please input text for search job",
    },
    jobDetail: {
      originJob: "Original Job",
      name: "Job name",
      label: "Job label",
      type: "Job type",
      engine: "Engine",
      description: "Description",
      jobNameEmpty: "Job name cannot be empty",
      jobTypeEmpty: "Job type cannot be empty",
      engineEmpty: "Engine type cannot be empty",
    },
  },
  message: {
    "linkis": {
      "refresh": "Refresh",
      "noDescription": "No description",
      "placeholderZip": "Please enter the workspace Python package path (only zip is supported)!",
      "emptyString": "Empty string",
      "description": "Description",
      "name": "Name",
      "order": "Order",
      "addParameterConfig": "Add parameter configuration",
      "editDescriptionEngineConfig": "Edit description engine config",
      "addAppType": "Add application type",
      "editContents": "Add contents",
      "eurekeRegisterCenter": "Eureke Register Center",
      "addTags": "Add Tags",
      "unfold": "Unfold",
      "fold": "Fold",
      "jumpPage": "Please check in the jump page...",
      "initiator": "Initiator",
      "find": "Find",
      "errorCode": "Error code",
      "errorDescription": "Error description",
      "notLog": "Log not obtained!",
      "editedSuccess": "Edited success!",
      "stopEngineTip": "Are you sure you want to stop the current engine?",
      "addVariable": "Add variable",
      "defaultValue": "Default",
      "noDefaultValue": "No default value",
      "stop": "Stop",
      "tip": "Tip",
      "serverTip": "No Results（resultLocation:null）",
      "log": "Task log",
      "detail": "Task detail",
      "result": "Task result",
      "startUp": "Start-up",
      "tagEdit": "Edit",
      "rename": "Rename",
      "instanceNum": "Instance Number",
      "keyTip": "The key cannot be empty",
      "instanceName": "Instance Name",
      "resources": "Resources",
      "reset": "Reset",
      "remove": "Remove",
      "submit": "Submit",
      "search": "Search",
      "save": "Save",
      "edit": "Edit",
      "cancel": "Cancel",
      "noDataText": "No data yet",
      "userName": "Username",
      "unselect": "Please select task",
      "searchName": "Please enter username to search",
      "generalView": "Switch to the common view",
      "manageView": "Switch to the admin view",
      "back": "Back",
      "warning": {
        "api": "Requesting API, please hold on!",
        "data": "Requesting data, please hold on!",
        "waiting": "Please wait for API response!",
        "biLoading": "Connecting with Visualis, please hold on!",
        "comingSoon": "New version is being integrated, so stay tuned !",
        "GJZ": "Open source co-construction is in progress, so stay tuned!"
      },
      "resourceManagement": {
        "resourceUsage": "Resource usage",
        "applicationList": "Application List"
      },
      "time": {
        "second": "Second",
        "minute": "Minute",
        "hour": "Hour",
        "day": "Day"
      },
      "tableColumns": {
        "engineInstance": "Engine instance",
        "engineType": "Engine type",
        "taskID": "Task ID",
        "fileName": "Source",
        "executionCode": "Execution Code",
        "status": "Status",
        "label": "Label",
        "engineVersion": "Engine Version",
        "engineVersionCannotBeNull": "Engine Version Cannot Be Null",
        "usedResources": "Used resources",
        "maximumAvailableResources": "Maximum available resources",
        "minimumAvailableResources": "Minimum available resources",
        "startTime": "Start time",
        "costTime": "Time Elapsed",
        "executeApplicationName": "Execution Engine",
        "requestApplicationName": "Created From",
        "user": "User",
        "createdTime": "Created At",
        "updateTime": "Update Time",
        "failedReason": "Key Information",
        "control": {
          "title": "Handle",
          "label": "View"
        }
      },
      "logLoading": "Requesting logs, please hold on",
      "title": "Linkis Control Panel",
      "info": "{num} new messages",
      "hint": "Please view in the redirected page……",
      "sideNavList": {
        "news": {
          "name": "Latest Activities",
          "children": {
            "daily": "Daily Operation Report"
          }
        },
        "function": {
          "name": "Frequently Used",
          "children": {
            "globalHistory": "Global History",
            "resource": "Resource Manager",
            "setting": "Settings",
            "dateReport": "Global Variables",
            "globalValiable": "Frequently Asked",
            "microserviceManage": "Microservice management",
            "ECMManage": "ECM Management",
            "dataSourceManage": "DataSource Manage"
          }
        }
      },
      "formItems": {
        "id": {
          "placeholder": "Please search by entering the ID"
        },
        "date": {
          "label": " Start Date",
          "placeholder": "Please choose the start date"
        },
        "engine": {
          "label": "Engine"
        },
        "status": {
          "label": "Status"
        }
      },
      "columns": {
        "taskID": "Task ID",
        "fileName": "Script Name",
        "executionCode": "Execution Code",
        "status": "Status",
        "costTime": "Time Elapsed",
        "executeApplicationName": "Execution Engine",
        "requestApplicationName": "Created From",
        "progress": "Progress",
        "createdTime": "Created At",
        "updatedTime": "Updated At",
        "control": {
          "title": "Handle",
          "label": "View"
        },
        "moduleName": "Module Name",
        "totalResource": "Total Resources",
        "usedResource": "Used Resources",
        "initializingResource": "Initing Resources",
        "memory": "Memory",
        "engineInstance": "Engine Instance",
        "applicationName": "Application Name",
        "usedTime": "Started At",
        "engineStatus": "Status",
        "username": "Username"
      },
      "shortcuts": {
        "week": "Recent Week",
        "month": "Recent Month",
        "threeMonths": "Recent Three Months"
      },
      "statusType": {
        "all": "All",
        "inited": "Waiting",
        "running": "Running",
        "succeed": "Succeeded",
        "cancelled": "Canceled",
        "failed": "Failed",
        "scheduled": "Applying Resources",
        "timeout": "Timeout",
        "retry": "Retry",
        "unknown": "Unknown"
      },
      "engineTypes": {
        "all": "All"
      },
      "header": "Resource Manager",
      "tabs": {
        "first": "User Sessions",
        "second": "User Resources",
        "third": "Server Resources"
      },
      "noLimit": "Unlimited",
      "core": "Cores",
      "row": {
        "applicationName": "Application Name",
        "usedTime": "Started At",
        "engineStatus": "Status",
        "engineInstance": "Engine Instance",
        "queueName": "Queue Name",
        "user": "User",
        "cpu": "Used server CPU resources",
        "memory": "Used server memory resources",
        "queueCpu": "Used Yarn queue CPU resources",
        "queueMemory": "Used Yarn queue memory resources"
      },
      "setting": {
        "global": "Global",
        "globalSetting": "Global Settings",
        "hide": "Hide",
        "show": "Show",
        "advancedSetting": "Advanced Settings",
        "dataDev": "Data Development"
      },
      "globalValiable": "Global Variables",
      "rules": {
        "first": {
          "required": "The key of variable {text} is empty",
          "lengthLimit": "Length between 1 to 128 characters",
          "letterTypeLimit": "Started with alphabetic characters, spance and Chinese characters are not allowed",
          "placeholder": "Please enter the variable name"
        },
        "second": {
          "required": "The value of variable {text} is empty",
          "lengthLimit": "Length between 1 to 128 characters",
          "placeholder": "Please enter the variable value"
        }
      },
      "addArgs": "New argument",
      "emptyDataText": "No global variable data yet",
      "sameName": "Duplicated key",
      "error": {
        "validate": "Invalid items found, please check and then retry!"
      },
      "success": {
        "update": "Successfully updated global variables!"
      },
      "datasource": {
        "pleaseInput": "Please input",
        "datasourceSrc": "Datasource",
        "connectTest": "Test Connection",
        "sourceName": "Data source name",
        "sourceDec": "Data source description",
        "sourceType": "Data source type:",
        "creator": "Creator:",
        "create": "New data source",
        "exports": "Demonstration export data source",
        "imports": "Demonstration of importing data sources",
        "Expired": "Expired",
        "versionList": "Version List",
        "dataSourceName": "Data Source Name",
        "dataSourceType": "Data Source Type",
        "dataSourceEnv": "Available Space",
        "status": "Status",
        "permissions": "Permissions",
        "label": "label",
        "Version": "Version",
        "desc": "Description",
        "action": "Action",
        "createUser": "Create User",
        "createTime": "Create Time",
        "versionDec": "Version Description",
        "watch": "View",
        "rollback": "Rollback",
        "publish": "Publish",
        "initVersion": "Initial Version",
        "updateVersion": "Version update",
        "published": "Published",
        "unpublish": "Unpublished",
        "cannotPublish": "Cannot Publish",
        "used": "Available",
        "commentValue": "Roll back from version {text}"
      }
    }
  }
};
