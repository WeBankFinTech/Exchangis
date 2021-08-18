// .fes.js 只负责管理编译时配置，只能使用plain Object

export default {
  publicPath: "./",
  access: {
    roles: {
      admin: ["/", "/onepiece", "/item_list", "/data_source", "/jobManagement"],
    },
  },
  router: {
    mode: "hash",
    routes: [
      {
        path: "/",
        redirect: "/item_list",
      },
      {
        path: "/item_list",
        component: "@/pages/item_list",
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
  mock: true,
  layout: {
    title: "数据交换",
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
