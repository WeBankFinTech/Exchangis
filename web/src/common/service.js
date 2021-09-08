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

export const copyJob = (id, params) => {
  return request(`/job/${id}/copy`, {...params}, {
    method: "POST",
  });
};