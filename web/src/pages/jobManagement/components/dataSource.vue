<template>
  <div class="data-source-warp">
    <!-- left -->
    <div class="ds-l">
      <span>数据源</span>
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
                  @updateDsInfo="updateDsInfo"
                  v-bind:title="sourceTitle"
                />
              </a-form-item>
              <!-- 动态组件 -->
              <a-form-item
                v-for="item in sourceParams"
                :key="item.field"
                :label="item.label"
                :name="item.label"
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
        <div class="data-source-warp-mid"></div>

        <!-- right -->
        <div class="data-source-warp-r">
          <div class="data-source-warp-r-content">
            <a-form ref="formRef">
              <a-form-item ref="dsInfo2" label="数据源信息" name="dsInfo2">
                <SelectDataSource
                  @updateDsInfo="updateDsInfo2"
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
import { defineComponent, ref, reactive, toRaw } from "vue";
import SelectDataSource from "./selectDataSource";
import DyncRender from "./dyncRender.vue";
export default defineComponent({
  props: {
    dsData: Object,
  },
  components: {
    SelectDataSource,
    DyncRender,
  },
  setup(props, context) {
    // 对象转标题
    const objToTitle = function (obj) {
      if (typeof obj !== "object") return "";
      const { type, db, table } = obj;
      return `${type}-数据源-${db}.${table}`;
    };
    const sourceTitle = objToTitle(props.dsData.dataSourceIds.source);
    const sinkTitle = objToTitle(props.dsData.dataSourceIds.sink);

    let sourceParams = props.dsData.params.sources;
    let sinkParams = props.dsData.params.sinks;

    const formRef = ref();
    const updateDsInfo = (dsInfo) => {
      formState.dsInfo = dsInfo;
      console.log(formState);
    };
    const updateDsInfo2 = (dsInfo) => {
      formState2.dsInfo = dsInfo;
      console.log(formState2);
    };
    const updateSourceParams = (info) => {
      const _sourceParams = toRaw(sourceParams).slice(0);
      _sourceParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      sourceParams = _sourceParams;
      console.log("sourceParams", sourceParams);
    };
    const updateSinkParams = (info) => {
      const _sinkParams = toRaw(sinkParams).slice(0);
      _sinkParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      sinkParams = _sinkParams;
      console.log("sinkParams", sinkParams);
    };
    return {
      formRef,
      updateDsInfo,
      updateDsInfo2,
      sourceTitle,
      sinkTitle,
      sourceParams,
      sinkParams,
      updateSourceParams,
      updateSinkParams,
    };
  },
});
</script>

<style lang="less" scoped>
.data-source-warp {
  width: 1100px;
  display: flex;
}
.ds-l {
  width: 122px;
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
}
.data-source-label {
  font-size: 14px;
  text-align: left;
}
</style>
