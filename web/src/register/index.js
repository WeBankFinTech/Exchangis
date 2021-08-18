/**
 * @description 加载所有 Plugins
 * @param  {ReturnType<typeofcreateApp>} app 整个应用的实例
 */
export function loadAllRegister(app) {
    const files = require.context('.', true, /\.js$/);
    files.keys().forEach((key) => {
        if (typeof files(key).default === 'function') {
            if (key !== './index.ts') files(key).default(app);
        }
    });
}
