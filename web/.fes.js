// .fes.js 只负责管理编译时配置，只能使用plain Object

export default {
  publicPath: "./",
  access: {
    roles: {
      admin: ["/", "/onepiece", "/jobManagement"],
    },
  },
  request: {
    dataField: "result",
  },
  extraBabelPlugins: [
    [
      "import",
      { libraryName: "ant-design-vue", libraryDirectory: "es", style: "css" },
    ],
  ],
  layout: {
    title: "Fes.js",
    footer: "Created by MumbelFe",
    multiTabs: false,
    menus: [
      {
        name: "index",
      },
      {
        name: "index",
      },
      {
        name: "job",
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
