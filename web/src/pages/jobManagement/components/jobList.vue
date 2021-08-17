<template>
  <div class="container">
    <div class="formWrap">
      <a-input-search
        v-model:value="search"
        placeholder="input search text"
        style="width: 200px"
        @search="handleSearch"
      />
      <a-button
        type="primary"
        @click="addJob"
        style="width: 160px; margin-left: 30px"
      >
        <template #icon> <PlusOutlined /></template>新增任务</a-button
      >
      <a-button type="primary" style="width: 160px; margin-left: 30px">
        <template #icon> <DownloadOutlined /></template>导入</a-button
      >
    </div>
    <div>
      <a-tabs v-model:activeKey="activeKey">
        <a-tab-pane key="1"
          ><template #tab>
            <span>
              <ApiOutlined />
              离线任务
            </span>
          </template>
          Tab 1</a-tab-pane
        >
        <a-tab-pane key="2"
          ><template #tab>
            <span>
              <NodeIndexOutlined />
              流式任务
            </span>
          </template>
          Tab 2</a-tab-pane
        >
      </a-tabs>
    </div>
    <CreateJob :visible="visible" @handleCreateJob="handleCreateJob"/>
  </div>
</template>
<script>
import {
  DownloadOutlined,
  NodeIndexOutlined,
  ApiOutlined,
  PlusOutlined,
} from "@ant-design/icons-vue";
import CreateJob from './createJob.vue';
export default {
  components: {
    DownloadOutlined,
    NodeIndexOutlined,
    ApiOutlined,
    PlusOutlined,
    CreateJob
  },
  data() {
    return {
      search: "1222",
      userName: "safdsaf",
      activeKey: "1",
      visible: false,
      loading: false,
    };
  },
  methods: {
    handleSearch(element) {
      const value = element.target.value;
      this.search = value;
    },
    addJob() {
      console.log(122);
      this.visible = true;
    },
    handleCreateJob(status) {
      this.visible = false;
      console.log(status);
    },
  },
};
</script>
