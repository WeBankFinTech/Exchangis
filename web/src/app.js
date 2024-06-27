/*
 * @Description: 
 * @Author: sueRim
 * @Date: 2022-05-13 10:19:27
 */
import { pum as pumApi, request as ajax, access as accessInstance, getRouter } from "@fesjs/fes";
import { message, Modal, ConfigProvider } from "ant-design-vue";
import zhCN from "ant-design-vue/es/locale/zh_CN";
import PageLoading from "@/components/PageLoading";
import UserCenter from "@/components/UserCenter";
import { BASE_URL } from "@/common/constants";
import { loadAllRegister } from "./register";

export const beforeRender = {
  loading: <PageLoading />,
  action() {
    const { setRole } = accessInstance;
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

// 自定义 axios
export const request = {
  baseURL: BASE_URL,
  responseDataAdaptor: (data) => {
    data.code = String(data.status);
    return data;
  },
  errorHandler: {
    default(error) {
      if (error.message && error.message.indexOf('timeout of') > -1) {
        return message.warning('请求超时')
      }
      if (error?.response?.data?.data?.errorMsg) {
        return message.error(error.response.data.data.errorMsg.desc);
      }
      console.log(error, error?.response)
      if (error?.type === 'REPEAT') return // 重复请求不进行提示
      message.error(error?.response?.data?.message || error?.data?.message ||  "系统异常");
    },
  },
  timeout: 30000
};

// 这里 自定义注册header
export const layout = {
  customHeader: <UserCenter />,
};

export function onAppCreated({ app }) {
  loadAllRegister(app);
}

const localStr = localStorage.getItem('fes_locale')
if (localStr !== 'zh-CN') {
  localStorage.setItem('fes_locale', 'zh-CN')
  document.location = '/'
}


window.addEventListener('beforeunload', function () {
  if (localStorage.getItem('exchangis_environment')) {
    localStorage.removeItem('exchangis_environment');
  }
});