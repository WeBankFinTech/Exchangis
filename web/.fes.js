export default {
  publicPath: "./",
  access: {
    roles: {
      admin: ["*"],
    },
  },
  router: {
    mode: "hash",
    routes: [
      {
        path: "/",
        redirect: "/projectManage",
      },
      {
        path: "/projectManage",
        component: "@/pages/projectManage",
        meta: {
          name: "projectManage",
          title: "globalMenu.projectManage",
        },
      },
      {
        path: "/dataSourceManage",
        component: "@/pages/dataSourceManage",
        meta: {
          name: "dataSourceManage",
          title: "globalMenu.dataSourceManage",
        },
      },
      {
        path: "/jobManagement",
        component: "@/pages/jobManagement",
        meta: {
          name: "jobManagement",
          title: "globalMenu.jobManagement",
        },
      },
    ],
  },
  request: {
    dataField: "data",
  },
  extraBabelPlugins: [["import", { libraryName: "ant-design-vue", libraryDirectory: "es", style: "css" }]],
  layout: {
    title: "",
    footer: "",
    logo: null,
    multiTabs: false,
    menus: [
      {
        name: "projectManage",
      },
      {
        name: "dataSourceManage",
      },
      {
        name: "jobManagement",
      },
    ],
  },
  devServer: {
    host:'0.0.0.0',
    port: 8000,
  },
  proxy: {
    "/api": {
      //target: "http://192.168.0.157:9321/",
      target: "http://127.0.0.1:9321/",
      changeOrigin: true,
      pathRewrite: { "^/api": "/api" },
    },
  },
  locale: {
    locale: "zh-CN", // default locale
    fallbackLocale: "zh-CN", // set fallback locale
    baseNavigator: true, // 开启浏览器语言检测
    share: true, // 用户是否需要手动改变语言
  },
};
