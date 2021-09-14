<template>
  <div class="content">
    <div class="navWrap">
      <div @click="() => changeTab({})" class="listTitle">
        <UnorderedListOutlined />任务列表
      </div>
      <div class="divider"></div>
      <div
        v-for="(item, index) in tabs"
        :key="index"
        :class="{ detailTitle: true, choosed: item.id === activeTabId }"
        @click="() => changeTab(item)"
      >
        <ClusterOutlined />{{ item.jobName }}
        <div class="closeIcon" @click="() => deleteTab(item)">
          <CloseOutlined />
        </div>
      </div>
    </div>
    <job-list v-show="!activeTabId" @showJobDetail="showJobDetail" />
    <job-detail v-if="activeTabId" :curTab="curTab"></job-detail>
  </div>
</template>
<script>
import { toRaw } from "vue";
import JobList from "./components/jobList.vue";
import JobDetail from "./components/jobDetail.vue";
import {
  UnorderedListOutlined,
  ClusterOutlined,
  CloseOutlined,
} from "@ant-design/icons-vue";
export default {
  components: {
    JobList,
    JobDetail,
    UnorderedListOutlined,
    ClusterOutlined,
    CloseOutlined
  },
  data() {
    return {
      name: "jobManagement11",
      choosedTab: "jobList",
      activeTabId: "",
      curTab: "",
      tabs: [
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
  mounted() {
    console.log("mounted");
  },
  methods: {
    showJobDetail(data) {
      data = toRaw(data);
      console.log(data);
      const tabs = [...this.tabs];
      if (!tabs.find((item) => item.id === data.id)) {
        tabs.push(data);
      }
      this.activeTabId = data.id;
      this.tabs = tabs;
    },
    changeTab(data) {
      data = toRaw(data);
      console.log(data);
      this.curTab = data
      this.activeTabId = data.id;
    },
    deleteTab(data) {
      data = toRaw(data);
      const tabs = [...this.tabs];
      const index = tabs.findIndex((item) => item.id === data.id);
      if (index !== -1) {
        tabs.splice(index, 1);
      }
      this.activeTabId = undefined;
      this.tabs = tabs;
    }
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 16px;
  box-sizing: border-box;
}
.navWrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-left: 15px;
  .listTitle {
    font-family: "Arial Negreta", "Arial Normal", "Arial";
    font-weight: 700;
    font-style: normal;
    font-size: 16px;
    cursor: pointer;
  }
  .divider {
    width: 2px;
    height: 27px;
    background: rgba(0, 0, 0, 0.3);
    margin-left: 20px;
    margin-right: 20px;
  }
  .detailTitle {
    width: 120px;
    height: 35px;
    font-family: "Arial Negreta", "Arial Normal", "Arial";
    font-weight: 700;
    font-style: normal;
    font-size: 14px;
    color: #000000;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding-left: 5px;
    position: relative;
    cursor: pointer;
    .closeIcon {
      position: absolute;
      top: 2px;
      right: 2px;
      cursor: pointer;
      color: #797979;
      font-size: 12px;
      font-weight: 700;
    }
  }
  .choosed {
    background: #fff;
  }
}
</style>
