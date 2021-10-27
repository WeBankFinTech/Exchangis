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
        <a-space size="middle">
          <span>数据库</span>
          <a-select
            v-model:value="sqlSource"
            style="width: 120px"
            placeholder="请先选择数据库"
            :options="
              sqlList.map((sql) => ({ value: sql.value, label: sql.name }))
            "
            @change="handleChangeSql"
          >
          </a-select>
          <span>数据源</span>
          <a-select
            placeholder="请先选择数据源"
            @change="handleChangeDS"
            :options="
              dataSourceList.map((ds) => ({ value: ds.value, label: ds.name }))
            "
          ></a-select>
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
import {
  getDataSourceTypes,
  getDBs,
  getTables,
  getDataSource,
} from "@/common/service";
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
      sqlId: "",
      dsId: "",
      curSql: "",
      dataSource: "",
      dataSourceList: [],
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
    // 根据数据类型ID的不同返回不同的数据源列表
    const queryDataSource = async (typeId) => {
      // 这里前端目前不做分页 固定了 page 和 pageSize
      let body = {
        name: "",
        typeId,
        page: 1,
        pageSize: 1000,
      };
      let res = [];
      try {
        let _res = await getDataSource(body);
        const { list } = _res;
        list.forEach((item) => {
          let o = Object.create(null);
          o.name = item.name;
          o.value = item.id;
          res.push(o);
        });
      } catch (err) {
        console.log("err");
      }
      return res;
    };
    // 选择数据库触发
    const handleChangeSql = async (sql) => {
      state.curSql = sql;
      const cur = state.sqlList.filter((i) => i.value === sql)[0];
      state.sqlId = cur.id;
      let dsOptions = await queryDataSource(cur.id);
      state.dataSourceList = dsOptions.length > 0 ? dsOptions : [];
    };
    // 选择数据源触发
    const handleChangeDS = async (ds) => {
      createTree(ds, () => {
        const cur = state.dataSourceList.filter((item) => {
          return item.value === ds;
        })[0];
        state.dataSource = cur.name;
        state.dsId = cur.value;
      });
    };
    // 创建 db & tables tree
    const createTree = async (ds, cb) => {
      if (!ds) return;
      const tree = [];
      // 这里 根据数据源请求 dbs
      const cur = state.dataSourceList.filter((item) => {
        return item.value === ds;
      })[0];
      let dbs = (await getDBs(state.curSql, cur.value)).dbs;
      if (!dbs) return;
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
      state.treeData = tree;
      cb();
    };
    const visible = ref(false);
    const showModal = () => {
      visible.value = true;
    };
    const selectItem = (e) => {
      state.defaultSelect = `${state.sqlSource}-${state.dataSource}-${e.join(
        ""
      )}`;
    };
    const handleOk = () => {
      visible.value = false;
      context.emit("updateDsInfo", state.defaultSelect, state.dsId);
    };
    /**
     * 获取数据库下 所有表
     * @param (sql, dsId, dbName)
     * @returns tbales
     */
    const asyncGetTables = async (sql, dsId, dbName) => {
      let tables;
      const res = [];
      tables = (await getTables(sql, dsId, dbName)).tbs || [];
      tables.forEach((table) => {
        const o = Object.create(null);
        o.title = table;
        o.key = `${dbName}-${table}`;
        res.push(o);
      });
      return res;
    };
    // 展开数据库树获取表叶子
    const handleExpandSql = (expandedKeys, { expanded, node }) => {
      const dbName = node.title;
      state.treeData.forEach(async (td) => {
        if (td.title === dbName) {
          if (td.children.length > 0 && td.children[0].title) return;
          let tables = await asyncGetTables(state.curSql, state.dsId, dbName);
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
      handleChangeDS,
    };
  },
});
</script>

<style lang="less" scoped>
.sds-wrap {
  display: inline-block;
}
</style>
