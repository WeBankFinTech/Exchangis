
import {
    pum as pumApi, request as ajax, access, getRouter
} from '@fesjs/fes';
import { message, Modal, ConfigProvider } from 'ant-design-vue';
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import PageLoading from '@/components/PageLoading';
import UserCenter from '@/components/UserCenter';
import { BASE_URL } from '@/common/constants';
import { loadAllRegister } from './register';
import { locale } from '@fesjs/fes';

import "ant-design-vue/lib/input/style/css";
import "ant-design-vue/lib/modal/style/css";
import "ant-design-vue/lib/select/style/css";
import "ant-design-vue/lib/form/style/css";
import "ant-design-vue/lib/style";


locale.setLocale({ locale: 'zh-CN' });

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



export function rootContainer(Container) {
    return () => (
        <ConfigProvider locale={zhCN}>
            <Container />
        </ConfigProvider>
    );
}


export const request = {
    baseURL: BASE_URL,
    errorHandler: {
        16462206() {
            Modal.error({
                title: '该任务当前状态不能继续进行标注作业',
                okText: '确定',
                onOk() {
                    const router = getRouter();
                    router.back();
                }
            });
        },
        401() {
            pumApi.redirectToLogin();
        },
        default(error) {
            message.error(error?.response?.data?.msg || '系统异常');
        }
    }
};

export function onAppCreated({ app }) {
    loadAllRegister(app);
}

export const layout = {
    customHeader: <UserCenter />
};