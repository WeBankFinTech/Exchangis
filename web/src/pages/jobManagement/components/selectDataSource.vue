<template>
  <div class="sds-wrap">
    <!-- <a-button type="dashed" @click="showModal">{{ defaultSelect }}</a-button> -->
    <div class="sds-button" @click="showModal" v-if="defaultSelect === '请点击后选择'">
      <PlusOutlined style="color: rgba(0, 0, 0, 0.65); font-size: 12px; margin-right: 8px" />
      <span>{{ defaultSelect }}</span>
    </div>
    <a-card hoverable v-else style="width: 400px;">
      <div class="sds-title-tag" v-for="(item, idx) in defaultSelect" :key="item + idx"
        @click="showModal" :title="item">
        <span v-if="idx===0" class="logo" :style="getBg()"></span>
        {{ item || '- -' }}
      </div>
    </a-card>
    <a-modal v-model:visible="visible" title="选择数据源" @ok="handleOk" width="500px">
      <!-- top -->
      <div class="sds-wrap-t">
        <a-space size="middle">
          <span>数据类型</span>
          <a-select v-model:value="curSql" style="width: 150px" placeholder="请先选择数据库"
            :options="sqlList.map((sql) => ({ value: sql.value, label: sql.name }))"
            @change="handleChangeSql">
          </a-select>
          <span>数据源</span>
          <a-select
              style="width: 150px"
              v-model:value="dataSource"
              showSearch
              :filter-option="false"
              :options="dataSourceList.map(ds => ({value: ds.value, label: ds.name}))"
              @popupScroll="handleScrollDS"
              @search="handleSearchDS"
              @change="handleChangeDS">
          </a-select>
        </a-space>
        <a-space size="middle" v-if="dataSource && showDbSearch">
          <span>搜索库</span>
          <a-input placeholder="按回车搜库" style="width: 300px;margin-top: 10px;margin-left:14px"
            v-model:value="searchDB" @keyup.enter="filterDatabase(dsId)">
          </a-input>
        </a-space>
        <a-space size="middle" v-if="dataSource && showTableSearch && currentDbValue">
          <span>搜索表</span>
          <a-input placeholder="按回车搜表" style="width: 300px;margin-top: 10px;margin-left:14px"
            v-model:value="searchWord" @keyup.enter="filterTree(dsId)"></a-input>
        </a-space>
      </div>
      <!-- bottom 类似tree组件 -->
      <div class="sds-wrap-b">
        <a-spin :spinning="spinning" style="
          text-align: center;
          width: 100%;
          height: 400px;
          padding-top: 150px;" />
        <a-directory-tree :height="400" :tree-data="treeData" :autoExpandParent="false"
          :icon="getIcon" v-model:expandedKeys="expandedKeys" @select="selectItem"
          @expand="handleExpandSql">
        </a-directory-tree>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { useRoute } from 'vue-router';
import { cloneDeep, debounce } from "lodash-es";
import { PlusOutlined, MinusOutlined } from '@ant-design/icons-vue';
import {
  getDataSourceTypes,
  getDBs,
  getTables,
  getDataSource,
} from '@/common/service';
import {
  defineComponent,
  reactive,
  toRefs,
  computed,
  watch,
  ref,
  toRaw,
  onMounted,
} from 'vue';
import { message } from 'ant-design-vue';

export default defineComponent({
  props: {
    title: [Object, String],
    engineType: String,
    direct: String,
    sourceType: String,
    projectId: String
  },
  components: {
    PlusOutlined,
    MinusOutlined,
  },
  emits: ['updateDsInfo'],
  setup(props, context) {
    let SQLlist, treeData, originTreeData;
    const sqlList = [];
    const expandedKeys = ref([]);
    const state = reactive({
      sqlSource: sqlList.length ? sqlList[0].value : '',
      sqlList,
      defaultSelect: props.title,
      treeData,
      originTreeData,
      sqlId: '',
      dsId: '',
      curSql: '',
      dataSource: '',
      selectTable: '',
      searchWord: '',
      searchDB: '',
      authDbs: [],
      authTbls: [],
      tableNotExist: false,
      currentPage: 1,
      currentDSWord: '',
      total: 0,
      dataSourceItem: null
    });
    const dataSources = ref([]);
    const dataSourceList = computed(() => {
      let keyWord = 'readAble';
      if (props.direct === 'sink') {
        keyWord = 'writeAble';
      }
      return dataSources.value.filter((v) => v[keyWord]);
    });
    const currentDbValue = ref('');
    let spinning = ref(false);
    const newProps = computed(() => JSON.parse(JSON.stringify(props.title)));
    watch(newProps, (val, oldVal) => {
      state.defaultSelect = val;
    });
    // 获取数据类型接口
    async function init(param) {
      sqlList.length = 0
      SQLlist = (await getDataSourceTypes(param)).list;
      // 数据源
      SQLlist.forEach((sql) => {
        sqlList.push({
          name: `${sql.name}(${sql.struct_classifier})`,
          value: sql.name,
          id: sql.id,
        });
      });
      if (state.sqlSource) await createTree(state.sqlSource);
    }
    const route = useRoute();
    // 根据数据类型ID的不同返回不同的数据源列表
    const queryDataSource = async (name = '') => {
      let body = {
        name,
        typeId: state.sqlId,
        page: state.currentPage,
        pageSize: 100,
        projectId: props.projectId
      };
      let res = [];
      let _total = 0;
      try {
        let _res = await getDataSource(body);
        const { list, total } = _res;
        list.forEach((item) => {
            let o = Object.create(null);
            o.name = item.name;
            o.value = item.id;
            res.push({ ...o, ...item });
          });
        _total = total;
      } catch (err) {
        console.log('err');
      }
      dataSources.value = [...dataSources.value, ...res];
      state.total = _total;
    };

    // 实时搜索数据源
    const handleSearchDS = debounce((name) => {
      state.currentPage = 1;
      state.currentDSWord = name;
      dataSources.value = [];
      queryDataSource(name);
    }, 300);

    const handleScrollDS = (e) => {
      const { target } = e;
      const { scrollTop, scrollHeight, offsetHeight } = target;
      if(scrollTop + 2 + offsetHeight >= scrollHeight && state.total > dataSources.value.length) {
        ++state.currentPage;
        queryDataSource(state.currentDSWord);
      }
    }

    const showTableSearch = ref(false);
    const showDbSearch = ref(false);

    // 选择数据库触发
    const handleChangeSql = async (sql) => {
      state.curSql = sql;
      const cur = state.sqlList.filter((i) => i.value === sql)[0];
      state.sqlId = cur.id;
      state.total = 0;
      state.currentPage = 1;
      state.currentDSWord = '';
      dataSources.value = [];
      await queryDataSource();
      // 清空
      state.dataSourceItem = null;
      state.dataSource = '';
      state.dsId = '';
      state.searchWord = '';
      state.searchDB = '';
      state.treeData = [];
      state.originTreeData = [];
      state.tableNotExist = false;
      showTableSearch.value = false;
      showDbSearch.value = false;
      currentDbValue.value = '';
    };
    // 选择数据源触发
    const handleChangeDS = async (ds, dbName = '') => {
      // 清空
      state.searchDB = '';
      currentDbValue.value = "";
      if (dbName.toString() !== '[object Object]') {
        // 避免select组件传入默认参数
        state.searchDB = dbName;
        currentDbValue.value = dbName;
      }
      state.searchWord = '';
      showTableSearch.value = false;
      showDbSearch.value = false;
      createTree(ds, () => {
        const cur = dataSourceList.value.filter((item) => {
          return item.value === ds;
        })[0];
        state.dataSourceItem = cur;
        state.dataSource = cur.name;
        state.dsId = cur.value;
        state.authDbs = (cur.authDbs || '').split(',').filter((v) => v);
        state.authTbls = (cur.authTbls || '').split(',').filter((v) => v);
      });
    };
    // 创建 db & tables tree
    const createTree = async (ds, cb) => {
      if (!ds) return;
      spinning.value = true;
      const tree = [];
      // 这里 根据数据源请求 dbs
      const cur = dataSourceList.value.filter((item) => {
        return item.value === ds;
      })[0];
      let dbs;
      try {
        dbs = (await getDBs(state.curSql, cur.value)).dbs;
        showDbSearch.value = true;
      } catch (e) {
        dbs = [];
        showDbSearch.value = false;
        console.log(e);
      }
      if (!dbs) return;
      if (state.searchDB) {
        dbs = dbs.filter((db) => {
          return new RegExp(state.searchDB).test(db);
        });
        if (dbs && dbs.length) {
          showTableSearch.value = true;
        } else {
          showTableSearch.value = false;
        }
      }
      dbs.forEach((db, index) => {
        const o = Object.create(null);
        o.selectable = false;
        o.title = db;
        o.key = db;
        o.children = [];
        // const oo = Object.create(null);
        // (oo.key = ""), (oo.title = ""), o.children.push(oo);
        tree.push(o);
      });
      state.treeData = tree;
      state.originTreeData = [].concat(tree); // 必须保存一份原始数据，用于过滤库表
      spinning.value = false;
      cb && cb();
    };

    const filterDatabase = (dsId) => {
      showTableSearch.value = false;
      state.searchWord = '';
      createTree(dsId)
    }

    // filter tree
    const filterTree = (ds) => {
      if (!currentDbValue.value) {
        return message.error('请先选择库');
      }
      state.searchDB = currentDbValue.value;
      // 直接从originTreeData对库和表进行过滤
      const tree = toRaw(state.originTreeData);
      if (state.searchWord) {
        state.treeData = tree.map((i) => {
          const filterChildren = i.children.filter((c) =>
            new RegExp(state.searchWord, 'i').test(c.title)
          );
          if(!filterChildren.some(item => item.title === state.searchWord) && i.key === currentDbValue.value) {
            filterChildren.unshift({
              title: `${state.searchWord}(新增)`,
              key: `${currentDbValue.value}.${state.searchWord}`,
              isLeaf: true
            })
          }
          return {
            ...i,
            children: filterChildren,
          };
        });
      } else {
        state.treeData = cloneDeep(tree);
      }
    };
    const visible = ref(false);
    // 重置数据
    const resetData = () => {
      state.sqlId = '';
      state.dsId = '';
      state.curSql = '';
      state.dataSource = '';
      state.dataSourceItem = null;
      state.selectTable = '';
      state.treeData = [];
      state.originTreeData = [];
      state.tableNotExist = false;
      expandedKeys.value = [];
      state.total = 0;
      state.currentPage = 1;
      state.currentDSWord = '';
      dataSources.value = [];
    };
    // 展示弹窗
    const showModal = async () => {
      let param = {
        engineType: props.engineType,
        direct: props.direct,
      };
      if (props.direct === 'sink') {
        param.sourceType = props.sourceType;
      }
      await init(param);
      resetData(); // 先重置数据
      let selects =
        state.defaultSelect === '请点击后选择' ? [] : state.defaultSelect;
      if (selects[0]) {
        await handleChangeSql(selects[0]);
      }
      if (selects[1]) {
        const cur = dataSourceList.value.filter((item) => {
          return item.name === selects[1];
        })[0];
        await handleChangeDS(cur?.value, selects[2]);
      }
      visible.value = true;
    };
    const selectItem = (selectedKeys, e) => {
      const [authDb, authTbl] = selectedKeys.join('').split('.');
      let bool1 = state.authDbs.length && !state.authDbs.includes(authDb); // 无权限的情况
      let bool2 = state.authTbls.length && !state.authTbls.includes(authTbl); // 无权限的情况
      if (bool1 || bool2) {
        return message.error('无权限');
      }
      state.selectTable = selectedKeys.join('');
      state.tableNotExist = (e.node.title || '').includes('(新增)'); // 是否自定义库表
    };
    const handleOk = () => {
      if (!state.curSql) {
        return message.error('未正确选择数据类型');
      }
      if (!state.dataSource) {
        return message.error('未正确选择数据源');
      }
      if (!state.selectTable) {
        return message.error('未正确选择库表');
      }
      state.defaultSelect = `${state.curSql}.${state.dataSource}.${state.selectTable}`;
      visible.value = false;
      context.emit('updateDsInfo', state.defaultSelect, state.dataSourceItem, state.tableNotExist);
      // 选择完 初始化数据
      state.curSql = '';
      state.dataSourceItem = null;
      state.dataSource = '';
      state.selectTable = '';
      state.treeData = [];
      state.originTreeData = [];
      state.tableNotExist = false;
      expandedKeys.value = [];
      state.total = 0;
      state.currentPage = 1;
      state.currentDSWord = '';
      dataSources.value = [];
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
        o.key = `${dbName}.${table}`;
        res.push(o);
      });
      return res;
    };
    // 展开数据库树获取表叶子
    const handleExpandSql = async (expandedKeys, { expanded, node }) => {
      showTableSearch.value = true;
      const dbName = node.eventKey;
      state.searchDB = dbName;
      currentDbValue.value = dbName;
      if (expanded) {
        spinning.value = true;
      }
      const loaded = state.originTreeData.find(
        (i) => i.title == dbName && i.children.length > 0
      );
      if (!loaded) {
        let tables = await asyncGetTables(state.curSql, state.dsId, dbName).finally(() => {
          spinning.value = false;
        });
        const newDbs = [];
        state.treeData.forEach(async (td) => {
          if (td.title === dbName) {
            // 应该根据searchWord过滤，不然用户先搜索，然后在展开库，仍然会看到所有表，体验不好
            let tempTables = cloneDeep(tables);
            if (state.searchWord) {
              tempTables = tempTables.filter((i) =>
                new RegExp(state.searchWord, 'i').test(i.title)
              );
              if (!tempTables.some(item => item.title === state.searchWord)) {
                tempTables.unshift({
                  title: `${state.searchWord}(新增)`,
                  key: `${dbName}.${state.searchWord}`,
                  isLeaf: true
                })
              }
            }
            tempTables.forEach((tb) => {
              tb.isLeaf = true;
            });
            td.children = tempTables.slice();
            newDbs.push(td);
            return;
          }
        });
        state.treeData = newDbs;
        const newOriginDbs = [];
        state.originTreeData.forEach(async (td) => {
          if (td.title === dbName) {
            let tempTables = cloneDeep(tables);
            tempTables.forEach((tb) => {
              tb.isLeaf = true;
            });
            td.children = tempTables.slice();
            newOriginDbs.push(td);
            return;
          }
        });
        state.originTreeData = newOriginDbs;
      } else {
        setTimeout(() => {
          spinning.value = false;
        }, 2000)
      }
    };
    const getBg = () => {
      let name =
        state.curSql ||
        (typeof state.defaultSelect === 'string'
          ? state.defaultSelect.split('.')[0]
          : state.defaultSelect[0]);
      return `background-image: url(${require('@/images/dataSourceTypeIcon/' +
        name +
        '.png')})`;
    };
    // 数据库树状图图表替换
    const getIcon = (props) => {
      const databaseActive = require('@/images/dataSourceTypeIcon/database_active.png');
      const database = require('@/images/dataSourceTypeIcon/database.png');
      const table = require('@/images/dataSourceTypeIcon/table.png');
      const { isLeaf, expanded } = props;
      if (isLeaf) {
        return <img src={table} style="margin-bottom: 4px;" />;
      }
      return (
        <img
          src={expanded ? databaseActive : database}
          style="margin-bottom: 4px;"
        />
      );
    };
    return {
      ...toRefs(state),
      dataSourceList,
      selectItem,
      visible,
      showModal,
      handleOk,
      sqlList,
      handleChangeSql,
      handleExpandSql,
      handleChangeDS,
      getBg,
      expandedKeys,
      createTree,
      filterDatabase,
      filterTree,
      spinning,
      showTableSearch,
      showDbSearch,
      getIcon,
      currentDbValue,
      handleScrollDS,
      handleSearchDS
    };
  },
});
</script>

<style lang="less" scoped>
.sds-wrap {
  display: inline-block;
  .logo {
    width: 20px;
    height: 20px;
    background-size: cover;
    display: inline-block;
    vertical-align: middle;
  }
  .sds-button {
    width: 420px;
    height: 128px;
    background: #f8fafd;
    border: 1px dashed #dee4ec;
    border-radius: 2px;
    line-height: 128px;
    text-align: center;

    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.65);
    font-weight: 500;
    cursor: pointer;
  }

  .sds-title-tag {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    padding: 5px 15px;
    background: #e6f0ff;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.65);
    font-weight: 500;
    height: 32px;
    cursor: pointer;
    text-align: center;
    &:first-child {
      font-weight: bold;
    }
    &:last-child {
      border-radius: 0 4px 4px 0;
    }
  }
  &:hover {
    .sds-title-tag {
      color: #2e92f7;
    }
  }
  :deep(.ant-card-body) {
    padding: 5px;
    height: 137px;
    overflow: hidden;
  }
}
.sds-wrap-b {
  min-height: 300px;
  max-height: 400px;
  overflow-y: auto;
}
</style>
