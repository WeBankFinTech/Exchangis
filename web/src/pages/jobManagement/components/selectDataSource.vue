<template>
  <div class="sds-wrap">
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
      <!-- <a-tree
        :tree-data="treeData"
        v-model:expandedKeys="expandedKeys"
        v-model:selectedKeys="selectedKeys"
      ></a-tree> -->
      <a-directory-tree
        multiple
        v-model:expandedKeys="expandedKeys"
        v-model:selectedKeys="selectedKeys"
      >
        <a-tree-node key="0-0" title="parent 0">
          <a-tree-node key="0-0-0" title="leaf 0-0" is-leaf />
          <a-tree-node key="0-0-1" title="leaf 0-1" is-leaf />
        </a-tree-node>
        <a-tree-node key="0-1" title="parent 1">
          <a-tree-node key="0-1-0" title="leaf 1-0" is-leaf />
          <a-tree-node key="0-1-1" title="leaf 1-1" is-leaf />
        </a-tree-node>
      </a-directory-tree>
    </div>
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
      key: "0-0",
      childrens: [
        { title: "a1", key: "0-0-0" },
        { title: "test_table", key: "0-0-1" },
      ],
    },
    { title: "db_test_mask", key: "0-1" },
    { title: "db_test_mask1", key: "0-2" },
    { title: "db_test_mask2", key: "0-3" },
  ],
};
const treeData = [
  {
    title: "parent 1",
    key: "0-0",
    children: [
      {
        title: "parent 1-0",
        key: "0-0-0",
        disabled: true,
        children: [
          {
            title: "leaf",
            key: "0-0-0-0",
            disableCheckbox: true,
          },
          {
            title: "leaf",
            key: "0-0-0-1",
          },
        ],
      },
      {
        title: "parent 1-1",
        key: "0-0-1",
        children: [
          {
            key: "0-0-1-0",
            slots: {
              title: "title0010",
            },
          },
        ],
      },
    ],
  },
];
import { defineComponent, reactive, toRefs, computed, watch, ref } from "vue";

export default defineComponent({
  setup() {
    const dataBase = dataBaseTypes[0];
    const state = reactive({
      dataBase,
      dataBaseTypes,
      colonyData,
      colony: colonyData[dataBase][0],
    });
    const colonys = computed(() => {
      return colonyData[state.dataBase];
    });
    // const treeData = computed(() => {
    //   console.log("state.colony", state.colony);
    //   return tables[state.colony];
    // });
    const expandedKeys = ref(["0-0", "0-1"]);
    const selectedKeys = ref([]);
    watch(expandedKeys, () => {
      console.log("expandedKeys", expandedKeys);
    });
    watch(selectedKeys, () => {
      console.log("selectedKeys", selectedKeys);
    });
    watch(
      () => state.dataBase,
      (val) => {
        state.colony = state.colonyData[val][0];
      }
    );

    return { ...toRefs(state), colonys, expandedKeys, selectedKeys };
  },
});
</script>

<style lang="less" scoped></style>
