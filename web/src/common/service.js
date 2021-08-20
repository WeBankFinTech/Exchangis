// 服务端接口管理
import { request } from "@fesjs/fes";

export const getProjectList = () => {
  return request("/projects");
};
