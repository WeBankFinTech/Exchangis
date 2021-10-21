<template>
  <div class="content">
    <div class="projectNav">
      <img class="img" src="../../images/u32.svg" />
      <router-link to="/projectManage"
        ><div class="link">
          {{ t("projectManage.topLine.title") }}
        </div></router-link
      >
      <div class="divider">/</div>
      <div class="name">{{ name }}</div>
    </div>
    <div class="navWrap">
      <div @click="() => changeTab({})" class="listTitle">
        <UnorderedListOutlined />{{ t("job.list") }}
      </div>
      <div class="divider"></div>
      <div class="titleWrap" v-for="(item, index) in tabs" :key="index">
        <div
          :class="{ detailTitle: true, choosed: item.id === activeTabId }"
          @click="() => changeTab(item)"
        >
          <ClusterOutlined />
          <a-tooltip :title="item.jobName">
            <div class="jobNameWrap">{{ item.jobName }}</div>
          </a-tooltip>
        </div>
        <div class="closeIcon" @click="() => deleteTab(item)">
          <CloseOutlined />
        </div>
      </div>
    </div>
    <job-list v-show="!activeTabId" @showJobDetail="showJobDetail" />
    <template v-for="job in tabs">
      <job-detail v-show="activeTabId == job.id" :curTab="job"></job-detail>
    </template>
  </div>
</template>
<script>
import { toRaw } from "vue";
import JobList from "./components/jobList.vue";
import JobDetail from "./components/jobDetail.vue";
import { useI18n } from "@fesjs/fes";
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
    CloseOutlined,
  },
  data() {
    const { t } = useI18n({ useScope: "global" });
    return {
      t,
      name: this.$route.query.name,
      choosedTab: "jobList",
      activeTabId: "",
      curTab: "",
      tabs: [],
    };
  },
  methods: {
    async getJobs(type = "OFFLINE") {
      this.tabs = (await getJobs(this.$route.query.id, type)).result;
    },
    showJobDetail(data) {
      data = toRaw(data);
      const tabs = [...this.tabs];
      if (!tabs.find((item) => item.id === data.id)) {
        tabs.push(data);
      }
      this.tabs = tabs;
      this.activeTabId = data.id;
      this.curTab = data;
    },
    changeTab(data) {
      data = toRaw(data);
      console.log(data, data.id);
      this.curTab = data;
      this.activeTabId = data.id;
    },
    deleteTab(data) {
      data = toRaw(data);
      const tabs = [...this.tabs];
      const index = tabs.findIndex((item) => item.id === data.id);
      if (index !== -1) {
        tabs.splice(index, 1);
      }
      this.tabs = tabs;
      this.activeTabId = undefined;
    },
  },
};
</script>
<style scoped lang="less">
.content {
  box-sizing: border-box;
}
.projectNav {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-left: 15px;
  margin-top: 10px;
  .img {
    width: 25px;
  }
  .link {
    color: #000000;
    font-size: 16px;
    margin-left: 10px;
    cursor: pointer;
    &:hover {
      color: #1890ff;
    }
  }
  .divider {
    color: #949494;
    padding: 0px 10px;
  }
  .name {
    color: #333333;
    font-size: 16px;
    font-weight: 600;
  }
}
.navWrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-left: 15px;
  margin-top: 15px;
  .listTitle {
    font-family: "Arial Negreta", "Arial Normal", "Arial";
    font-weight: 600;
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
  .titleWrap {
    position: relative;
    .detailTitle {
      height: 35px;
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 600;
      font-style: normal;
      font-size: 14px;
      color: #000000;
      display: flex;
      justify-content: flex-start;
      align-items: center;
      padding-left: 5px;
      padding-right: 10px;
      cursor: pointer;
    }
    .choosed {
      background: #fff;
    }
    .jobNameWrap {
      width: 100px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }
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
}
</style>
