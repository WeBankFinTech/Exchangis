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
          <a-select
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
const dataBaseTypes = ["Hive", "HBase"];
const colonyData = {
  Hive: ["A集群", "B集群", "C集群"],
  HBase: ["D集群", "E集群", "F集群"],
};
const tables = {
  A集群: [
    {
      title: "default",
      key: "default",
      children: [
        { title: "a1", key: "a1" },
        { title: "test_table", key: "test_table" },
      ],
    },
    { title: "db_test_mask", key: "db_test_mask" },
    { title: "db_test_mask1", key: "db_test_mask1" },
    { title: "db_test_mask2", key: "db_test_mask2" },
  ],
  D集群: [
    {
      title: "default2",
      key: "default2",
      children: [
        { title: "a2", key: "a2" },
        { title: "test_table2", key: "test_table2" },
      ],
    },
    { title: "db_test_mask3", key: "db_test_mask3" },
    { title: "db_test_mask4", key: "db_test_mask4" },
    { title: "db_test_mask5", key: "db_test_mask5" },
  ],
};
import {
  defineComponent,
  reactive,
  toRefs,
  computed,
  watch,
  ref,
  toRaw,
} from "vue";

export default defineComponent({
  props: {
    title: String,
  },
  emits: ["updateDsInfo"],
  setup(props, context) {
    const dataBase = dataBaseTypes[0];
    const state = reactive({
      dataBase,
      dataBaseTypes,
      colonyData,
      colony: colonyData[dataBase][0],
      defaultSelect: props.title,
    });
    const visible = ref(false);

    const showModal = () => {
      visible.value = true;
    };
    const colonys = computed(() => {
      return colonyData[state.dataBase];
    });
    const treeData = computed(() => {
      return tables[state.colony];
    });
    const expandedKeys = ref([]);
    const selectedKeys = ref();
    let _defaultSelect = "";
    const selectItem = (e) => {
      _defaultSelect = `${state.dataBase}-${state.colony}-${e.join("")}`;
    };
    const handleOk = () => {
      visible.value = false;
      state.defaultSelect = _defaultSelect;
      context.emit("updateDsInfo", _defaultSelect);
    };
    watch(
      () => state.dataBase,
      (val) => {
        state.colony = state.colonyData[val][0];
      }
    );
    return {
      ...toRefs(state),
      colonys,
      treeData,
      expandedKeys,
      selectedKeys,
      selectItem,
      visible,
      showModal,
      handleOk,
    };
  },
});
</script>

<style lang="less" scoped>
.sds-wrap {
  display: inline-block;
}
</style>
