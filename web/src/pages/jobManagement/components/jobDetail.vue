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
          <DatabaseFilled class="database-icon" />
          <span>子任务列表</span>
          <a-popconfirm
            title="是否新增子任务?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="addNewTask"
            @cancel="cancel"
          >
            <PlusSquareOutlined class="ps-icon" />
          </a-popconfirm>
        </div>
        <div v-for="(item, idx) in list" :key="idx" :class="getClass(idx)">
          <div class="task-title">
            <span
              class="subjobName"
              @click="changeCurTask(idx)"
              v-if="
                activeIndex !== idx || (activeIndex === idx && !nameEditable)
              "
              >{{ item.subjobName }}</span
            >
            <a-input
              @pressEnter="nameEditable = false"
              v-model:value="item.subjobName"
              v-if="activeIndex === idx && nameEditable"
            ></a-input>
            <EditOutlined
              @click="nameEditable = true"
              v-if="activeIndex === idx && !nameEditable"
            />
            <a-popconfirm
              title="是否删除子任务?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="deleteSub(idx)"
              @cancel="cancel"
            >
              <DeleteOutlined class="delete-icon" />
            </a-popconfirm>
            <a-popconfirm
              title="是否复制子任务?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="copySub(item)"
              @cancel="cancel"
            >
              <CopyOutlined class="copy-icon" />
            </a-popconfirm>
          </div>
          <template
            v-if="
              item.dataSourceIds &&
              item.dataSourceIds.source &&
              item.dataSourceIds.source.db
            "
          >
            <div
              class="sub-table"
              :title="
                item.dataSourceIds.source.db +
                '.' +
                item.dataSourceIds.source.table
              "
            >
              {{
                item.dataSourceIds.source.db +
                "." +
                item.dataSourceIds.source.table
              }}
            </div>
            <div class="arrow-down-icon"><ArrowDownOutlined /></div>
            <div
              class="sub-table"
              :title="
                item.dataSourceIds.sink.db + '.' + item.dataSourceIds.sink.table
              "
            >
              {{
                item.dataSourceIds.sink.db + "." + item.dataSourceIds.sink.table
              }}
            </div>
          </template>
        </div>
      </div>
      <div class="jd_right">
        <div>
          <DataSource
            v-bind:dsData="curTask"
            @updateDataSource="updateDataSource"
          />
        </div>
        <div>
          <FieldMap
            v-bind:fmData="curTask.transforms"
            @updateFieldMap="updateFieldMap"
          />
        </div>
        <div>
          <ProcessControl
            v-bind:psData="curTask.settings"
            @updateProcessControl="updateProcessControl"
          />
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
  EditOutlined,
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
    EditOutlined,
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
      activeIndex: -1,
      curTask: {},
      nameEditable: false,
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
        let data = await getJobInfo(this.curTab.id);
        data.content.subJobs.forEach((item) => {
          item.engineType = data.engineType;
        });
        this.jobData = data;
        this.list = this.jobData.content.subJobs;
        if (this.list.length) {
          this.activeIndex = 0;
          this.curTask = this.list[this.activeIndex];
          console.log("this.curTask", this.curTask);
        }
      } catch (error) {}
    },
    handleModalFinish() {},
    handleModalCopy(data) {
      if (data) {
        this.jobData.content.subJobs.push(data);
      }
      console.log(this.list, this.jobData.content.subJobs);
    },
    copySub(item) {
      this.copyObj = item;
      this.modalCopy.visible = true;
    },
    deleteSub(index) {
      this.jobData.content.subJobs.splice(index, 1);
      if (this.activeIndex === index && this.list.length) {
        this.activeIndex = 0;
        this.curTask = this.list[this.activeIndex];
      }
    },
    cancel() {},
    getClass(index) {
      if (this.activeIndex === index) {
        return "sub-content active";
      } else {
        return "sub-content";
      }
    },
    changeCurTask(index) {
      this.activeIndex = index;
      this.curTask = this.list[this.activeIndex];
      console.log("this.curTask", this.curTask);
    },
    addNewTask() {
      let task = {
        subjobName: "new",
        dataSourceIds: {
          source: {
            type: "",
            id: "",
            db: "",
            table: "",
          },
          sink: {
            type: "",
            id: "",
            db: "",
            table: "",
          },
        },
      };
      this.jobData.content.subJobs.push(task);
      this.activeIndex = this.jobData.content.subJobs.length - 1;
      this.curTask = this.list[this.activeIndex];
    },
    updateFieldMap(transforms) {
      this.curTask.transforms = transforms;
      console.log("curTask", this.curTask);
    },
    updateProcessControl(settings) {
      this.curTask.settings = settings;
      console.log("curTask", this.curTask);
    },
    updateDataSource(dataSource) {
      const { dataSourceIds, params } = dataSource;
      this.curTask.dataSourceIds = dataSourceIds;
      this.curTask.params = params;
      console.log("curTask", this.curTask);
    },
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
    > span {
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
        border: 1px solid rgba(155, 155, 155, 0.5);
        padding: 10px 15px;
        border-radius: 5px;
        position: relative;
        &.active {
          border: 1px solid rgb(102, 102, 255);
          .task-title {
            font-weight: bolder;
            .subjobName {
              color: rgb(102, 102, 255);
            }
          }
          .sub-table {
            color: rgb(22, 155, 213);
          }
        }
        .task-title {
          font-size: 15px;
          .subjobName {
            color: rgba(155, 155, 155, 0.5);
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
          color: rgba(155, 155, 155, 0.5);
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
        .mask {
          width: 100%;
          height: 100%;
          position: absolute;
          background-color: rgba(255, 255, 255, 0.6);
          top: 0;
          left: 0;
        }
      }
    }
    .jd_right {
      flex: 5;
    }
  }
}
</style>
