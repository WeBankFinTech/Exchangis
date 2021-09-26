// 服务端接口管理
import { request } from "@fesjs/fes";
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
////////////////////////////////////////////////////////////////////
export const getDataSourceList = (params) => {
  return request("/datasources/query", { ...params }, { method: "POST" });
};

export const getDataSourceTypes = () => {
  return request("/datasources/type", {}, { method: "GET" });
};

export const getDBs = (type, id) => {
  return request(`/datasources/${type}/${id}/dbs`, {}, { method: "GET" });
}

export const getTables = (type, id, dbName) => {
  return request(`/datasources/${type}/${id}/dbs/${dbName}/tables`, {}, { method: "GET" });
}


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

export const getDataSourceById = (id) => {
  return request(`/datasources/${id}`, {}, { method: "GET" });
};

export const getJobInfo = (id) => {
  return request(`/job/${id}`, null, {
    method: "GET",
  });
};

export const getJobs = (id, jobType) => {
  return request(`/job?projectId=${id}&jobType=${jobType}`, null, {
    method: "GET",
  });
};

export const createJob = (params) => {
  return request("/job", { ...params }, { method: "POST" });
};

export const getEngineType = () => {
  return request(`/job/engineType`, null, {
    method: "GET",
  });
};

export const saveProject = (id, body) => {
  return request(`/job/${id}/content`, body, {
    method: "PUT",
  });
};

export const expireDataSource = (id) => {
  return request(`/datasources/${id}/expire`, {}, { method: "PUT" });
};

export const publishDataSource = (id, versionId) => {
  return request(`/datasources/${id}/${versionId}/publish`, {}, { method: "PUT" });
};
