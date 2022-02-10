// 服务端接口管理
import { request } from "@fesjs/fes";
import { BASE_URL } from "@/common/constants";
////////////////////////////////////////////////////////////////////
export const getProjectList = (name) => {
  return request("/projects", { name }, { method: "POST" });
};

export const createProject = (body) => {
  return request("/createProject", body);
};

export const deleteProject = (id) => {
  return request("/projects/" + id, null, {
    method: "DELETE",
  });
};

export const getProjectById = (id) => {
  return request("/projects/" + id, null, {
    method: "GET",
  });
};

export const updateProject = (body) => {
  return request("/updateProject", body, {
    method: "PUT",
  });
};

export const getDataSourceList = (params) => {
  return request("/datasources/query", { ...params }, { method: "POST" });
};

// 数据源管理 获取数据源
export const getDataSourceTypes = () => {
  return request(
    `/datasources/type?t=_${new Date().getTime()}`,
    {},
    { method: "GET" }
  );
};

// 数据源管理 获取动态参数
export const getKeyDefine = (dataSourceTypeId) => {
  return request(
    `/datasources/types/${dataSourceTypeId}/keydefines?t=_${new Date().getTime()}`,
    {},
    { method: "GET" }
  );
};

// 查询数据源
export const getDataSource = (body) => {
  return request("/datasources/query", body, { method: "POST" });
};

export const getDBs = (type, id) => {
  return request(`/datasources/${type}/${id}/dbs`, {}, { method: "GET" });
};

export const getTables = (type, id, dbName) => {
  return request(
    `/datasources/${type}/${id}/dbs/${dbName}/tables`,
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

export const getFields = (params) => {
  return request(
    `/datasources/fieldsmapping`,
    { ...params },
    { method: "POST" }
  );
};

export const createDataSource = (params) => {
  return request("/datasources", { ...params }, { method: "POST" });
};

export const updateDataSource = (id, params) => {
  return request("/datasources/" + id, { ...params }, { method: "PUT" });
};

export const deleteDataSource = (id) => {
  return request(`/datasources/${id}`, {}, { method: "DELETE" });
};

export const getDataSourceVersionList = (id) => {
  return request(`/datasources/${id}/versions`, {}, { method: "GET" });
};

export const testDataSourceConnect = (type, id) => {
  return request(`/datasources/${type}/${id}/connect`, {}, { method: "PUT" });
};

export const getDataSourceById = (id, versionId) => {
  return request(`/datasources/${id}`, {versionId}, { method: "GET" });
};

export const getJobInfo = (id) => {
  return request(`/job/${id}`, null, {
    method: "GET",
  });
};

//获取任务列表
export const getJobList = (query) => {
  return request(`/job?${query}`, null, {
    method: "GET",
  });
};

//获取执行引擎列表
export const getEngineType = () => {
  return request(`/job/engineType`, null, {
    method: "GET",
  });
};

//新建任务
export const createJob = (params) => {
  return request(
    `/job`,
    { ...params },
    {
      method: "POST",
    }
  );
};

//复制任务
export const copyJob = (id, params) => {
  return request(
    `/job/${id}/copy`,
    { ...params },
    {
      method: "POST",
    }
  );
};

//删除任务
export const deleteJob = (id) => {
  return request(`/job/${id}`, null, {
    method: "DELETE",
  });
};

//导入任务
export const importJob = (id, params) => {
  return request(
    `/job/import`,
    { ...params },
    {
      method: "POST",
    }
  );
};

//执行任务
export const executeTask = (id) => {
  return request(`/job/${id}/action/execute`, null, {
    method: "POST",
  });
};

export const getJobs = (id, jobType) => {
  return request(`/job?projectId=${id}&jobType=${jobType}`, null, {
    method: "GET",
  });
};

export const saveProject = (id, body) => {
  return request(`/job/${id}/content`, body, {
    method: "PUT",
  });
};

// 保存/更新任务配置
export const updateTaskConfiguration = (id, body) => {
  return request(`/job/${id}/config`, body, {
    method: "PUT",
  });
};

export const expireDataSource = (id) => {
  return request(`/datasources/${id}/expire`, {}, { method: "PUT" });
};

export const publishDataSource = (id, versionId) => {
  return request(
    `/datasources/${id}/${versionId}/publish`,
    {},
    { method: "PUT" }
  );
};

export const getSourceParams = (engineType, type, ds) => {
  return request(
    `/datasources/${engineType}/${type}/params/ui?dir=${ds}`,
    {},
    { method: "GET" }
  );
};

export const getSettingsParams = (engineType) => {
  return request(
    `/jobs/engine/${engineType}/settings/ui`,
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
  return request("/tasks", body, {
    method: "GET",
  });
};
// 新版同步历史-获取job列表
export const getSyncHistoryJobList = (body) => {
  return request("/job/listJobs", body, {
    method: "GET",
  });
};
// 删除同步历史
export const delSyncHistory = (jobExecutionId) => {
  return request(`/job/${jobExecutionId}/deleteJob`, {}, {
    method: "POST",
  });
};
// 读取Task限速配置
export const getSpeedLimit = (params) => {
  return request(
    `/job/${params.jobId}/speedlimit/${params.taskName}/params/ui`,
    {},
    {
      method: "GET",
    }
  );
};
// 保存Task限速配置
export const saveSpeedLimit = (params, body) => {
  return request(`/job/${params.jobId}/speedlimit/${params.taskName}`, body, {
    method: "PUT",
  });
};

// 获取运行日志
export const getLogs = (params) => {
  return request(
    `/execution/tasks/${params.taskID}/logs`,
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
  return request("/metrics/taskstate", {}, { method: "GET" });
};

// 任务进度
export const getTaskProcess = () => {
  return request("/metrics/taskprocess", {}, { method: "GET" });
};

// 流量监控
export const getDataSourceFlow = () => {
  return request("/metrics/datasourceflow", {}, { method: "GET" });
};

// 资源使用
export const getEngineriesSource = () => {
  return request("/metrics/engineresource", {}, { method: "GET" });
};

export const getEngineriesSourceCpu = () => {
  return request("/metrics/engineresourcecpu", {}, { method: "GET" });
};

export const getEngineriesSourceMem = () => {
  return request("/metrics/engineresourcemem", {}, { method: "GET" });
};


/* 作业执行模块接口 */
export const executeJob = (id) => {
  return request(`/job/${id}/execute`,undefined, {
    method: "POST",
  })
}

export const getJobStatus = (id) => {
  return request(`/job/execution/${id}/status`, {}, {
    method: "GET",
  })
}

export const getJobTasks = (id) => {
  return request(`/job/execution/${id}/taskList`, null, {
    method: "GET",
  })
}

export const getProgress = (id) => {
  return request(`/job/execution/${id}/progress?_=${Math.random()}`, null, {
    method: "GET",
  })
}

export const getMetrics = (taskId, jobExecutionId) => {
  return request(`/task/execution/${taskId}/metrics`, {jobExecutionId}, {
    method: "POST",
  })
}

export const killJob = (id) => {
  return request(`/job/execution/${id}/kill`, null, {
    method: "POST",
  })
}

// 获取job运行日志
export const getJobExecLog = (params) => {
  return request(
    `/job/execution/${params.id}/log?_=${Math.random()}`,
    {
      fromLine: params.fromLine || 0,
      pageSize: params.pageSize || 100,
      onlyKeywords: params.onlyKeywords,
      ignoreKeywords: params.ignoreKeywords,
      lastRows: params.lastRows
    },
    {
      method: "GET",
    }
  );
}

// 获取task运行日志
export const getTaskExecLog = (params) => {
  return request(
    `/task/execution/${params.taskId}/log?_=${Math.random()}`,
    {
      fromLine: params.fromLine || 0,
      pageSize: params.pageSize || 100,
      jobExecutionId: params.id,
      onlyKeywords: params.onlyKeywords,
      ignoreKeywords: params.ignoreKeywords,
      lastRows: params.lastRows
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
    `${url}?dataSourceId=${params.dataSourceId}&database=${params.database}&table=${params.table}&_=${Math.random()}`,
    {},
    {
      method: "GET",
    }
  );
}