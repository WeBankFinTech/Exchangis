import { pum as pumApi, request as ajax, access, getRouter } from "@fesjs/fes";
import { message, Modal, ConfigProvider } from "ant-design-vue";
import zhCN from "ant-design-vue/es/locale/zh_CN";
import PageLoading from "@/components/PageLoading";
import UserCenter from "@/components/UserCenter";
import { BASE_URL } from "@/common/constants";
import { loadAllRegister } from "./register";

export const beforeRender = {
  loading: <PageLoading />,
  action() {
    const { setRole } = access;
    return new Promise((resolve) => {
      setTimeout(() => {
        setRole("admin");
        // 初始化应用的全局状态，可以通过 useModel('@@initialState') 获取，具体用法看@/components/UserCenter 文件
        resolve({
          userName: "harrywan",
        });
      }, 1000);
    });
  },
};

export function rootContainer(Container) {
  return () => (
    <ConfigProvider locale={zhCN}>
      <Container />
    </ConfigProvider>
  );
}

export const request = {
  baseURL: BASE_URL,
  responseDataAdaptor: (data) => {
    data.code = String(data.status);
    return data;
  },
  errorHandler: {
    default(error) {
      message.error(error?.response?.data?.message || "系统异常");
    },
  },
};

export const layout = {
  customHeader: <UserCenter />,
};

export function onAppCreated({ app }) {
  loadAllRegister(app);
}
