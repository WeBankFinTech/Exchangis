// 服务端接口管理
import { request } from "@fesjs/fes";
import { BASE_URL } from "@/common/constants";

import { getEnvironment } from "@/common/utils";

////////////////////////////////////////////////////////////////////
export const getProjectList = (name, current, size) => {
  return request(`/projects?name=${name}&current=${current}&size=${size}`, {
    labels: {
      route: getEnvironment()
    }
  }, { method: "POST" });
};

export const createProject = (body) => {
  return request("/createProject", {
    ...body,
    labels: {
      route: getEnvironment()
    }
  });
};

export const deleteProject = (id) => {
  return request("/projects/" + id, {
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "DELETE",
  });
};

export const getProjectById = (id) => {
  return request("/projects/" + id + '?labels=' + getEnvironment(), null, {
    method: "GET",
  });
};

export const updateProject = (body) => {
  return request("/updateProject", {
    ...body,
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "PUT",
  });
};

export const getDataSourceList = (params) => {
  return request("/datasources/query", {
    ...params,
    labels: {
      route: getEnvironment()
    }
  }, { method: "POST" });
};

// 数据源管理 获取数据源
export const getDataSourceTypes = (param) => {
  let extra = ''
  if (param) {
    const { engineType, direct, sourceType } = param
    extra = `&engineType=${engineType}&direct=${direct}${sourceType ? '&sourceType=' + sourceType : ''}`
  }
  return request(
    `/datasources/type?labels=${getEnvironment()}&t=_${new Date().getTime()}${extra}`,
    {},
    { method: "GET" }
  );
};

// 数据源管理 获取动态参数
export const getKeyDefine = (dataSourceTypeId) => {
  return request(
    `/datasources/types/${dataSourceTypeId}/keydefines?labels=${getEnvironment()}&t=_${new Date().getTime()}`,
    {},
    { method: "GET" }
  );
};

// 查询数据源
export const getDataSource = (body) => {
  return request("/datasources/query", {
    ...body,
    labels: {
      route: getEnvironment()
    }
  }, { method: "POST" });
};

export const getDBs = (type, id) => {
  return request(`/datasources/${type}/${id}/dbs?labels=${getEnvironment()}`, {}, { method: "GET" });
};

export const getTables = (type, id, dbName) => {
  return request(
    `/datasources/${type}/${id}/dbs/${dbName}/tables?labels=${getEnvironment()}`,
    {},
    { method: "GET" }
  );
};

/*export const getFields = (type, id, dbName, tableName) => {
  return request(
    `/datasources/${type}/${id}/dbs/${dbName}/tables/${tableName}/fields`,
    {},
    { method: "GET" }
  );
};*/
// /datasources/fieldsmaping
export const getFields = (params) => {
  return request(
    `/job/transform/settings`,
    {
      ...params,
      labels: {
        route: getEnvironment()
      }
    },
    { method: "POST" }
  );
};

export const createDataSource = (params) => {
  return request("/datasources", {
    ...params,
    labels: {
      route: getEnvironment()
    }
  }, { method: "POST" });
};

export const updateDataSource = (id, params) => {
  return request("/datasources/" + id, {
    ...params,
    labels: {
      route: getEnvironment()
    }
  }, { method: "PUT" });
};

export const deleteDataSource = (id) => {
  return request(`/datasources/${id}`, {
    labels: {
      route: getEnvironment()
    }
  }, { method: "DELETE" });
};

export const getDataSourceVersionList = (id) => {
  return request(`/datasources/${id}/versions?labels=${getEnvironment()}`, {}, { method: "GET" });
};

export const testDataSourceConnect = (type, id) => {
  return request(`/datasources/${type}/${id}/connect?_=${Math.random()}`, {
    labels: {
      route: getEnvironment()
    }
  }, { method: "PUT" });
};

export const testDataSourceNotSavedConnect = (params) => {
  return request(`/datasources/op/connect?_=${Math.random()}`, {
    ...params,
    labels: {
      route: getEnvironment()
    }
  }, { method: "POST" });
};

export const getDataSourceById = (id, versionId) => {
  return request(`/datasources/${id}?labels=${getEnvironment()}`, {versionId}, { method: "GET" });
};

export const getJobInfo = (id) => {
  return request(`/job/${id}?labels=${getEnvironment()}`, null, {
    method: "GET",
  });
};

//获取任务列表
export const getJobList = (query) => {
  return request(`/job?labels=${getEnvironment()}&${query}`, null, {
    method: "GET",
  });
};

//获取执行引擎列表
export const getEngineType = () => {
  return request(`/job/engineType?labels=${getEnvironment()}`, null, {
    method: "GET",
  });
};

//新建任务
export const createJob = (params) => {
  return request(
    `/job`,
    {
      ...params,
      labels: {
        route: getEnvironment()
      }
    },
    {
      method: "POST",
    }
  );
};

//复制任务
export const copyJob = (id, params) => {
  return request(
    `/job/${id}/copy`,
    {
      ...params,
      labels: {
        route: getEnvironment()
      }
    },
    {
      method: "POST",
    }
  );
};

//编辑任务
export const modifyJob = (id, params) => {
  return request(
    `/job/${id}`,
    {
      ...params,
      labels: {
        route: getEnvironment()
      }
    },
    {
      method: "PUT",
    }
  );
};

//删除任务
export const deleteJob = (id) => {
  return request(`/job/${id}`, {
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "DELETE",
  });
};

//导入任务
export const importJob = (id, params) => {
  return request(
    `/job/import`,
    {
      ...params,
      labels: {
        route: getEnvironment()
      }
    },
    {
      method: "POST",
    }
  );
};

//执行任务
export const executeTask = (id) => {
  return request(`/job/${id}/action/execute`, {
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "POST",
  });
};

export const getJobs = (id, jobType, name, current, size) => {
  return request(`/job?labels=${getEnvironment()}&projectId=${id}&jobType=${jobType}&name=${name}&current=${current}&size=${size}`, null, {
    method: "GET",
  });
};

export const saveProject = (id, body, type = 'save') => {
  return request(`/job/${id}/content`, {
    ...body,
    labels: {
      route: getEnvironment()
    }
  }, {
    headers: { 'save-from': type },
    method: "PUT",
  });
};

// 保存/更新任务配置
export const updateTaskConfiguration = (id, body) => {
  return request(`/job/${id}/config`, {
    ...body,
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "PUT",
  });
};

export const expireDataSource = (id) => {
  return request(`/datasources/${id}/expire`, {
    labels: {
      route: getEnvironment()
    }
  }, { method: "PUT" });
};

export const publishDataSource = (id, versionId) => {
  return request(
    `/datasources/${id}/${versionId}/publish`,
    {
      labels: {
        route: getEnvironment()
      }
    },
    { method: "PUT" }
  );
};

export const getSourceParams = (engineType, type, ds) => {
  return request(
    `/datasources/${engineType}/${type}/params/ui?labels=${getEnvironment()}&dir=${ds}`,
    {},
    { method: "GET" }
  );
};

export const getSettingsParams = (engineType) => {
  return request(
    `/jobs/engine/${engineType}/settings/ui?labels=${getEnvironment()}`,
    {},
    { method: "GET" }
  );
};

// job执行
/*export const executeJob = (id) => {
  return request(`/job/${id}/action/execute`, {}, {
    method: "POST",
  });
};*/

// 同步历史
export const getSyncHistory = (body) => {
  return request("/tasks?labels=" + getEnvironment(), body, {
    method: "GET",
  });
};
// 新版同步历史-获取job列表
export const getSyncHistoryJobList = (body) => {
  return request("/job/listJobs?labels=" + getEnvironment(), body, {
    method: "GET",
  });
};
// 删除同步历史
export const delSyncHistory = (jobExecutionId) => {
  return request(`/job/${jobExecutionId}/deleteJob`, {
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "POST",
  });
};
// 读取Task限速配置
export const getSpeedLimit = (params) => {
  return request(
    `/job/${params.jobId}/speedlimit/${params.taskName}/params/ui?labels=${getEnvironment()}`,
    {},
    {
      method: "GET",
    }
  );
};
// 保存Task限速配置
export const saveSpeedLimit = (params, body) => {
  return request(`/job/${params.jobId}/speedlimit/${params.taskName}`, {
    ...body,
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "PUT",
  });
};

// 获取运行日志
export const getLogs = (params) => {
  return request(
    `/execution/tasks/${params.taskID}/logs?labels=${getEnvironment()}`,
    {
      fromLine: params.fromLine || 1,
      pageSize: params.pageSize || 10
    },
    {
      method: "GET",
    }
  );
}


// 首页相关

// 任务状态
export const getTaskState = () => {
  return request("/metrics/taskstate?labels=" + getEnvironment(), {}, { method: "GET" });
};

// 任务进度
export const getTaskProcess = () => {
  return request("/metrics/taskprocess?labels=" + getEnvironment(), {}, { method: "GET" });
};

// 流量监控
export const getDataSourceFlow = () => {
  return request("/metrics/datasourceflow?labels=" + getEnvironment(), {}, { method: "GET" });
};

// 资源使用
export const getEngineriesSource = () => {
  return request("/metrics/engineresource?labels=" + getEnvironment(), {}, { method: "GET" });
};

export const getEngineriesSourceCpu = () => {
  return request("/metrics/engineresourcecpu?labels=" + getEnvironment(), {}, { method: "GET" });
};

export const getEngineriesSourceMem = () => {
  return request("/metrics/engineresourcemem?labels=" + getEnvironment(), {}, { method: "GET" });
};


/* 作业执行模块接口 */
export const executeJob = (id) => {
  return request(`/job/${id}/execute`,{
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "POST",
  })
}

export const getJobStatus = (id) => {
  return request(`/job/execution/${id}/status?labels=${getEnvironment()}`, {}, {
    method: "GET",
  })
}

export const getJobTasks = (id) => {
  return request(`/job/execution/${id}/taskList?labels=${getEnvironment()}`, null, {
    method: "GET",
  })
}

export const getProgress = (id) => {
  return request(`/job/execution/${id}/progress?labels=${getEnvironment()}&_=${Math.random()}`, null, {
    method: "GET",
  })
}

export const getMetrics = (taskId, jobExecutionId) => {
  return request(`/task/execution/${taskId}/metrics`, {
    jobExecutionId,
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "POST",
  })
}

export const killJob = (id) => {
  return request(`/job/execution/${id}/kill`, {
    labels: {
      route: getEnvironment()
    }
  }, {
    method: "POST",
  })
}

// 获取job运行日志
export const getJobExecLog = (params) => {
  return request(
    `/job/execution/${params.id}/log?labels=${getEnvironment()}&_=${Math.random()}`,
    {
      fromLine: params.fromLine || 0,
      pageSize: params.pageSize || 50,
      onlyKeywords: params.onlyKeywords,
      ignoreKeywords: params.ignoreKeywords,
      lastRows: params.lastRows,
      enableTail: true
    },
    {
      method: "GET",
    }
  );
}

// 获取task运行日志
export const getTaskExecLog = (params) => {
  return request(
    `/task/execution/${params.taskId}/log?labels=${getEnvironment()}&_=${Math.random()}`,
    {
      fromLine: params.fromLine || 0,
      pageSize: params.pageSize || 50,
      jobExecutionId: params.id,
      onlyKeywords: params.onlyKeywords,
      ignoreKeywords: params.ignoreKeywords,
      lastRows: params.lastRows,
      enableTail: true
    },
    {
      method: "GET",
    }
  );
}

// 获取分区信息
export const getPartitionInfo = (params) => {
  if (!params.source) return
  const url = params.source.split(BASE_URL)[1]
  return request(
    `${url}?labels=${getEnvironment()}&dataSourceId=${params.dataSourceId}&database=${params.database}&table=${params.table}&tableNotExist=${params.tableNotExist}&_=${Math.random()}`,
    {},
    {
      method: "GET",
    }
  );
}

// 获取字段映射转换函数
export const getFieldFunc = (funcType) => {
  if (!funcType) return
  return request(
    `/job/func/${funcType}?labels=${getEnvironment()}&_=${Math.random()}`,
    {},
    {
      method: "GET",
    }
  );
}

// 获取字段映射转换函数
export const encryptFunc = (param) => {
  return request(
    `/datasources/tools/encrypt?labels=${getEnvironment()}`,
    param,
    {
      method: "POST",
    }
  );
}

// 获取执行用户
export const getExecutor = () => {
  return request(
    `/job/Executor?labels=${getEnvironment()}`,
    {},
    {
      method: "GET",
    }
  )
}

// processor的内容保存
export const saveProcessor = (param) => {
  return request(
    `/job/transform/processor/code_content?labels=${getEnvironment()}`,
    param,
    {
      method: "POST",
    }
  );
}

// processor的内容更新
export const updateProcessor = ({ proc_code_id, ...param }) => {
  return request(
    `/job/transform/processor/code_content/${proc_code_id}?labels=${getEnvironment()}`,
    param,
    {
      method: "PUT",
    }
  );
}

// processor的内容更新
export const getTemplate = () => {
  return request(
    `/job/transform/processor/code_template?labels=${getEnvironment()}`,
    {},
    {
      method: "GET",
    }
  );
}

// processor的内容更新
export const getProcessor = (proc_code_id) => {
  return request(
    `/job/transform/processor/code_content/${proc_code_id}?labels=${getEnvironment()}`,
    {},
    {
      method: "GET",
    }
  );
}

// 获取项目权限
export const getProjectPermission = (projectId) => {
  return request(
    `/getProjectPermission/${projectId}?labels=${getEnvironment()}`,
    {},
    {
      method: "GET",
    }
  );
}