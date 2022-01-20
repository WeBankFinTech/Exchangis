<template>
  <a-modal title="版本列表" footer="" :visible="visible" :width="600" :confirm-loading="confirmLoading" @cancel="$emit('update:visible', false)">
    <a-table size="small" :pagination="false" :columns="columns" :loading="confirmLoading" :data-source="data">
      <template #published="{ text }">
        {{ text ? "是" : "否" }}
      </template>
      <template #action="row">
        <a-space>
          <a-button v-if="!row.text.published" size="small" @click="publishDataSource(row.text.versionId)">发布</a-button>
          <a-button size="small" @click="$emit('openModal', row.text, rowInfo)">查看</a-button>
          <a-button size="small" @click="handleTestConnect(row)">测试链接</a-button>
        </a-space>
      </template>
    </a-table>
  </a-modal>
</template>

<script>
import { getDataSourceVersionList, publishDataSource } from "@/common/service";
import { message } from "ant-design-vue";
import {
  testDataSourceConnect,
} from "@/common/service";
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
    title: "是否发布",
    dataIndex: "published",
    align: "center",
    slots: { customRender: "published" },
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
    title: "操作",
    align: "center",
    slots: { customRender: "action" },
  },
];
export default {
  name: "VersionModal",
  props: {
    visible: { type: Boolean },
    id: { type: [Number, String] },
    rowInfo: {type: [Object]}
  },
  watch: {
    visible(val) {
      if (val === true) this.getDataSourceVersionList();
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
    async publishDataSource(versionId) {
      this.confirmLoading = true;
      await publishDataSource(this.id, versionId);
      this.confirmLoading = false;
      await this.getDataSourceVersionList();
      message.success("发布成功");
    },
    // 测试链接
    async handleTestConnect(row) {
      await testDataSourceConnect(row.text.datasourceId, row.text.versionId);
      message.success("连接成功");
    }
  },
};
</script>

<style scoped lang="less"></style>
