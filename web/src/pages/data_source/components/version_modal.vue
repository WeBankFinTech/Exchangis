<template>
  <a-modal title="版本列表" footer="" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-table size="small" :columns="columns" :data-source="data"> </a-table>
  </a-modal>
</template>

<script>
const columns = [
  {
    title: "版本",
    dataIndex: "name",
    align: "center",
    key: "name",
  },
  {
    title: "状态",
    dataIndex: "age",
    align: "center",
    key: "age",
  },
  {
    title: "创建者",
    dataIndex: "address",
    align: "center",
    key: "address",
  },
  {
    title: "版本注释",
    key: "tags",
    align: "center",
    dataIndex: "tags",
  },
  {
    title: "创建时间",
    align: "center",
    key: "action",
  },
  {
    title: "操作",
    align: "center",
    key: "action",
  },
];
export default {
  name: "VersionModal",
  props: {
    // 是否可见
    visible: {
      type: Boolean,
    },
  },

  data() {
    return {
      confirmLoading: false,
      columns,
      data: [],
    };
  },
};
</script>

<style scoped lang="less"></style>
