<template>
  <div class="container">
    <div class="tools-bar">
      <span @click="modalCfg.visible = true"><SettingOutlined />配置</span>
      <div class="divider"></div>
      <span><CaretRightOutlined />执行</span>
      <div class="divider"></div>
      <span><SaveOutlined />保存</span>
      <div class="divider"></div>
      <span><HistoryOutlined />执行历史</span>
    </div>
    <div class="jd-content">
      <div class="jd_left">
        <div class="sub-title">
          <DatabaseFilled class="database-icon"/>
          <span>子任务列表</span>
          <PlusSquareOutlined class="ps-icon"/>
        </div>
        <div v-for="(item, idx) in list" :key="idx" class="sub-content">
          <div class="task-title">
            <span class="subjobName">{{ item.subjobName }}</span>
            <DeleteOutlined class="delete-icon" @click="deleteSub(idx)" />
            <CopyOutlined class="copy-icon" @click="copySub(item)" />
          </div>
          <template v-if="item.dataSourceIds">
            <div class="sub-table" :title="item.dataSourceIds.source.db + '.' +  item.dataSourceIds.source.table">{{ item.dataSourceIds.source.db + '.' +  item.dataSourceIds.source.table }}</div>
            <div class="arrow-down-icon"><ArrowDownOutlined /></div>
            <div class="sub-table" :title="item.dataSourceIds.sink.db + '.' +  item.dataSourceIds.sink.table">{{ item.dataSourceIds.sink.db + '.' +  item.dataSourceIds.sink.table }}</div>
          </template>
        </div>
      </div>
      <div class="jd_right">
        <CheckCircleOutlined />
        <div>
          <DataSource v-bind:dsData="list[0]" />
        </div>
        <div>
          <FieldMap v-bind:fmData="list[0].transforms" />
        </div>
        <div>
          <ProcessControl v-bind:psData="list[0].settings" />
        </div>
      </div>
    </div>
    <config-modal
      v-model:visible="modalCfg.visible"
      :id="modalCfg.id"
      @finish="handleModalFinish"
    />
    <copy-modal
      v-model:visible="modalCopy.visible"
      :origin="copyObj"
      @finish="handleModalCopy"
    />
  </div>
</template>
<script>
import { toRaw } from "vue";
import {
  SettingOutlined,
  CaretRightOutlined,
  SaveOutlined,
  HistoryOutlined,
  DatabaseFilled,
  PlusSquareOutlined,
  CopyOutlined,
  DeleteOutlined,
  ArrowDownOutlined,
  CheckCircleOutlined,
} from "@ant-design/icons-vue";
import configModal from "./configModal";
import copyModal from "./copyModal";
import { getJobInfo } from "@/common/service";
import { jobInfo } from "../mock";
import DataSource from "./dataSource";
import FieldMap from "./fieldMap.vue";
import ProcessControl from "./processControl.vue";
export default {
  components: {
    SettingOutlined,
    CaretRightOutlined,
    SaveOutlined,
    HistoryOutlined,
    DatabaseFilled,
    PlusSquareOutlined,
    CopyOutlined,
    DeleteOutlined,
    ArrowDownOutlined,
    CheckCircleOutlined,
    configModal,
    copyModal,
    DataSource,
    FieldMap,
    ProcessControl,
  },
  data() {
    return {
      name: "",
      modalCfg: {
        id: "",
        visible: false,
      },
      modalCopy: {
        visible: false,
      },
      jobData: {},
      copyObj: {},
      list: [],
    };
  },
  props: {
    curTab: Object,
  },
  watch: {
    curTab(n, o) {
      this.init();
    },
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      this.name = this.curTab.jobName;
      this.getInfo();
    },
    async getInfo() {
      try {
        //await getJobInfo(this.curTab.id);
        jobInfo.content.subJobs.forEach((item) => {
          item.engineType = jobInfo.engineType;
        });
        this.jobData = jobInfo;
        this.list = this.jobData.content.subJobs;
        console.log("jobData", this.jobData);
      } catch (error) {}
    },
    handleModalFinish() {},
    handleModalCopy(data) {
      if(data) {
        this.jobData.content.subJobs.push(data)
      }
      console.log(this.list, this.jobData.content.subJobs)
    },
    copySub(item) {
      this.copyObj = item;
      this.modalCopy.visible = true;
    },
    deleteSub(index) {
      this.jobData.content.subJobs.splice(index, 1)
    }
  },
};
</script>
<style scoped lang="less">
.container {
  .tools-bar {
    width: 100%;
    border-top: 1px solid rgb(228, 228, 228);
    border-bottom: 1px solid rgb(228, 228, 228);
    background: rgb(242, 242, 242);
    padding: 10px 30px;
    font-size: 16px;
    >span {
      cursor: pointer;
    }
    .anticon {
      margin-right: 5px;
    }
    .divider {
      width: 1px;
      height: 20px;
      background: rgba(0, 0, 0, 0.3);
      margin-left: 20px;
      margin-right: 20px;
      display: inline-block;
      position: relative;
      top: 5px;
    }
  }
  .jd-content {
    display: flex;
    overflow-x: auto;
    width: 100%;
    .jd_left {
      flex: 2;
      padding: 0 25px;
      .sub-title {
        font-size: 16px;
        font-weight: bolder;
        margin-top: 15px;
        .database-icon {
          color: rgb(102, 102, 255);
          margin-right: 5px;
        }
        .ps-icon {
          float: right;
          cursor: pointer;
          line-height: 28px;
        }
      }
      .sub-content {
        width: 200px;
        margin: 10px 0;
        border: 1px solid rgb(102, 102, 255);
        padding: 10px 15px;
        border-radius: 5px;
        .task-title {
          font-size: 15px;
          font-weight: bolder;
          .subjobName{
            color: rgb(102, 102, 255);
            cursor: pointer;
          }
          .delete-icon {
            float: right;
            cursor: pointer;
            margin-right: 10px;
            line-height: 28px;
          }
          .copy-icon {
            float: right;
            cursor: pointer;
            margin-right: 5px;
            line-height: 28px;
          }
        }
        .sub-table {
          color: rgb(22, 155, 213);
          text-align: center;
          margin: 5px 0;
          width: 100%;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
        .arrow-down-icon {
          text-align: center;
          font-weight: bolder;
          font-size: 16px;
        }
      }
    }
    .jd_right {
      flex: 5;
    }
  }
}
</style>
