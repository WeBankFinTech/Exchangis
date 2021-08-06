// .fes.js 只负责管理编译时配置，只能使用plain Object

<<<<<<< Updated upstream

export default {
    publicPath: './',
    access: {
        roles: {
            admin: ["/", "/onepiece"]
        }
    },
    layout: {
        title: "Fes.js",
        footer: 'Created by MumbelFe',
        multiTabs: false,
        menus: [{
            name: 'index'
        }]
    },
    devServer: {
        port: 8000
    },
    enums: {
        status: [['0', '无效的'], ['1', '有效的']]
    }
=======
export default {
  publicPath: "./",
  access: {
    roles: {
      admin: ["/", "/onepiece", "/item_list", "/data_source"],
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
>>>>>>> Stashed changes
};
