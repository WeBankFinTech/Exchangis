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
        redirect: "/project_list",
      },
      {
        path: "/project_list",
        component: "@/pages/project_list",
        meta: {
          name: "item_list",
          title: "项目列表",
        },
      },
      {
        path: "/data_source",
        component: "@/pages/data_source",
        meta: {
          name: "data_source",
          title: "数据源列表",
        },
      },
      {
        path: "/jobManagement",
        component: "@/pages/jobManagement",
        meta: {
          name: "jobManagement",
          title: "数据任务管理",
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
    footer: "Created by MumbelFe",
    multiTabs: false,
    menus: [
      {
        name: "item_list",
      },
      {
        name: "data_source",
      },
      {
        name: "jobManagement",
      },
    ],
  },
  devServer: {
    port: 8000,
  },
  proxy: {
    "/api": {
      target: "http://192.168.0.157:9321/",
      changeOrigin: true,
      pathRewrite: { "^/api": "" },
    },
  },
  enums: {
    status: [
      ["0", "无效的"],
      ["1", "有效的"],
    ],
  },
  locale: {
    locale: "zh-CN", // default locale
    fallbackLocale: "zh-CN", // set fallback locale
    baseNavigator: true, // 开启浏览器语言检测
    share: true, // 用户是否需要手动改变语言
  },
};
