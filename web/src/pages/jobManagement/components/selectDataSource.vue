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
            :options="
              sqlList.map((sql) => ({ value: sql.value, label: sql.name }))
            "
            @change="handleChangeSql"
          >
          </a-select>
        </a-space>
      </div>
      <!-- bottom 类似tree组件 -->
      <div class="sds-wrap-b">
        <a-directory-tree
          :tree-data="treeData"
          @select="selectItem"
          @expand="handleExpandSql"
        ></a-directory-tree>
      </div>
    </a-modal>
  </div>
</template>

<script>
//import { SQLlist, dbs, tables } from "../mock";
import { getDataSourceTypes, getDBs, getTables } from "@/common/service";
import {
  defineComponent,
  reactive,
  toRefs,
  computed,
  watch,
  ref,
  toRaw,
  onMounted,
} from "vue";

export default defineComponent({
  props: {
    title: String,
  },
  emits: ["updateDsInfo"],
  setup(props, context) {
    let SQLlist, treeData;
    const sqlList = [];
    const state = reactive({
      sqlSource: sqlList.length ? sqlList[0].value : "",
      sqlList,
      defaultSelect: props.title,
      treeData,
      id: "",
      curSql: "",
    });
    const newProps = computed(() => JSON.parse(JSON.stringify(props.title)));
    watch(newProps, (val, oldVal) => {
      state.defaultSelect = val;
    });
    async function init() {
      SQLlist = (await getDataSourceTypes()).list;
      // 数据源
      SQLlist.forEach((sql) => {
        sqlList.push({
          name: sql.option,
          value: sql.name,
          id: sql.id,
        });
      });
      if (state.sqlSource) await createTree(state.sqlSource);
    }
    onMounted(init());
    const handleChangeSql = async (sql) => {
      state.curSql = sql;
      createTree(sql, () => {
        state.treeData = treeData;
        const cur = sqlList.filter((item) => {
          return item.value === sql;
        })[0];
        state.id = cur.id;
      });
    };
    // 创建 db & tables tree
    const createTree = async (sql, cb) => {
      if (!sql) return;
      const tree = [];
      // 这里 根据数据源请求 dbs
      const cur = sqlList.filter((item) => {
        return item.value === sql;
      })[0];
      let dbs = (await getDBs(cur.value, cur.id)).dbs;
      // let promises = [];
      // dbs.forEach((db, index) => {
      //   const o = Object.create(null);
      //   o.selectable = false;
      //   o.title = db;
      //   o.key = db;
      //   o.children = [];
      //   promises.push(
      //     new Promise((resolve, reject) => {
      //       getTables(cur.value, cur.id, db).then((res) => {
      //         let tables = res.tbs || [];
      //         tables.forEach((table) => {
      //           const oo = Object.create(null);
      //           oo.title = table;
      //           oo.key = `${db}-${table}`;
      //           o.children.push(oo);
      //         });
      //         tree.push(o);
      //         resolve();
      //       });
      //     })
      //   );
      // });
      // Promise.all(promises)
      //   .then((result) => {
      //     treeData = tree;
      //     cb && cb();
      //   })
      //   .catch((error) => {
      //     console.log(error);
      //     treeData = [];
      //     cb && cb();
      //   });
      dbs.forEach((db, index) => {
        const o = Object.create(null);
        o.selectable = false;
        o.title = db;
        o.key = db;
        o.children = [];
        const oo = Object.create(null);
        (oo.key = ""), (oo.title = ""), o.children.push(oo);
        tree.push(o);
      });

      treeData = tree;
      cb();
    };
    const visible = ref(false);
    const showModal = () => {
      visible.value = true;
    };
    const selectItem = (e) => {
      state.defaultSelect = `${state.sqlSource}-${e.join("")}`;
    };
    const handleOk = () => {
      visible.value = false;
      context.emit("updateDsInfo", state.defaultSelect, state.id);
    };
    /**
     * 获取数据库下 所有表
     * @param (sql, dbName)
     * @returns tbales
     */
    const asyncGetTables = async (sql, dbName) => {
      let tables;
      const res = [];
      const cur = sqlList.filter((item) => {
        return item.value === sql;
      })[0];
      tables = (await getTables(cur.value, cur.id, dbName)).tbs || [];
      tables.forEach((table) => {
        const o = Object.create(null);
        o.title = table;
        o.key = `${dbName}-${table}`;
        res.push(o);
      });
      return res;
    };
    const handleExpandSql = (expandedKeys, { expanded, node }) => {
      const dbName = node.title;
      state.treeData.forEach(async (td) => {
        if (td.title === dbName) {
          if (td.children.length > 0 && td.children[0].title) return;
          let tables = await asyncGetTables(state.curSql, dbName);
          return (td.children = tables.slice());
        }
      });
    };
    return {
      ...toRefs(state),
      selectItem,
      visible,
      showModal,
      handleOk,
      sqlList,
      handleChangeSql,
      handleExpandSql,
    };
  },
});
</script>

<style lang="less" scoped>
.sds-wrap {
  display: inline-block;
}
</style>
