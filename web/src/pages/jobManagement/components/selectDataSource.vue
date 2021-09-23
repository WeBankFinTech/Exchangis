<template>
  <div class="sds-wrap">
    <a-button type="primary" @click="showModal">{{ defaultSelect }}</a-button>
    <a-modal
      v-model:visible="visible"
      title="选择数据源"
      @ok="handleOk"
      width="500px"
    >
      <!-- top -->
      <div class="sds-wrap-t">
        <a-space>
          <!-- <a-select
            v-model:value="dataBase"
            style="width: 120px"
            :options="dataBaseTypes.map((db) => ({ value: db }))"
          >
          </a-select>
          <a-select
            v-model:value="colony"
            style="width: 120px"
            :options="colonys.map((colony) => ({ value: colony }))"
          >
          </a-select> -->
          <a-select
            v-model:value="sqlSource"
            style="width: 120px"
            :options="sqlList.map((sqlSource) => ({ value: sqlSource }))"
            @change="handleChangeSql"
          >
          </a-select>
        </a-space>
      </div>
      <!-- bottom 类似tree组件 -->
      <div class="sds-wrap-b">
        <a-directory-tree
          :tree-data="treeData"
          v-model:expandedKeys="expandedKeys"
          v-model:selectedKeys="selectedKeys"
          @select="selectItem"
        ></a-directory-tree>
      </div>
    </a-modal>
  </div>
</template>

<script>
//import { SQLlist, dbs, tables } from "../mock";
import { getDataSourceTypes } from "@/common/service"
// const dataBaseTypes = ["Hive", "HBase"];
// const colonyData = {
//   Hive: ["A集群", "B集群", "C集群"],
//   HBase: ["D集群", "E集群", "F集群"],
// };
// const tables = {
//   Hive: [
//     {
//       title: "default",
//       key: "default",
//       selectable: false,
//       children: [
//         { title: "a1", key: "default-a1" },
//         { title: "test_table", key: "default-test_table" },
//       ],
//     },
//     { title: "db_test_mask", selectable: false, key: "db_test_mask" },
//     { title: "db_test_mask1", selectable: false, key: "db_test_mask1" },
//     { title: "db_test_mask2", selectable: false, key: "db_test_mask2" },
//   ],
//   MYSQL: [
//     {
//       title: "default2",
//       key: "default2",
//       children: [
//         { title: "a2", key: "a2" },
//         { title: "test_table2", key: "test_table2" },
//       ],
//     },
//     { title: "db_test_mask3", key: "db_test_mask3" },
//     { title: "db_test_mask4", key: "db_test_mask4" },
//     { title: "db_test_mask5", key: "db_test_mask5" },
//   ],
// };
import {
  defineComponent,
  reactive,
  toRefs,
  computed,
  watch,
  ref,
  toRaw,
  onMounted
} from "vue";

export default defineComponent({
  props: {
    title: String,
  },
  emits: ["updateDsInfo"],
  setup(props, context) {
    let SQLlist, dbs, tables
    async function init () {
      dbs = (await getDataSourceTypes()).list
    }
    init()
    console.log(123)
    // 数据源
    const sqlList = [];
    SQLlist.forEach((sql) => {
      sqlList.push(sql.name);
    });
    const handleChangeSql = (sql) => {
      const tree = createTree(sql, dbs, tables);
      console.log(tree);
      state.treeData = tree;
    };
    const state = reactive({
      sqlSource: sqlList[0],
      sqlList,
      defaultSelect: props.title,
      treeData,
    });
    // 创建 db & tables tree
    const createTree = (sql, dbs, tables) => {
      console.log(sql);
      const tree = [];
      // 这里 根据数据源请求 dbs
      dbs.forEach((db) => {
        const o = Object.create(null);
        o.selectable = false;
        o.title = db;
        o.key = db;
        o.children = [];

        // 这里 根据数据源和db请求 tables
        tables.forEach((table) => {
          const oo = Object.create(null);
          oo.title = table;
          oo.key = `${db}-${table}`;
          o.children.push(oo);
        });

        tree.push(o);
      });
      return tree;
    };
    const visible = ref(false);
    const showModal = () => {
      visible.value = true;
    };
    const treeData = createTree(state.sqlSource, dbs, tables);
    const expandedKeys = ref([]);
    const selectedKeys = ref();
    const selectItem = (e) => {
      let _defaultSelect = `${sqlSource}-${e.join("")}`;
      state.defaultSelect = _defaultSelect;
    };
    const handleOk = () => {
      visible.value = false;
      context.emit("updateDsInfo", state.defaultSelect);
    };
    return {
      ...toRefs(state),
      expandedKeys,
      selectedKeys,
      selectItem,
      visible,
      showModal,
      handleOk,
      sqlList,
      handleChangeSql,
    };
  },
  watch: {
    title: {
      handler: function (newVal) {
        console.log("watch props");
        this.props = newVal;
      },
      deep: true,
    },
  },
});
</script>

<style lang="less" scoped>
.sds-wrap {
  display: inline-block;
}
</style>
