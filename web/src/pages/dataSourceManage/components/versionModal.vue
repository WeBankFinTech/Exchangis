<template>
  <a-modal title="版本列表" footer="" :visible="visible" :width="600" :confirm-loading="confirmLoading" @cancel="$emit('update:visible', false)">
    <a-table size="small" :pagination="false" :columns="columns" :loading="confirmLoading" :data-source="data"> </a-table>
  </a-modal>
</template>

<script>
import { getDataSourceVersionList } from "@/common/service";
import { version } from "ant-design-vue";
const columns = [
  {
    title: "版本ID",
    align: "center",
    dataIndex: "versionId",
  },
  {
    title: "创建者",
    dataIndex: "createUser",
    align: "center",
  },
  {
    title: "HOST",
    dataIndex: ["connectParams", "host"],
    align: "center",
  },
  {
    title: "PORT",
    dataIndex: ["connectParams", "port"],
    align: "center",
  },
  {
    title: "用户名",
    dataIndex: ["connectParams", "username"],
    align: "center",
  },
  {
    title: "密码",
    dataIndex: ["connectParams", "password"],
    align: "center",
  },
];
export default {
  name: "VersionModal",
  props: {
    visible: { type: Boolean },
    id: { type: [Number, String] },
  },
  watch: {
    visible(val) {
      if (val === true) {
        this.getDataSourceVersionList();
      }
    },
  },
  data() {
    return {
      confirmLoading: false,
      columns,
      data: [],
    };
  },
  methods: {
    async getDataSourceVersionList() {
      this.confirmLoading = true;
      let { versions } = await getDataSourceVersionList(this.id);
      this.confirmLoading = false;
      this.data = versions;
    },
  },
};
</script>

<style scoped lang="less"></style>
