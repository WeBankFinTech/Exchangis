// 服务端接口管理
import { request } from "@fesjs/fes";

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
