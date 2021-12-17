<template>
  <div class="data-source-warp">
    <!-- left -->
    <div class="ds-l">
      <div class="main-header">
        <img src="../../../images/jobDetail/u2664.png" />
        <img
          src="../../../images/jobDetail/u2666.png"
          style="
            width: 25px;
            height: 25px;
            position: absolute;
            left: 16px;
            top: 3px;
          "
        />
        <span class="main-header-label" @click="showInfo">数据源</span>
      </div>
    </div>
    <!-- right -->
    <div class="ds-r">
      <div class="main-header">
        <div>
          <span class="main-header-label">数据来源</span>
        </div>
        <div>
          <span class="main-header-label">数据目的</span>
        </div>
      </div>

      <div class="main-content" v-show="isFlod">
        <!-- left -->
        <div class="data-source-warp-l">
          <div class="data-source-warp-l-content">
            <a-form ref="formRef" :label-col="labelCol">
              <a-form-item
                label="数据源信息"
                name="dsInfo"
                width="300"
                class="source-title"
              >
                <SelectDataSource
                  @updateDsInfo="updateSourceInfo"
                  :title="sourceTitle"
                />
              </a-form-item>
              <!-- 动态组件 -->
              <a-form-item
                v-for="item in dataSource.params.sources"
                :key="item.field"
                :label="item.label"
                :name="item.label"
                :help="sourcesHelpMsg[item.key.split('.').pop()]"
                :validate-status="sourcesHelpStatus[item.key.split('.').pop()]"
                :required="item.required"
              >
                <dync-render
                  v-bind:param="item"
                  @updateInfo="updateSourceParams"
                />
              </a-form-item>
            </a-form>
          </div>
        </div>

        <!-- mid -->

        <div class="data-source-warp-mid">
          <RightCircleOutlined style="font-size: 50px; color: #66f" />
        </div>

        <!-- right -->

        <div class="data-source-warp-r">
          <div class="data-source-warp-r-content">
            <a-form ref="formRef" :label-col="labelCol">
              <a-form-item
                ref="dsInfo2"
                label="数据源信息"
                name="dsInfo2"
                class="source-title"
              >
                <SelectDataSource
                  @updateDsInfo="updateSinkInfo"
                  :title="sinkTitle"
                />
              </a-form-item>
              <!-- 动态组件 -->
              <a-form-item
                v-for="item in dataSource.params.sinks"
                :key="item.field"
                :label="item.label"
                :name="item.label"
                :help="sinksHelpMsg[item.key.split('.').pop()]"
                :validate-status="sinksHelpStatus[item.key.split('.').pop()]"
                :required="item.required"
              >
                <dync-render
                  v-bind:param="item"
                  @updateInfo="updateSinkParams"
                />
              </a-form-item>
            </a-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { RightCircleOutlined } from "@ant-design/icons-vue";
import {
  defineComponent,
  ref,
  reactive,
  toRaw,
  watch,
  computed,
  defineAsyncComponent,
} from "vue";
import { getSourceParams, getSettingsParams } from "@/common/service";
import { message, notification } from "ant-design-vue";

export default defineComponent({
  props: {
    dsData: Object,
    engineType: String,
  },
  emits: [
    "updateSourceInfo",
    "updateSinkInfo",
    "updateSourceParams",
    "updateSinkParams",
  ],
  components: {
    SelectDataSource: defineAsyncComponent(() =>
      import("./selectDataSource.vue")
    ),
    DyncRender: defineAsyncComponent(() => import("./dyncRender.vue")),
    RightCircleOutlined,
  },
  setup(props, context) {
    // 对象转标题
    const objToTitle = function (obj) {
      if (typeof obj !== "object") return "";
      const { type, db, table, ds } = obj;
      if (!type && !db && !table && !ds) return "请点击后选择";
      return `${type}-${ds}-${db}-${table}`;
    };

    let sourceTitle = ref(objToTitle(props.dsData.dataSourceIds.source));
    let sinkTitle = ref(objToTitle(props.dsData.dataSourceIds.sink));
    let isFlod = ref(true);

    const dataSource = reactive({
      dataSourceIds: {
        source: props.dsData.dataSourceIds.source || {},
        sink: props.dsData.dataSourceIds.sink || {},
      },
      params: {
        sources: props.dsData.params.sources || [],
        sinks: props.dsData.params.sinks || [],
      },
    });

    const sourcesHelpMsg = reactive({});
    const sourcesHelpStatus = reactive({});

    const sinksHelpMsg = reactive({});
    const sinksHelpStatus = reactive({});

    dataSource["params"]["sources"].forEach((item) => {
      let key = item.key.split(".").pop();
      sourcesHelpMsg[key] = "";
      sourcesHelpStatus[key] = "success";
    });

    dataSource["params"]["sinks"].forEach((item) => {
      let key = item.key.split(".").pop();
      sinksHelpMsg[key] = "";
      sinksHelpStatus[key] = "success";
    });

    const newProps = computed(() => JSON.parse(JSON.stringify(props.dsData)));
    watch(newProps, (val, oldVal) => {
      const newVal = typeof val === "string" ? JSON.parse(val) : val;
      sourceTitle.value = objToTitle(newVal.dataSourceIds.source);
      sinkTitle.value = objToTitle(newVal.dataSourceIds.sink);
      dataSource.dataSourceIds = {
        source: newVal.dataSourceIds.source || {},
        sink: newVal.dataSourceIds.sink || {},
      };
      dataSource.params = {
        sources: newVal.params.sources || [],
        sinks: newVal.params.sinks || [],
      };
    });

    const formRef = ref();
    const updateSourceInfo = (dsInfo, id) => {
      const info = dsInfo.split("-");
      if (dataSource.dataSourceIds.sink.type === info[0]) {
        sourceTitle.value = objToTitle({
          type: info[0],
          id: "",
          db: "",
          table: "",
          ds: "",
        })
        return message.error('SQOOP引擎输入/输出数据源必须包含HIVE,请重新选择')
      }
      dataSource.dataSourceIds.source.type = info[0];
      dataSource.dataSourceIds.source.ds = info[1];
      dataSource.dataSourceIds.source.db = info[2];
      dataSource.dataSourceIds.source.table = info[3];
      dataSource.dataSourceIds.source.id = id;

      getSourceParams(
        props.engineType,
        dataSource.dataSourceIds.source.type,
        "source"
      ).then((res) => {
        res.uis.forEach(ui => {
          if (!ui.value && ui.defaultValue) {
            ui.value = ui.defaultValue
          }
        })
        dataSource.params.sources = res.uis || [];
        context.emit("updateSourceInfo", dataSource);
      });
    };
    const updateSinkInfo = (dsInfo, id) => {
      const info = dsInfo.split("-");
      if (dataSource.dataSourceIds.source.type === info[0]) {
        sinkTitle.value = objToTitle({
          type: info[0],
          id: "",
          db: "",
          table: "",
          ds: ""
        })
        return message.error('SQOOP引擎输入/输出数据源必须包含HIVE,请重新选择')
      }
      dataSource.dataSourceIds.sink.type = info[0];
      dataSource.dataSourceIds.sink.ds = info[1];
      dataSource.dataSourceIds.sink.db = info[2];
      dataSource.dataSourceIds.sink.table = info[3];
      dataSource.dataSourceIds.sink.id = id;

      getSourceParams(
        props.engineType,
        dataSource.dataSourceIds.sink.type,
        "sink"
      ).then((res) => {
        res.uis.forEach(ui => {
          if (!ui.value && ui.defaultValue) {
            ui.value = ui.defaultValue
          }
        })
        dataSource.params.sinks = res.uis || [];
        context.emit("updateSinkInfo", dataSource);
      });
    };
    const updateSourceParams = (info) => {
      const _sourceParams = dataSource.params.sources.slice(0);
      let _key = info.key.split(".").pop();
      if (info.required && !info.value) {
        sourcesHelpMsg[_key] = `请输入${info.label}`;
        sourcesHelpStatus[_key] = "error";
      } else if (info.validateType === 'REGEX') {
        const num_reg = new RegExp(`${info.validateRange}`)
        if (!num_reg.test(info.value)) {
          sourcesHelpMsg[info.key] = info.validateMsg
          sourcesHelpStatus[info.key] = "error";
        } else {
          sourcesHelpMsg[info.key] = "";
          sourcesHelpStatus[info.key] = "success";
        }
      } else {
        sourcesHelpMsg[_key] = "";
        sourcesHelpStatus[_key] = "success";
      }
      /*switch (_key) {
        case "batch_size":
          if (!/^[1-9]*$/.test(info.value)) {
            sourcesHelpMsg[_key] = "请正确输入批量大小";
            sourcesHelpStatus[_key] = "error";
          } else {
            sourcesHelpMsg[_key] = "";
            sourcesHelpStatus[_key] = "success";
          }
          break;
        case "percentage":
          if (_key == "percentage") {
            if (!/^[0-9]*$/.test(info.value)) {
              sourcesHelpMsg[_key] = "请正确输入脏数据占比阈值";
              sourcesHelpStatus[_key] = "error";
            } else {
              sourcesHelpMsg[_key] = "";
              sourcesHelpStatus[_key] = "success";
            }
          }
          break;
      }*/

      _sourceParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });

      dataSource.params.sources = _sourceParams;
      context.emit("updateSourceParams", dataSource);
    };
    const updateSinkParams = (info) => {
      const _sinkParams = dataSource.params.sinks.slice(0);
      let _key = info.key.split(".").pop();
      if (info.required && !info.value) {
        sinksHelpMsg[_key] = `请输入${info.label}`;
        sinksHelpStatus[_key] = "error";
      } else if (info.validateType === 'REGEX') {
        const num_reg = new RegExp(`${info.validateRange}`)
        if (!num_reg.test(info.value)) {
          sinksHelpMsg[info.key] = `请正确输入${info.label}`;
          sinksHelpStatus[info.key] = "error";
        } else {
          sinksHelpMsg[info.key] = "";
          sinksHelpStatus[info.key] = "success";
        }
      } else {
        sinksHelpMsg[_key] = "";
        sinksHelpStatus[_key] = "success";
      }
      /*if (!info.value) {
        sinksHelpMsg[_key] = `请正确输入${info.label}`;
        sinksHelpStatus[_key] = "error";
      } else {
        sinksHelpMsg[_key] = "";
        sinksHelpStatus[_key] = "success";
      }
      switch (_key) {
        case "batch_size":
          if (!/^[0-9]*$/.test(info.value)) {
            sinksHelpMsg[_key] = "请正确输入批量大小";
            sinksHelpStatus[_key] = "error";
          } else {
            sinksHelpMsg[_key] = "";
            sinksHelpStatus[_key] = "success";
          }
          break;
        case "percentage":
          if (_key == "percentage") {
            if (!/^[0-9]*$/.test(info.value)) {
              sinksHelpMsg[_key] = "请正确输入脏数据占比阈值";
              sinksHelpStatus[_key] = "error";
            } else {
              sinksHelpMsg[_key] = "";
              sinksHelpStatus[_key] = "success";
            }
          }
          break;
      }*/
      _sinkParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      dataSource.params.sinks = _sinkParams;
      context.emit("updateSinkParams", dataSource);
    };
    const showInfo = () => {
      isFlod.value = !isFlod.value;
    };
    return {
      formRef,
      updateSourceInfo,
      updateSinkInfo,
      sourceTitle,
      sinkTitle,
      dataSource,
      updateSourceParams,
      updateSinkParams,
      showInfo,
      isFlod,

      sourcesHelpMsg,
      sourcesHelpStatus,
      sinksHelpMsg,
      sinksHelpStatus,

      labelCol: {
        style: {
          width: "120px",
          "text-align": "start",
        },
      },
    };
  },
  watch: {},
});
</script>

<style lang="less" scoped>
.data-source-warp {
  // width: 1215px;
  display: flex;
  margin-top: 15px;
}
.ds-l {
  width: 122px;
  .main-header {
    height: 33px;
    background: inherit;
    border: none;
    display: flex;
    border-top-left-radius: 100%;
    border-bottom-left-radius: 100%;
    background-color: #6b6b6b;
    position: relative;
    /*&::before {
      content: "";
      position: absolute;
      width: 16px;
      height: 33px;
      background-color: #66f;
      border-top-right-radius: 16px;
      border-bottom-right-radius: 16px;
      right: 962px;
    }*/
    :nth-of-type(1) {
      text-align: center;
      line-height: 33px;
      font-size: 16px;
    }
    .main-header-label {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      color: #ffffff;
      position: absolute;
      left: 46px;
    }
  }
}
.ds-r {
  flex: 1;
  display: flex;
  background: inherit;
  background-color: rgba(255, 255, 255, 1);
  box-sizing: border-box;
  border-top: none;
  flex-direction: column;
  .main-header {
    height: 33px;
    background: inherit;
    background-color: rgba(107, 107, 107, 1);
    border: none;
    display: flex;
    > div {
      flex: 1;
      text-align: center;
      line-height: 33px;
    }
    .main-header-label {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      color: #ffffff;
    }
  }
  .main-content {
    border: 1px solid rgba(102, 102, 255, 1);
    border-top: none;
    padding: 25px 30px;
    display: flex;
  }
}
.data-source-warp-l {
  flex: 1;
  .ds-warp-l-c-item {
    > div {
      display: inline;
    }
  }
}
.data-source-warp-r {
  flex: 1;
}
.data-source-warp-mid {
  width: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.data-source-label {
  font-size: 14px;
  text-align: left;
}
.source-title {
  ::v-deep .ant-form-item-label {
    width: 100%;
    text-align: left;
  }
}
</style>
