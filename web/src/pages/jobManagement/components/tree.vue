<template>
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
</template>
<script>
import { defineComponent, ref } from "vue";
export default defineComponent({
  setup() {
    const expandedKeys = ref(["0-0", "0-1"]);
    const selectedKeys = ref([]);
    return {
      expandedKeys,
      selectedKeys,
    };
  },
});
</script>
