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
                v-for="item in sourceParams"
                :key="item.field"
                :label="item.label"
                :name="item.label"
                :rules="{
                  required: item.required,
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
                v-for="item in sinkParams"
                :key="item.field"
                :label="item.label"
                :name="item.label"
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
import { defineComponent, ref, reactive, toRaw } from "vue";
import SelectDataSource from "./selectDataSource";
import DyncRender from "./dyncRender.vue";
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
    console.log(props.dsData.dataSourceIds.source)
    let sourceTitle = objToTitle(props.dsData.dataSourceIds.source);
    let sinkTitle = objToTitle(props.dsData.dataSourceIds.sink);

    let { source, sink } = props.dsData.dataSourceIds;

    let sourceParams = props.dsData.params.sources;
    let sinkParams = props.dsData.params.sinks;

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
      source.type = info[0];
      source.db = info[1];
      source.table = info[2];

      const dataSource = createDataSoure(
        source,
        sink,
        sourceParams,
        sinkParams
      );
      context.emit("updateDataSource", dataSource);
    };
    const updateSinkInfo = (dsInfo) => {
      const info = dsInfo.split("-");
      sink.type = info[0];
      sink.db = info[1];
      sink.table = info[2];

      const dataSource = createDataSoure(
        source,
        sink,
        sourceParams,
        sinkParams
      );
      context.emit("updateDataSource", dataSource);
    };
    const updateSourceParams = (info) => {
      const _sourceParams = toRaw(sourceParams).slice(0);
      _sourceParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      sourceParams = _sourceParams;

      const dataSource = createDataSoure(
        source,
        sink,
        sourceParams,
        sinkParams
      );
      context.emit("updateDataSource", dataSource);
    };
    const updateSinkParams = (info) => {
      const _sinkParams = toRaw(sinkParams).slice(0);
      _sinkParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      sinkParams = _sinkParams;

      const dataSource = createDataSoure(
        source,
        sink,
        sourceParams,
        sinkParams
      );
      context.emit("updateDataSource", dataSource);
    };

    return {
      formRef,
      updateSourceInfo,
      updateSinkInfo,
      sourceTitle,
      sinkTitle,
      sourceParams,
      sinkParams,
      updateSourceParams,
      updateSinkParams,
    };
  },
  watch: {
    dsData: {
      handler: function (newVal) {
        console.log("watch props");
        this.props = newVal;
      },
      deep: true,
    },
  },
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
