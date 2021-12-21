<template>
  <div class="container">
    <div class="tools-bar">
      <span @click="modalCfg.visible = true"><SettingOutlined />配置</span>
      <div class="divider"></div>
      <span @click="executeTask"><CaretRightOutlined v-if="!spinning" />
        <a-spin :spinning="spinning"></a-spin> 执行</span>
      <div class="divider"></div>
      <span @click="saveAll()"><SaveOutlined />保存</span>
      <div class="divider"></div>
      <span @click="executeHistory"><HistoryOutlined />执行历史</span>
    </div>
    <div class="jd-content" v-if="list.length !== 0">
      <div class="jd_left">
          <div class="sub-title">
            <!--<DatabaseFilled class="database-icon" />-->
            <span>子任务列表</span>
            <!--<a-popconfirm
              title="是否新增子任务?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="addNewTask"
              @cancel="cancel"
            >
              <PlusSquareOutlined class="ps-icon" />
            </a-popconfirm>-->
          </div>
          <div v-for="(item, idx) in list" :key="idx" :class="getClass(idx)">
            <div class="task-title">
              <div
                class="subjobName"
                @click="changeCurTask(idx)"
                v-if="
                  activeIndex !== idx || (activeIndex === idx && !nameEditable)
                "
                :title="item.subJobName"
              >
                {{ item.subJobName }}
              </div>
              <a-input
                @pressEnter="nameEditable = false"
                v-model:value="item.subJobName"
                v-if="activeIndex === idx && nameEditable"
              ></a-input>
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
              <EditOutlined
                @click="nameEditable = true"
                v-if="activeIndex === idx && !nameEditable"
                class="rename-icon"
              />
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
          <a-button
            size="large"
            style="
              width: 218px;
              font-family: PingFangSC-Regular;
              font-size: 14px;
              line-height: 22px;
              font-weight: 400;
              border: 1px dashed #dee4ec;
            "
            @click="addNewTask"
          >
            <template #icon> <PlusOutlined /></template>添加子任务
          </a-button>
        </div>
      <div class="jd_right">
        <div>
          <DataSource
            v-if="curTask"
            v-bind:dsData="curTask"
            v-bind:engineType="curTask.engineType"
            @updateSourceInfo="updateSourceInfo"
            @updateSinkInfo="updateSinkInfo"
            @updateSourceParams="updateSourceParams"
            @updateSinkParams="updateSinkParams"
          />
        </div>
        <div>
          <FieldMap
            v-if="curTask"
            v-bind:fmData="curTask.transforms"
            v-bind:fieldsSink="fieldsSink"
            v-bind:fieldsSource="fieldsSource"
            v-bind:deductions="deductions"
            v-bind:addEnabled="addEnabled"
            v-bind:engineType="curTask.engineType"
            @updateFieldMap="updateFieldMap"
          />
        </div>
        <div>
          <ProcessControl
            v-if="curTask"
            v-bind:psData="curTask.settings"
            v-bind:engineType="curTask.engineType"
            @updateProcessControl="updateProcessControl"
          />
        </div>
      </div>
    </div>
    <div class="cardWrap" v-if="list.length === 0">
      <div class="emptyTab">
        <div class="void-page-wrap">
          <div class="void-page-main">
            <div class="void-page-main-img">
              <img src="../../../assets/img/void_page.png" alt="空页面" />
            </div>
            <div class="void-page-main-title">
              <span>该任务下没有子任务，请先创建一个子任务</span>
            </div>
            <div class="void-page-main-button">
              <a-button
                type="primary"
                size="large"
                style="
                  font-family: PingFangSC-Regular;
                  font-size: 14px;
                  line-height: 22px;
                  font-weight: 400;
                "
                @click="addNewTask"
              >
                <template #icon> <PlusOutlined /></template
                >{{ t("job.action.createJob") }}
              </a-button>
            </div>
          </div>
        </div>
      </div>
      <div v-for="item in streamList" :key="item.id" class="card">
        <job-card
          :jobData="item"
          type="STREAM"
          @showJobDetail="showJobDetail"
          @handleJobCopy="handleJobCopy"
          @refreshList="getJobs"
        />
      </div>
    </div>
    <!-- 执行历史  jd-bottom -->
    <div class="jd-bottom" v-show="visibleDrawer">
      <div class="jd-bottom-top" >
        <span>执行历史</span>
        <CloseOutlined
          style="
            color: rgba(0, 0, 0, 0.45);
            font-size: 12px;
            position: absolute;
            right: 24px;
            top: 18px;
            cursor: pointer;
          "
          @click="onCloseDrawer"
        />
      </div>
      <div class="jd-bottom-content">
        <a-table
          :columns="ehColumns"
          :data-source="ehTableData"
          :pagination="ehPagination"
          @change="onPageChange"
        >
        </a-table>
      </div>
    </div>

    <config-modal
      v-model:visible="modalCfg.visible"
      :id="modalCfg.id"
      :formData="configModalData"
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
import { toRaw, h, defineAsyncComponent } from "vue";
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
  MinusOutlined,
  PlusOutlined,
  CloseOutlined,
} from "@ant-design/icons-vue";
import {
  getJobInfo,
  saveProject,
  getSettingsParams,
  getFields,
  executeTask,
  updateTaskConfiguration,
  getSyncHistory,
  executeJob,
} from "@/common/service";
import { message, notification } from "ant-design-vue";
import { randomString } from "../../../common/utils";
import { useI18n } from "@fesjs/fes";

/**
 * 用于判断一个对象是否有空 value,如果有返回 true
 */
const objectValueEmpty = (obj) => {
  let isEmpty = false;
  Object.keys(obj).forEach((o) => {
    if (obj[o] == null || obj[o] == "" || obj[o] == undefined) {
      isEmpty = true;
    }
  });
  return isEmpty;
};

const formatDate = (d) => {
  let date = new Date(d);
  let YY = date.getFullYear() + "-";
  let MM =
    (date.getMonth() + 1 < 10
      ? "0" + (date.getMonth() + 1)
      : date.getMonth() + 1) + "-";
  let DD = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
  let hh =
    (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":";
  let mm =
    (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) +
    ":";
  let ss = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
  return YY + MM + DD + " " + hh + mm + ss;
};

const ehColumns = [
  {
    title: "ID",
    dataIndex: "id",
  },
  {
    title: "任务名称",
    dataIndex: "taskName",
  },
  {
    title: "触发时间",
    dataIndex: "launchTime",
  },
  {
    title: "创建用户",
    dataIndex: "createUser",
  },
  {
    title: "状态",
    dataIndex: "status",
  },
  {
    title: "完成时间",
    dataIndex: "completeTime",
  },
  // {
  //   title: "操作",
  //   dataIndex: "options",
  //   slots: {
  //     customRender: "operation",
  //   },
  // },
];

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
    PlusOutlined,
    "config-modal": defineAsyncComponent(() => import("./configModal.vue")),
    "copy-modal": defineAsyncComponent(() => import("./copyModal.vue")),
    DataSource: defineAsyncComponent(() => import("./dataSource.vue")),
    FieldMap: defineAsyncComponent(() => import("./fieldMap.vue")),
    ProcessControl: defineAsyncComponent(() => import("./processControl.vue")),
    MinusOutlined,
    CloseOutlined,
  },
  data() {
    const { t } = useI18n({ useScope: "global" });
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
      curTask: null,
      nameEditable: false,

      fieldsSource: [],
      fieldsSink: [],
      deductions: [],
      addEnabled: true,

      configModalData: {},

      visibleDrawer: false,
      pageSize: 10,
      currentPage: 1,
      ehColumns: ehColumns,
      ehTableData: [],
      ehPagination: {
        total: 0,
        pageSize: 10,
      },
      t,
      spinning: false
    };
  },
  props: {
    curTab: Object,
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
        let data = (await getJobInfo(this.curTab.id)).result;
        if (!data.content || data.content === "[]") {
          data.content = {
            subJobs: [],
          };
        } else {
          data.content = JSON.parse(data.content);
        }
        data.content.subJobs.forEach((item) => {
          item.engineType = data.engineType;
        });
        this.jobData = data;

        const configData = Object.create(null);
        configData["executeNode"] = data["executeNode"] || "";
        configData["proxyUser"] = data["proxyUser"] || "";
        configData["syncType"] = data["syncType"] || "";
        configData["jobParams"] = JSON.parse(data["jobParams"]);
        this.configModalData = configData;

        console.log("configData", configData);

        this.list = this.jobData.content.subJobs;
        if (this.list.length) {
          this.activeIndex = 0;
          this.curTask = this.list[this.activeIndex];
          this.updateSourceInfo(this.curTask);
          // this.updateSinkInfo(this.curTask); 当sink和source都有值的时候,请求的结果是一致的,所以省去一次多余重复请求
        }
      } catch (error) {}
    },
    // 更新保存任务配置
    handleModalFinish(config) {
      let _this = this;
      const { id } = this.curTab;
      const _config = Object.assign(
        {},
        JSON.parse(JSON.stringify(toRaw(config)))
      );
      if (_config.jobParams) {
        let jobParams = Object.create(null);
        _config.jobParams.forEach((item) => {
          // jobParams = Object.assign(jobParams, item);
          jobParams[item.key] = item.value;
        });
        _config.jobParams = JSON.stringify(jobParams);
      }
      console.log("_config", _config);
      updateTaskConfiguration(id, _config)
        .then((res) => {
          message.success("更新/保存成功");
          _this.jobData["proxyUser"] = _config["proxyUser"];
        })
        .catch((err) => {
          message.error("更新/保存失败");
          console.log("updateTaskConfiguration error", err);
        });
    },
    handleModalCopy(data) {
      if (data) {
        this.jobData.content.subJobs.push(data);
      }
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
      } else {
        this.activeIndex = -1;
        this.curTask = null;
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
    },
    addNewTask() {
      let subJobName = randomString(12);
      let task = {
        subJobName,
        engineType: this.jobData.engineType,
        dataSourceIds: {
          source: {
            type: "",
            id: "",
            db: "",
            table: "",
            ds: "",
          },
          sink: {
            type: "",
            id: "",
            db: "",
            table: "",
            ds: "",
          },
        },
        params: {
          sources: [],
          sinks: [],
        },
        transforms: {
          type: "MAPPING",
          mapping: [],
        },
        settings: [],
      };
      getSettingsParams(this.jobData.engineType).then((res) => {
        res.ui.forEach((ui) => {
          if (!ui.value && ui.defaultValue) {
            ui.value = ui.defaultValue;
          }
        });
        task.settings = res.ui || [];
        this.jobData.content.subJobs.push(task);
        this.$nextTick(() => {
          this.activeIndex = this.jobData.content.subJobs.length - 1;
          this.curTask = this.list[this.activeIndex];
          this.deductions = []
        });
      });
    },
    updateFieldMap(transforms) {
      console.log("update field map", transforms);
      this.curTask.transforms = transforms;
    },
    updateProcessControl(settings) {
      this.curTask.settings = settings;
    },
    getFieldsParams(dataSource) {
      const { dataSourceIds, params } = dataSource;
      this.curTask.dataSourceIds = dataSourceIds;
      this.curTask.params = params;
      const source = this.curTask.dataSourceIds.source;
      const sink = this.curTask.dataSourceIds.sink;
      if (!source.type || !source.id || !sink.type || !sink.id) return null;
      return {
        sourceTypeId: source.type,
        sourceDataSourceId: source.id,
        sourceDataBase: source.db,
        sourceTable: source.table,
        sinkTypeId: sink.type,
        sinkDataSourceId: sink.id,
        sinkDataBase: sink.db,
        sinkTable: sink.table,
        engine: this.jobData.engineType,
      };
    },
    updateSourceInfo(dataSource) {
      /*getFields(source.type, source.id, source.db, source.table).then((res) => {
       this.fieldsSource = res.columns;
       })*/
      const data = this.getFieldsParams(dataSource);
      if (data) {
        getFields(data).then((res) => {
          this.fieldsSource = res.sourceFields;
          this.fieldsSink = res.sinkFields;
          this.deductions = res.deductions;
          this.addEnabled = res.addEnabled;
        });
      }
    },
    updateSinkInfo(dataSource) {
      /*getFields(sink.type, sink.id, sink.db, sink.table).then((res) => {
        this.fieldsSink = res.columns;
      })*/
      const data = this.getFieldsParams(dataSource);
      if (data) {
        getFields(data).then((res) => {
          this.fieldsSource = res.sourceFields;
          this.fieldsSink = res.sinkFields;
          this.deductions = res.deductions;
          this.addEnabled = res.addEnabled;
        });
      }
    },
    updateSourceParams(dataSource) {
      const { params } = dataSource;
      this.curTask.params = params;
    },
    updateSinkParams(dataSource) {
      const { params } = dataSource;
      this.curTask.params = params;
    },
    // 提交前 对 必填数据进行校验
    checkPostData(data) {
      const res = [];
      const { proxyUser, content } = data;
      const jobs = content.subJobs.slice();
      if (!proxyUser) {
        res.push("配置任务中执行用户不可为空");
      }
      jobs.forEach((job) => {
        const { params, settings } = job;
        for (let key in params) {
          params[key].forEach((i) => {
            if (!i.value && i.required) {
              res.push(`${i.label}不可为空`);
            }
          });
        }

        settings.forEach((i) => {
          if (!i.value && i.required) {
            res.push(`${i.label}不可为空`);
          }
        });
      });

      return res;
    },
    saveAll() {
      let saveContent = [],
        data = toRaw(this.jobData);
      const tips = this.checkPostData(data);
      if (tips.length > 0) {
        return notification["warning"]({
          message: "任务信息未完整填写",
          description: h(
            "ul",
            {},
            tips.map((tip) => h("li", {}, tip))
          ),
          duration: 5,
        });
      }
      if (!data.content || !data.content.subJobs) {
        return message.error("缺失保存对象");
      }
      for (let i = 0; i < data.content.subJobs.length; i++) {
        const jobData = toRaw(data.content.subJobs[i]);
        let cur = {};
        cur.subJobName = jobData.subJobName;
        if (
          !jobData.dataSourceIds ||
          objectValueEmpty(jobData.dataSourceIds.source) ||
          objectValueEmpty(jobData.dataSourceIds.sink)
        ) {
          return message.error("未选择数据源库表");
        }
        cur.dataSources = {
          source_id: `${jobData.dataSourceIds.source.type}.${jobData.dataSourceIds.source.id}.${jobData.dataSourceIds.source.db}.${jobData.dataSourceIds.source.table}`,
          sink_id: `${jobData.dataSourceIds.sink.type}.${jobData.dataSourceIds.sink.id}.${jobData.dataSourceIds.sink.db}.${jobData.dataSourceIds.sink.table}`,
        };
        /*if (
          !jobData.params ||
          !jobData.params.sources.length ||
          !jobData.params.sinks.length
        ) {
          return message.error("缺失数据源参数");
        }*/
        cur.params = {
          sources: [],
          sinks: [],
        };
        jobData.params.sources.forEach((source) => {
          cur.params.sources.push({
            config_key: source.field, // UI中field
            config_name: source.label, // UI中label
            config_value: source.value, // UI中value
            sort: source.sort,
          });
        });
        jobData.params.sinks.forEach((source) => {
          cur.params.sinks.push({
            config_key: source.field, // UI中field
            config_name: source.label, // UI中label
            config_value: source.value, // UI中value
            sort: source.sort,
          });
        });
        cur.transforms = jobData.transforms;
        cur.settings = [];
        if (jobData.settings && jobData.settings.length) {
          jobData.settings.forEach((setting) => {
            cur.settings.push({
              config_key: setting.field, // UI中field
              config_name: setting.label, // UI中label
              config_value: setting.value, // UI中value
              sort: setting.sort,
            });
          });
        }
        saveContent.push(cur);
      }
      saveProject(this.jobData.id, {
        content: JSON.stringify(saveContent),
      }).then((res) => {
        message.success("保存成功");
      });
    },
    // 执行任务
    executeTask() {
      const { id } = this.curTab;
      this.spinning = true
      executeJob(id)
        .then((res) => {
          this.spinning = false
          message.info("执行成功");
        })
        .catch((err) => {
          this.spinning = false
          console.log("executeTask error", err);
        });
    },
    executeHistory() {
      this.visibleDrawer = true;
      this.getTableFormCurrent(1, "search");
    },
    getTableFormCurrent(current, type) {
      let _this = this;
      if (
        _this.ehTableData.length == _this.ehPagination.total &&
        _this.ehPagination.total > 0 &&
        type !== "search"
      )
        return;
      if (_this.currentPage == current && type !== "search") return;
      let jobId = _this.curTab.id || "";
      let _pageSize = _this.pageSize;
      getSyncHistory({ jobId, current, size: _pageSize })
        .then((res) => {
          if (res.result.length > 0) {
            const result = res.result || [];
            result.forEach((item) => {
              item["launchTime"] = formatDate(item["launchTime"]);
              item["completeTime"] = formatDate(item["completeTime"]);
              switch (item["status"]) {
                case "SUCCESS":
                  item["status"] = "执行成功";
                  break;
                case "FAILED":
                  item["status"] = "执行失败";
                  break;
                case "RUNNING":
                  item["status"] = "运行中";
              }
              item["key"] = item["id"];
            });
            if (type == "search") {
              _this.TableData = [];
            }
            _this.ehPagination.total = res["total"];
            _this.ehTableData = _this.ehTableData.concat(result);
          }
        })
        .catch((err) => {
          console.log("syncHistory error", err);
        });
    },
    onPageChange(page) {
      const { current } = page;
      this.getTableFormCurrent(current, "onChange");
    },
    onCloseDrawer() {
      this.visibleDrawer = false;
    },
  },
};
</script>
<style scoped lang="less">
@import "../../../common/content.less";
.container {
  .tools-bar {
    width: 100%;
    border-bottom: 1px solid #dee4ec;
    background: #f8f9fc;
    padding: 10px 30px;
    font-size: 16px;
    color: rgba(0, 0, 0, 0.65);
    > span {
      cursor: pointer;
    }
    .anticon {
      margin-right: 5px;
    }
    .divider {
      width: 1px;
      height: 20px;
      background: #dee4ec;
      margin-left: 20px;
      margin-right: 20px;
      display: inline-block;
      position: relative;
      top: 5px;
    }
  }
  .jd-content {
    overflow: hidden;
    width: 100%;
    /*height: calc(100vh - 130px);*/
    .jd_left {
      float: left;
      width: 250px;
      padding: 0 15px;
      background-color: #f8f9fc;
      border-right: 1px solid #dee4ec;
      padding-bottom: 2000px;
      margin-bottom: -2000px;
      .sub-title {
        font-size: 14px;
        margin-top: 16px;
        font-weight: 500;
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
        width: 218px;
        margin: 10px 0;
        border: 1px solid #dee4ec;
        padding: 10px 15px 5px;
        border-radius: 5px;
        position: relative;
        &.active {
          border: 1px solid #2e92f7;
          .task-title {
            .subjobName {
              color: rgba(0, 0, 0, 0.85);
            }
          }
          .sub-table {
            color: #677c99;
          }
        }
        .task-title {
          font-size: 15px;
          .subjobName {
            color: rgba(0, 0, 0, 0.85);
            cursor: pointer;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            max-width: 115px;
            display: inline-block;
          }
          .rename-icon {
            float: right;
            cursor: pointer;
            margin-right: 10px;
            line-height: 28px;
            color: rgba(0, 0, 0, 0.5);
          }
          .copy-icon {
            float: right;
            cursor: pointer;
            margin-right: 10px;
            line-height: 28px;
            color: rgba(0, 0, 0, 0.5);
          }
          .delete-icon {
            float: right;
            cursor: pointer;
            margin-right: 0;
            line-height: 28px;
            color: rgba(0, 0, 0, 0.5);
          }
        }
        .sub-table {
          color: #677c99;
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
          color: #677c99;
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
      overflow-x: auto;
      float: right;
      width: calc(100% - 250px);
    }
  }

  .jd-bottom {
    overflow: auto;
    width: calc(100% - 200px);
    position: fixed;
    height: 30%;
    bottom: 0;
    background-color: white;
    .jd-bottom-top {
      width: calc(100% - 200px);
      height: 48px;
      position: fixed;
      bottom: 30%;
      background-color: #f8f9fc;
      padding: 12px 24px;
      font-family: PingFangSC-Medium;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
      font-weight: 500;
    }

    &-content {
      padding: 18px 24px;
    }
  }
}

.cardWrap {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 30px;
  .emptyTab {
    font-size: 16px;
    height: calc(100vh - 130px);
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .card {
    margin: 10px 20px 10px 0px;
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
