import { request as ajax, access, getRouter } from "@fesjs/fes";
import PageLoading from "@/components/PageLoading";
import UserCenter from "@/components/UserCenter";
import { message, Modal, ConfigProvider } from "ant-design-vue";
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

export const request = {
  baseURL: BASE_URL,
  responseDataAdaptor: (data) => {
    data.code = String(data.status);
    return data;
  },
};

export const layout = {
  customHeader: <UserCenter />,
};


export function onAppCreated({ app }) {
  loadAllRegister(app);
}
export const locale = {
  locale: {
    locale: "zh-CN", // default locale
    fallbackLocale: "zh-CN", // set fallback locale
    baseNavigator: true, // 开启浏览器语言检测
    share: true, // 用户是否需要手动改变语言
  },
};
