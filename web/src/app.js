<<<<<<< Updated upstream


import { access } from '@fesjs/fes';
import PageLoading from '@/components/PageLoading';
import UserCenter from '@/components/UserCenter';

export const beforeRender = {
    loading: <PageLoading />,
    action() {
        const { setRole } = access;
        return new Promise((resolve) => {
            setTimeout(() => {
                setRole('admin');
                // 初始化应用的全局状态，可以通过 useModel('@@initialState') 获取，具体用法看@/components/UserCenter 文件
                resolve({
                    userName: 'harrywan'
                });
            }, 1000);
        });
    }
};

export const layout = {
    customHeader: <UserCenter />
};
=======
import { access } from "@fesjs/fes";
import PageLoading from "@/components/PageLoading";
import UserCenter from "@/components/UserCenter";
import "ant-design-vue/dist/antd.css"; // or 'ant-design-vue/dist/antd.less'
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
  dataField: "result",
};
export const layout = {
  customHeader: <UserCenter />,
};

export function onAppCreated({ app }) {}
>>>>>>> Stashed changes
