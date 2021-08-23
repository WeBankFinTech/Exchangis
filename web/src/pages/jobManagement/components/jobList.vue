<template>
  <div class="content">
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
          <div class="cardWrap">
            <div v-for="(item, index) in jobList" :key="index" class="card">
              <job-card :jobData="item" @showJobDetail="showJobDetail"/>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2"
          ><template #tab>
            <span>
              <NodeIndexOutlined />
              流式任务
            </span>
          </template>
          <div class="cardWrap">
            <div v-for="(item, index) in jobList" :key="index" class="card">
              <job-card :jobData="item" />
            </div></div
        ></a-tab-pane>
      </a-tabs>
    </div>
    <CreateJob :visible="visible" @handleCreateJob="handleCreateJob" />
  </div>
</template>
<script>
import {
  DownloadOutlined,
  NodeIndexOutlined,
  ApiOutlined,
  PlusOutlined,
} from "@ant-design/icons-vue";
import CreateJob from "./createJob.vue";
import JobCard from "./job_card.vue";
export default {
  components: {
    DownloadOutlined,
    NodeIndexOutlined,
    ApiOutlined,
    PlusOutlined,
    CreateJob,
    JobCard,
  },
  data() {
    return {
      search: "1222",
      userName: "safdsaf",
      activeKey: "1",
      visible: false,
      loading: false,
      jobList: [
        {
          id: 1, // 任务id
          projectId: 1, // 所属项目id
          jobName: "任务名1",
          jobType: "OFFLINE",
          engineType: "DataX", // 执行引擎
          jobLabels: "renwu, hello, hello",
          jobDesc: "任务描述",
        },
        {
          id: 2, // 任务id
          projectId: 1, // 所属项目id
          jobName: "任务名2",
          jobType: "STREAM",
          engineType: "Sqoop", // 执行引擎
          jobLabels: "renwu, hello, hello",
          jobDesc:
            "任务描述ets how a flex item will grow or shrink to fit the space available in itsets how a flex item will grow or shrink to fit the space available in its",
        },
      ],
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
    showJobDetail(data){
      console.log(data);
      this.$emit("showJobDetail", data)
    }
  },
};
</script>
<style scoped lang="less">
.cardWrap {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 30px;
  .card {
    margin: 10px 20px 10px 0px;
  }
}
</style>
