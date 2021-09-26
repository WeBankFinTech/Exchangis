<template>
  <div class="data-source-warp">
    <!-- left -->
    <div class="ds-l">
      <div class="main-header">
        <span class="main-header-label">数据源</span>
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

      <div class="main-content">
        <!-- left -->
        <div class="data-source-warp-l">
          <div class="data-source-warp-l-content">
            <a-form ref="formRef">
              <a-form-item label="数据源信息" name="dsInfo">
                <SelectDataSource
                  @updateDsInfo="updateSourceInfo"
                  v-bind:title="sourceTitle"
                />
              </a-form-item>
              <!-- 动态组件 -->
              <a-form-item
                v-for="item in dataSource.params.sources"
                :key="item.field"
                :label="item.label"
                :name="item.label"
                :rules="{
                  trigger: 'change',
                }"
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
            <a-form ref="formRef">
              <a-form-item ref="dsInfo2" label="数据源信息" name="dsInfo2">
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
import { defineComponent, ref, reactive, toRaw, watch } from "vue";
import SelectDataSource from "./selectDataSource";
import DyncRender from "./dyncRender.vue";
import { getSourceParams } from "@/common/service"

export default defineComponent({
  props: {
    dsData: Object,
  },
  emits: ["updateDataSource"],
  components: {
    SelectDataSource,
    DyncRender,
    RightCircleOutlined,
  },
  setup(props, context) {
    // 对象转标题
    const objToTitle = function (obj) {
      if (typeof obj !== "object") return "";
      const { type, db, table } = obj;
      return `${type}-数据源-${db}.${table}`;
    };

    let sourceTitle = ref(objToTitle(props.dsData.dataSourceIds.source))
    let sinkTitle = ref(objToTitle(props.dsData.dataSourceIds.sink))

    const dataSource = reactive({
      dataSourceIds: {
        source: props.dsData.dataSourceIds.source  || {},
        sink: props.dsData.dataSourceIds.sink || {}
      },
      params: {
        sources: props.dsData.params.sources || [],
        sinks: props.dsData.params.sinks || []
      }
    })

    watch(props.dsData, (newVal, oldVal) => {
      sourceTitle.value = objToTitle(newVal.dataSourceIds.source);
      sinkTitle.value = objToTitle(newVal.dataSourceIds.sink);
      dataSource.dataSourceIds = {
        source: newVal.dataSourceIds.source  || {},
        sink: newVal.dataSourceIds.sink || {}
      }
      dataSource.params = {
        sources: newVal.params.sources || [],
        sinks: newVal.params.sinks || []
      }
      console.log(newVal)
    })

    const formRef = ref();
    const createDataSoure = (source, sink, sourceParams, sinkParams) => {
      const dataSourceIds = Object.create(null);
      const params = Object.create(null);

      dataSourceIds.source = source;
      dataSourceIds.sink = sink;
      params.sources = sourceParams;
      params.sinks = sinkParams;

      return {
        dataSourceIds,
        params,
      };
    };
    const updateSourceInfo = (dsInfo) => {
      const info = dsInfo.split("-");
      dataSource.dataSourceIds.source.type = info[0];
      dataSource.dataSourceIds.source.db = info[1];
      dataSource.dataSourceIds.source.table = info[2];

      getSourceParams(dataSource.dataSourceIds.source.type).then(res => {
        dataSource.params.sources = res.uis || []
        context.emit("updateDataSource", dataSource);
      })
    };
    const updateSinkInfo = (dsInfo) => {
      const info = dsInfo.split("-");
      dataSource.dataSourceIds.sink.type = info[0];
      dataSource.dataSourceIds.sink.db = info[1];
      dataSource.dataSourceIds.sink.table = info[2];

      getSourceParams(dataSource.dataSourceIds.sink.type).then(res => {
        dataSource.params.sinks = res.uis || []
        context.emit("updateDataSource", dataSource);
      })
    };
    const updateSourceParams = (info) => {
      const _sourceParams = dataSource.params.sources.slice(0);
      _sourceParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      dataSource.params.sources = _sourceParams;
      context.emit("updateDataSource", dataSource);
    };
    const updateSinkParams = (info) => {
      const _sinkParams = dataSource.params.sinks.slice(0);
      _sinkParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      dataSource.params.sinks = _sinkParams;
      context.emit("updateDataSource", dataSource);
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
    };
  },
  watch: {},
});
</script>

<style lang="less" scoped>
.data-source-warp {
  width: 1100px;
  display: flex;
  margin-top: 15px;
}
.ds-l {
  width: 122px;
  .main-header {
    height: 33px;
    background: inherit;
    background-color: rgba(102, 102, 255, 1);
    border: none;
    display: flex;
    border-top-left-radius: 16px;
    border-bottom-left-radius: 16px;
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
      width: 100%;
      text-align: center;
      line-height: 33px;
      font-size: 16px;
    }
    .main-header-label {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      color: #ffffff;
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
  width: 172px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.data-source-label {
  font-size: 14px;
  text-align: left;
}
</style>
