<template>
  <div class="data-source-warp">
    <!-- left -->
    <!-- <div class="ds-l">
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
    </div> -->
    <!-- right -->
    <div class="ds-r">
      <div class="main-header" @click="showInfo">
        <span style="margin-right: 8px; color: rgba(0, 0, 0, 0.45)">
          <RightOutlined v-if="!isFold" />
          <DownOutlined v-else />
        </span>
        <span>数据源</span>
        <!-- <div>
          <span class="main-header-label">数据来源</span>
        </div>
        <div>
          <span class="main-header-label">数据目的</span>
        </div> -->
      </div>

      <div class="main-content" v-show="isFold">
        <!-- left -->
        <div class="data-source-warp-l">
          <div class="data-source-warp-l-content">
            <a-form
              ref="formRef"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
            >
              <a-form-item name="dsInfo">
                <SelectDataSource
                  key="source"
                  @updateDsInfo="updateSourceInfo"
                  :title="sourceTitle"
                  :engineType="engineType"
                  :projectId="projectId"
                  direct="source"
                />
              </a-form-item>
              <!-- 动态组件 -->
              <a-form-item
                v-for="item in sourceParams"
                :key="item.field"
                :label="item.label"
                :name="item.label"
                :help="sourcesHelpMsg[item.key.split('.').pop()]"
                :validate-status="sourcesHelpStatus[item.key.split('.').pop()]"
                :required="item.required"
                :class="[sourcesHelpStatus[item.key.split('.').pop()] === 'success' ? 'form-item-has-success' : '']"
              > 
                <dync-render
                  v-bind:param="item"
                  @updateInfo="updateSourceParams"
                  :style="styleObject"
                  :data="dataSource.dataSourceIds.source"
                  :tableNotExist="srcTBNotExist"
                />
              </a-form-item>
            </a-form>
          </div>
        </div>

        <!-- mid -->

        <div class="data-source-warp-mid">
          <span
            class="iconfont icon-jiantou"
            style="color: #99b0d0; font-size: 24px"
          ></span>
        </div>

        <!-- right -->

        <div class="data-source-warp-r">
          <div class="data-source-warp-r-content">
            <a-form
              ref="formRef"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              v-if="sourceTitle !== '请点击后选择'"
            >
              <a-form-item name="dsInfo2">
                <SelectDataSource
                  key="sink"
                  @updateDsInfo="updateSinkInfo"
                  :title="sinkTitle"
                  :style="styleObject"
                  :engineType="engineType"
                  direct="sink"
                  :sourceType="sourceType"
                  :projectId="projectId"
                />
              </a-form-item>
              <!-- 动态组件 -->
              <a-form-item
                v-for="item in sinksParams"
                :key="item.field"
                :label="item.label"
                :name="item.label"
                :help="sinksHelpMsg[item.key.split('.').pop()]"
                :validate-status="sinksHelpStatus[item.key.split('.').pop()]"
                :required="item.required"
                :class="[sinksHelpStatus[item.key.split('.').pop()] === 'success' ? 'form-item-has-success' : '']"
              >
                <dync-render
                  v-bind:param="item"
                  @updateInfo="updateSinkParams"
                  :style="styleObject"
                  :data="dataSource.dataSourceIds.sink"
                  :tableNotExist="sinkTBNotExist"
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
import {
  RightCircleOutlined,
  DownOutlined,
  RightOutlined,
  ArrowRightOutlined,
} from "@ant-design/icons-vue";
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
    projectId: String
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
    RightOutlined,
    DownOutlined,
    ArrowRightOutlined,
  },
  setup(props, context) {
    // 对象转标题
    const objToTitle = function (obj) {
      if (typeof obj !== "object") return "";
      const { type, db, table, name } = obj;
      if (!type && !db && !table && !name) return "请点击后选择";
      return [type, name, db, table];
    };

    let sourceTitle = ref(objToTitle(props.dsData.dataSourceIds.source));
    let sinkTitle = ref(objToTitle(props.dsData.dataSourceIds.sink));
    let sourceType = ref(props.dsData.dataSourceIds.source.type) // 源数据的数据源类型
    let sinkType = ref(props.dsData.dataSourceIds.sink.type) // 源数据的数据源类型
    let isFold = ref(true);
    const srcTBNotExist = ref(props.dsData.dataSourceIds.source.tableNotExist);
    const sinkTBNotExist = ref(props.dsData.dataSourceIds.sink.tableNotExist);

    const dataSource = reactive({
      dataSourceIds: {
        source: { ...props.dsData.dataSourceIds.source },
        sink: { ...props.dsData.dataSourceIds.sink }
      },
      params: {
        sources: [ ...props.dsData.params.sources ],
        sinks: [ ...props.dsData.params.sinks ]
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

    // 
    const setParamSource = (arr) => {
      let transferModeValue = ''
      let defShow  = arr.filter(v => !v.refId)
      arr.forEach(ui => {
        ui.show = '_true'
        if (ui.field === "transferMode") {
          transferModeValue = ui.value + ui.id;
        }
        if (ui.refId) {
          ui.show = defShow.some(v => v.id === ui.refId) ? '_true' : '_false';
          if (ui.field === "nullFormat") {
            ui.show = transferModeValue === ("记录" + ui.refId) ? '_true' : '_false';
          }
          ui.value = ui.show !== '_false' ? ui.value : ''
        }
      })
    }

    setParamSource(dataSource.params.sources)

    const setParamSink = (arr) => {
      arr.forEach(ui => {
          ui.show = '_true';
          if (ui.field === "nullFormat" && sinkType.value === 'ELASTICSEARCH') {
            ui.show = '_false';
          }
          ui.value = ui.show !== '_false' ? ui.value : ''
      })
    }

    setParamSink(dataSource.params.sinks)

    dataSource["params"]["sinks"].forEach((item) => {
      let key = item.key.split(".").pop();
      sinksHelpMsg[key] = "";
      sinksHelpStatus[key] = "success";
    });
  
    const newProps = computed(() => JSON.parse(JSON.stringify(props.dsData)))
    watch(newProps, (val, oldVal) => {
      const newVal = typeof val === "string" ? JSON.parse(val) : val;
      sourceTitle.value = objToTitle(newVal.dataSourceIds.source);
      sinkTitle.value = objToTitle(newVal.dataSourceIds.sink);
      sourceType.value = newVal.dataSourceIds.source.type; // 源数据源类型赋值
      sinkType.value = newVal.dataSourceIds.sink.type; // 目的数据源类型赋值
      srcTBNotExist.value = newVal.dataSourceIds.source.tableNotExist;
      sinkTBNotExist.value = newVal.dataSourceIds.sink.tableNotExist;
      dataSource.dataSourceIds = {
        source: newVal.dataSourceIds.source || {},
        sink: newVal.dataSourceIds.sink || {}
      };
      dataSource.params = {
        sources: newVal.params.sources || [],
        sinks: newVal.params.sinks || []
      };
      setParamSource(dataSource.params.sources);
      setParamSink(dataSource.params.sinks);
      console.log('数据源信息', newProps)
    }, { deep: true });

    const formRef = ref();
    // 选完
    const updateSourceInfo = (dsInfo, dsItem, tableNotExist) => {
      const info = dsInfo.split(".");

      // 修改来源数据源，清空目的数据源
      dataSource.dataSourceIds.sink = {
        type: '',
        id: '',
        db: '',
        table: '',
        name: '',
        creator: ''
      }
      sinkTitle.value = objToTitle({
        type: '',
        id: '',
        db: '',
        table: '',
        name: '',
      })
      dataSource.params.sinks = []
      /*if ((dataSource.dataSourceIds.sink.type && dataSource.dataSourceIds.source.type !== 'HIVE')
        && (info[0] && info[0] !== 'HIVE')
        && props.engineType === 'SQOOP') {
        sourceTitle.value = objToTitle({
          type: info[0],
          id: "",
          db: "",
          table: "",
          ds: "",
        });
        return message.error("SQOOP引擎输入/输出数据源必须包含HIVE,请重新选择");
      }*/
      sourceType.value = info[0];
      srcTBNotExist.value = tableNotExist;
      dataSource.dataSourceIds.source.tableNotExist = tableNotExist;
      dataSource.dataSourceIds.source.type = info[0];
      dataSource.dataSourceIds.source.name = info[1];
      dataSource.dataSourceIds.source.db = info[2];
      dataSource.dataSourceIds.source.table = info[3];
      dataSource.dataSourceIds.source.id = dsItem.id;
      dataSource.dataSourceIds.source.creator = dsItem.createUser;
      getSourceParams(
        props.engineType,
        dataSource.dataSourceIds.source.type,
        "source"
      ).then((res) => {
        let transferModeValue = '';
        let defShow = res.uis.filter(v => !v.refId);
        res.uis.forEach((ui, index) => {
          /*if (ui.type && ui.type === 'MAP') {
            res.uis[index] = ui = {
              key: 'partition.source',
              field: 'partition',
              label: '分区信息',
              sort: 2,
              value: null,
              defaultValue: null,
              unit: '',
              required: false,
              validateType: 'REGEX',
              validateRange: '',
              validateMsg: 'xxx',
              source: '/api/rest_j/v1/exchangis/job/partitionInfo',
              type: 'MAP'
            }
          }
          if (ui.source) {
            ui.source = ui.source + '?_=' + Math.random()
          }*/
          ui.show = '_true'
          if (!ui.value && ui.defaultValue) {
            ui.value = ui.defaultValue;
          }
          if (ui.field === "transferMode" && ui.id ) {
            transferModeValue = ui.value + ui.id;
          }
          if (ui.refId) {
            ui.show = defShow.some(v => v.id === ui.refId) ? '_true' : '_false';
            if (ui.field === "nullFormat") {
              ui.show = transferModeValue === ("记录" + ui.refId)  ? '_true' : '_false';
            }
            ui.value = ui.show !== '_false' ? ui.value : ''
          }
        });
        dataSource.params.sources = res.uis || [];
        context.emit("updateSourceInfo", dataSource);
      });
    };
    const updateSinkInfo = (dsInfo, dsItem, tableNotExist) => {
      const info = dsInfo.split(".");
      if ((info[0] && info[0] !== 'HIVE')
        && (dataSource.dataSourceIds.source.type && dataSource.dataSourceIds.source.type !== 'HIVE')
        && props.engineType === 'SQOOP') {
        sinkTitle.value = objToTitle({
          type: info[0],
          id: "",
          db: "",
          table: "",
          name: "",
        });
        return message.error("SQOOP引擎输入/输出数据源必须包含HIVE,请重新选择");
      }
      sinkType.value = info[0];
      sinkTBNotExist.value = tableNotExist;
      dataSource.dataSourceIds.sink.tableNotExist = tableNotExist;
      dataSource.dataSourceIds.sink.type = info[0];
      dataSource.dataSourceIds.sink.name = info[1];
      dataSource.dataSourceIds.sink.db = info[2];
      dataSource.dataSourceIds.sink.table = info[3];
      dataSource.dataSourceIds.sink.id = dsItem.id;
      dataSource.dataSourceIds.sink.creator = dsItem.createUser;

      getSourceParams(
        props.engineType,
        dataSource.dataSourceIds.sink.type,
        "sink"
      ).then((res) => {
        res.uis.forEach((ui) => {
          ui.show = '_true';
          if (!ui.value && ui.defaultValue) {
            ui.value = ui.defaultValue;
          }
          if (ui.field === "nullFormat" && sinkType.value === 'ELASTICSEARCH') {
            ui.show = '_false';
          }
          ui.value = ui.show !== '_false' ? ui.value : ''
        });
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
      } else if (info.validateType === "REGEX") {
        const num_reg = new RegExp(`${info.validateRange}`);
        if (!num_reg.test(info.value)) {
          sourcesHelpMsg[info.key] = info.validateMsg;
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
      let transferModeValue = ''
      let defShow = _sourceParams.filter(v => !v.refId); // 默认显示项
      _sourceParams.forEach((item) => {
        item.show = '_true';
        if (item.field === "transferMode" && item.id) {
          transferModeValue = item.value + item.id
        }
        if (item.refId) {
          item.show = defShow.some(v => v.id === item.refId) ? '_true' : '_false';
          if (item.field === "nullFormat") {
            item.show = transferModeValue === ("记录" + item.refId) ? '_true' : '_false';
          }
          item.value = item.show !== '_false' ? item.value : ''
        }
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
      } else if (info.validateType === "REGEX") {
        const num_reg = new RegExp(`${info.validateRange}`);
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
        item.show = '_true';
        if (item.field === "nullFormat" && sinkType.value === 'ELASTICSEARCH') {
          item.show = '_false';
        }
        item.value = item.show !== '_false' ? item.value : ''
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      dataSource.params.sinks = _sinkParams;
      context.emit("updateSinkParams", dataSource);
    };
    const showInfo = () => {
      isFold.value = !isFold.value;
    };

    // 源数据数组
    const sourceParams = computed(() => {
      const sources = dataSource.params.sources.filter(v => v.show !== '_false');
      return sources;
    })
    const sinksParams = computed(() => {
      const sinks = dataSource.params.sinks.filter(v => v.show !== '_false');
      return sinks;
    })
    return {
      formRef,
      updateSourceInfo,
      updateSinkInfo,
      sourceTitle,
      sinkTitle,
      dataSource,
      updateSourceParams,
      updateSinkParams,
      srcTBNotExist,
      sinkTBNotExist,
      showInfo,
      isFold,
      sourcesHelpMsg,
      sourcesHelpStatus,
      sinksHelpMsg,
      sinksHelpStatus,
      sourceParams,
      sinksParams,
      labelCol: {
        style: {
          style: {
            width: "417px",
            "text-align": "left",
          },
        },
      },
      wrapperCol: {
        style: {
          "text-align": "left",
        },
      },
      styleObject: {
        width: "400px",
      },
      sourceType
    };
  },
  watch: {},
});
</script>

<style lang="less" scoped>
.data-source-warp {
  display: flex;
  padding: 24px;
}
.ds-l {
  width: 122px;
  .main-header {
    height: 20px;
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
    height: 20px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
  .main-content {
    display: flex;
    padding: 24px;
    padding-bottom: 0px;
    min-width: 960px;
    max-width: 960px;
  }
}
.data-source-warp-l {
  flex: 1;
  .ds-warp-l-c-item {
    > div {
      display: inline;
    }
  }
  :deep(.ant-form-item-label) {
    width: 100%;
    text-align: left;
  }
}
.data-source-warp-r {
  flex: 1;
  :deep(.ant-form-item-label) {
    width: 100%;
    text-align: left;
  }
}
.data-source-warp-mid {
  width: 74px;
  height: 128px;
  line-height: 128px;
  text-align: center;
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
.form-item-has-success {
  :deep(.ant-input) {
    border-color: #d9d9d9;
  }
}
</style>
