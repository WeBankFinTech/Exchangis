<template>
  <div class="content">
    <!-- <div class="projectNav">
      <img class="img" src="../../images/u32.svg" />
      <router-link to="/projectManage"
        ><div class="link">
          {{ t("projectManage.topLine.title") }}
        </div></router-link
      >
      <div class="divider">/</div>
      <div class="name">{{ name }}</div>
    </div> -->

    <!-- top nav -->
    <div class="job-management-tabs">
      <div class="job-management-tab">
        <div @click="() => changeTab({})" class="job-management-tab-item">
          <span class="iconfont icon-gongzuoliu job-management-icon"></span>
          <span class="job-management-tab-name">{{ t("job.list") }}</span>
        </div>
        <div
          v-for="(item, idx) in tabs"
          :key="idx"
          class="job-management-tab-item"
          :class="{active: idx === active}"
          @click="choose(idx)"
          @mouseenter.self="item.isHover = true"
          @mouseleave.self="item.isHover = false"
        >
          <div>
            <span class="iconfont icon-hive job-management-icon"></span>
          </div>
          <div
            :title="item.title"
            class="job-management-tab-name"
            @click="() => changeTab(item)"
          >
            {{ item.jobName }}
          </div>
          <div class="closeIcon">
            <a-popconfirm
              title="确定未保存就离开？"
              @confirm="() => deleteTab(item)"
            >
              <template #icon
                ><QuestionCircleOutlined style="color: red"
              /></template>
              <CloseOutlined />
            </a-popconfirm>
          </div>
        </div>
      </div>
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
        <div class="closeIcon">
          <a-popconfirm
            title="确定未保存就离开？"
            @confirm="() => deleteTab(item)"
          >
            <template #icon
              ><QuestionCircleOutlined style="color: red"
            /></template>
            <CloseOutlined />
          </a-popconfirm>
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
import { toRaw, defineAsyncComponent } from "vue";
import { useI18n } from "@fesjs/fes";
import {
  UnorderedListOutlined,
  ClusterOutlined,
  CloseOutlined,
  QuestionCircleOutlined,
} from "@ant-design/icons-vue";
export default {
  components: {
    JobList: defineAsyncComponent(() => import("./components/jobList.vue")),
    JobDetail: defineAsyncComponent(() => import("./components/jobDetail.vue")),
    UnorderedListOutlined,
    ClusterOutlined,
    CloseOutlined,
    QuestionCircleOutlined,
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
      active: 0,
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
    choose(idx) {
      this.active  = idx
      debugger
    },
  },
};
</script>
<style scoped lang="less">
@import '../../common/content.less';
.job-management-icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
  color: #2e92f7;
}
.job-management-tabs {
}

.job-management-tab {
  height: 34px;
  display: flex;
  border-bottom: 1px solid #dee4ec;
  &-item {
    position: relative;
    height: 33px;
    line-height: 33px;
    padding: 0 8px;
    cursor: pointer;
    overflow: hidden;
    text-align: center;
    min-width: 100px;
    max-width: 200px;
    display: flex;
    background-color: #fff;
    border-right: 1px solid #e8eaec;
    &:active {
      margin-top: -1px;
      &:before {
        content: "";
        position: absolute;
        top: -1px;
        left: 0;
        right: 0;
        height: 2px;
        background-color: #2e92f7;
      }
      height: 34px;
      line-height: 33px;
      background-color: #ebf5ff;
      color: #2e92f7;
    }
  }

  &-name {
    width: 100%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    padding-right: 5px;
    padding-left: 5px;
    font-size: 14px;
    font-family: PingFangSC-Medium;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.65);
    &.active {
      color: #2e92f7;
    }
  }
}
.content {
  box-sizing: border-box;
  background-color: #fff;
  height: calc(100vh - 48px);
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
