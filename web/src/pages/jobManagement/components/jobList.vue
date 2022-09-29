<template>
  <div class="jl-content">
    <div class="formWrap">
      <a-input
        :placeholder="t('job.action.jobSearch')"
        v-model:value="search"
        @pressEnter="handleSearch"
        style="width: 336px"
      >
        <template #prefix>
          <icon-searchOutlined style="color: #dee4ec" />
        </template>
      </a-input>

      <!-- <a-input-search
        v-model:value="search"
        :placeholder="t('job.action.jobSearch')"
        style="width: 300px"
        @search="handleSearch"
      /> -->
      <div>
        <a-button
          type="primary"
          style="
            width: 106px;
            margin-left: 8px;
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #ffffff;
            line-height: 22px;
            font-weight: 400;
          "
          :disabled="hasPermission"
          @click="addJob"
        >
          <template #icon> <PlusOutlined /></template
          >{{ t("job.action.createJob") }}
        </a-button>
        <a-upload
          action="/api/rest_j/v1/exchangis/job/import"
          @change="handleImport"
          :showUploadList="false"
        >
          <a-button
            style="
              width: 78px;
              margin-left: 8px;
              font-family: PingFangSC-Regular;
              font-size: 14px;
              color: rgba(0, 0, 0, 0.65);
              line-height: 22px;
              font-weight: 400;
            "
          >
            <template #icon> <DownloadOutlined /></template
            >{{ t("job.action.import") }}
          </a-button>
        </a-upload>
      </div>
    </div>
    <a-spin :spinning="spinning">
      <div class="tabWrap">
        <a-tabs type="card" v-model:activeKey="activeKey">
          <a-tab-pane key="1">
            <template #tab>
              <span>
                <ApiOutlined />
                {{ t("job.type.offline") }}
              </span>
            </template>
            <div class="cardWrap">
              <div v-if="offlineList.length === 0" class="emptyTab">
                <div class="void-page-wrap">
                  <div class="void-page-main">
                    <div class="void-page-main-img">
                      <img
                        src="../../../assets/img/void_page.png"
                        alt="空页面"
                      />
                    </div>
                    <div class="void-page-main-title">
                      <span>暂时没有离线任务，请先创建一个离线任务</span>
                    </div>
                    <!--<div class="void-page-main-button">
                      <a-button
                        type="primary"
                        style="
                          width: 106px;
                          margin-left: 8px;
                          font-family: PingFangSC-Regular;
                          font-size: 14px;
                          color: #ffffff;
                          line-height: 22px;
                          font-weight: 400;
                        "
                        @click="addJob"
                      >
                        <template #icon> <PlusOutlined /></template
                        >{{ t("job.action.createJob") }}
                      </a-button>
                      <a-upload
                        action="/api/rest_j/v1/exchangis/job/import"
                        @change="handleImport"
                        :showUploadList="false"
                      >
                        <a-button
                          style="
                            width: 78px;
                            margin-left: 8px;
                            font-family: PingFangSC-Regular;
                            font-size: 14px;
                            color: rgba(0, 0, 0, 0.65);
                            line-height: 22px;
                            font-weight: 400;
                          "
                        >
                          <template #icon> <DownloadOutlined /></template
                          >{{ t("job.action.import") }}
                        </a-button>
                      </a-upload>
                    </div>-->
                  </div>
                </div>
              </div>
              <div v-for="item in offlineList" :key="item.id" class="card">
                <job-card
                  :jobData="item"
                  type="OFFLINE"
                  @showJobDetail="showJobDetail"
                  @handleJobCopy="handleJobCopy"
                  @handleJobModify="handleJobModify"
                  @refreshList="getJobs"
                  @handleDel="handleDel"
                />
              </div>
              <div class="pagination-line">
                <a-pagination
                  v-model:current="pageCfg.current"
                  v-model:pageSize="pageCfg.size"
                  @change="handleChangePage"
                  :total="total"
                  show-less-items
                />
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="2">
            <template #tab>
              <span>
                <NodeIndexOutlined />
                {{ t("job.type.stream") }}
              </span>
            </template>
            <div class="cardWrap">
              <div v-if="streamList.length === 0" class="emptyTab">
                <div class="void-page-wrap">
                  <div class="void-page-main">
                    <div class="void-page-main-img">
                      <img
                        src="../../../assets/img/void_page.png"
                        alt="空页面"
                      />
                    </div>
                    <div class="void-page-main-title">
                      <span>暂时没有流式任务，请先创建一个流式任务</span>
                    </div>
                    <!--<div class="void-page-main-button">
                      <a-button
                        type="primary"
                        style="
                          width: 106px;
                          margin-left: 8px;
                          font-family: PingFangSC-Regular;
                          font-size: 14px;
                          color: #ffffff;
                          line-height: 22px;
                          font-weight: 400;
                        "
                        @click="addJob"
                      >
                        <template #icon> <PlusOutlined /></template
                        >{{ t("job.action.createJob") }}
                      </a-button>
                      <a-upload
                        action="/api/rest_j/v1/exchangis/job/import"
                        @change="handleImport"
                        :showUploadList="false"
                      >
                        <a-button
                          style="
                            width: 78px;
                            margin-left: 8px;
                            font-family: PingFangSC-Regular;
                            font-size: 14px;
                            color: rgba(0, 0, 0, 0.65);
                            line-height: 22px;
                            font-weight: 400;
                          "
                        >
                          <template #icon> <DownloadOutlined /></template
                          >{{ t("job.action.import") }}
                        </a-button>
                      </a-upload>
                    </div>-->
                  </div>
                </div>
              </div>
              <div v-for="item in streamList" :key="item.id" class="card">
                <job-card
                  :jobData="item"
                  type="STREAM"
                  @showJobDetail="showJobDetail"
                  @handleJobCopy="handleJobCopy"
                  @handleJobModify="handleJobModify"
                  @refreshList="getJobs"
                  @handleDel="handleDel"
                />
              </div>
              <div class="pagination-line">
                <a-pagination
                  v-model:current="pageCfg2.current"
                  v-model:pageSize="pageCfg2.size"
                  @change="handleChangePage"
                  :total="total2"
                  show-less-items
                />
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-spin>
    <CreateJob
      :visible="visible"
      :editData="editJobData"
      :projectId="projectId"
      :mode="mode"
      @handleJobAction="handleJobAction"
    />
  </div>
</template>
<script>
import {
  DownloadOutlined,
  NodeIndexOutlined,
  ApiOutlined,
  PlusOutlined,
  SearchOutlined,
} from "@ant-design/icons-vue";
import { defineAsyncComponent } from "vue";
import { message } from "ant-design-vue";
import { useI18n } from "@fesjs/fes";
import { getJobs, getProjectPermission } from "@/common/service";

export default {
  components: {
    DownloadOutlined,
    NodeIndexOutlined,
    ApiOutlined,
    PlusOutlined,
    CreateJob: defineAsyncComponent(() => import("./createJob.vue")),
    JobCard: defineAsyncComponent(() => import("./job_card.vue")),
    iconSearchOutlined: SearchOutlined,
  },
  data() {
    const { t } = useI18n({ useScope: "global" });
    return {
      t,
      search: "",
      userName: "safdsaf",
      activeKey: "1",
      visible: false,
      loading: false,
      editJobData: {},
      offlineList: [],
      streamList: [],
      offlineListOrigin: [],
      streamListOrigin: [],
      projectId: this.$route.query.id,
      spinning: false,
      mode: 'create',
      // 分页配置
      pageCfg: {
        current: 1,
        size: 10,
      },
      pageCfg2: {
        current: 1,
        size: 10,
      },
      total: 0,
      total2: 0,
      hasPermission: true, // 是否有添加
    };
  },
  mounted() {
    this.getJobs("OFFLINE");
    this.getJobs("STREAM");
    this.getPermission(this.projectId);
  },
  methods: {
    async getJobs(type, current=1, size=10) {
      this.spinning = true;
      const { result, total } = await getJobs(this.projectId, type, this.search, current, size);
      this.spinning = false;
      if (type === "OFFLINE") {
        this.pageCfg.current = current
        this.offlineList = result;
        this.offlineListOrigin = result;
        this.total = total
      } else {
        this.pageCfg2.current = current
        this.streamList = result;
        this.streamListOrigin = result;
        this.total2 = total
      }
      //this.search = "";
      //this.handleSearch();
    },
    handleSearch() {
      /*const search = this.search;
      if (!search) {
        this.offlineList = [...this.offlineListOrigin];
        this.streamList = [...this.streamListOrigin];
      } else {
        this.offlineList = [...this.offlineListOrigin].filter((item) =>
          item.jobName.toLowerCase().includes(search.toLowerCase())
        );
        this.streamList = [...this.streamListOrigin].filter((item) =>
          item.jobName.toLowerCase().includes(search.toLowerCase())
        );
      }*/
      this.handleChangePage(1)
    },
    addJob() {
      this.mode = 'create'
      this.visible = true;
      this.editJobData = {}
    },
    handleJobAction(newJobData) {
      this.visible = false;
      this.editJobData = {};
      if (newJobData) {
        const newKey = newJobData.jobType === "OFFLINE" ? "1" : "2";
        if (newKey !== this.activeKey) {
          this.activeKey = newKey;
        }
        this.getJobs(newJobData.jobType);
      }
      this.$emit("changeType");
    },
    handleJobCopy(data) {
      this.mode = 'copy'
      this.visible = true;
      this.editJobData = data;
    },
    handleJobModify(data) {
      this.mode = 'modify'
      this.visible = true;
      this.editJobData = data;
    },
    showJobDetail(data) {
      console.log(data);
      this.$emit("showJobDetail", data);
    },
    handleImport(info) {
      if (info.file.status !== "uploading") {
        console.log(info.file);
      }
      if (info.file.status === "done") {
        message.success(this.t("job.action.fileUpSuccess"));
        this.getJobs("OFFLINE");
        this.getJobs("STREAM");
      } else if (info.file.status === "error") {
        message.error(this.t("job.action.fileUpFailed"));
      }
    },
    handleChangePage(current) {
      if (this.activeKey == 1) {
        this.pageCfg.current = current
        this.getJobs('OFFLINE', current, this.pageCfg.size)
      } else {
        this.pageCfg2.current = current
        this.getJobs('STREAM', current, this.pageCfg2.size)
      }
    },
    // 获取用户权限
    async getPermission(projectId) {
      const { exchangisProjectUser } = await getProjectPermission(projectId) || {};
      if (exchangisProjectUser?.projectId === this.projectId) {
        this.hasPermission = false;
      }
    },
    handleDel(id) {
      this.$emit('updateTabs', id)
    }
  },
  watch: {
    activeKey: {
      handler: function (newVal) {
        console.log(newVal);
      },
      deep: true,
    },
  },
};
</script>
<style scoped lang="less">
.formWrap {
  display: flex;
  justify-content: space-between;
  padding: 24px 24px 0 15px;
  padding-top: 24px;
}
.tabWrap {
  margin-top: 10px;
  padding: 0px 15px;
}
.cardWrap {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 30px;
  .emptyTab {
    font-size: 16px;
    height: 60vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .card {
    margin: 10px 20px 10px 0px;
  }
  .pagination-line {
    width: 100%;
    text-align: right;
    position: fixed;
    right: 10px;
    bottom: 10px;
  }
}

.void-page-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #fff;
  .void-page-main {
    &-img {
      text-align: center;
    }
    &-title {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.45);
      letter-spacing: 0;
      text-align: center;
      line-height: 28px;
      font-weight: 400;
      margin-top: 24px;
      margin-bottom: 16px;
    }
    &-button {
      min-width: 106px;
      height: 32px;
      line-height: 32px;
      text-align: center;
      &-item {
        background: #2e92f7;
        color: #fff;
      }
    }
  }
}
</style>
