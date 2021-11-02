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
      {
        path: "/synchronizationHistory",
        component: "@/pages/synchronizationHistory",
        meta: {
          name: "synchronizationHistory",
          title: "globalMenu.synchronizationHistory",
        },
      },
      {
        path: "/homePage",
        component: "@/pages/homePage",
        meta: {
          name: "homePage",
          title: "globalMenu.homePage",
        },
      },
    ],
  },
  request: {
    dataField: "data",
  },
  extraBabelPlugins: [
    [
      "import",
      { libraryName: "ant-design-vue", libraryDirectory: "es", style: "css" },
    ],
  ],
  layout: {
    title: "",
    footer: "",
    logo: null,
    multiTabs: false,
    menus: [
      {
        name: "homePage",
      },
      {
        name: "projectManage",
      },
      {
        name: "dataSourceManage",
      },
      {
        name: "synchronizationHistory",
      },
    ],
  },
  devServer: {
    host: "0.0.0.0",
    port: 8000,
  },
  proxy: {
    "/api": {
      //target: "http://192.168.0.157:9321/",
      //target: "http://172.24.8.51:9321/",
      target: "http://121.36.12.247:8088",
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
