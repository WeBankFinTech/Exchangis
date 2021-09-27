<template>
  <div class="process-control-warp">
    <!-- left -->
    <div class="ps-l">
      <div class="main-header">
        <span class="main-header-label">过程控制</span>
      </div>
    </div>
    <!-- right -->
    <div class="ps-r">
      <div class="main-header">
        <div>
          <span class="main-header-label">速度控制</span>
        </div>
      </div>

      <div class="main-content">
        <a-form ref="formRef">
          <!-- 动态组件 -->
          <a-form-item
            v-for="item in settingData.psData"
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
              @updateInfo="updateSettingParams"
            />
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, toRaw, watch, computed } from "vue";
import DyncRender from "./dyncRender.vue";
export default defineComponent({
  props: {
    psData: Array,
  },
  emits: ["updateProcessControl"],
  components: {
    DyncRender,
  },
  setup(props, context) {
    const formRef = ref();
    let settingData = reactive({
      psData: toRaw(props.psData)
    })

    const newProps = computed(() => JSON.parse(JSON.stringify(props.psData)))
    watch(newProps, (val, oldVal) => {
      settingData.psData = typeof val === 'string' ? JSON.parse(val): val
      console.log(val, 123)
    })
    const updateSettingParams = (info) => {
      const _settingParams = toRaw(settingData.psData).slice(0);
      _settingParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      settingData.psData = _settingParams;
      console.log("sourceParams", settingData.psData);
      context.emit("updateProcessControl", settingData.psData);
    };
    return {
      formRef,
      settingData,
      updateSettingParams,
    };
  }
});
</script>

<style lang="less" scoped>
.process-control-warp {
  margin-top: 30px;
  width: 1100px;
  display: flex;
}
.ps-l {
  width: 122px;
  .main-header {
    height: 33px;
    background: inherit;
    background-color: rgba(102, 102, 255, 1);
    border: none;
    display: flex;
    border-top-left-radius: 16px;
    border-bottom-left-radius: 16px;
    :nth-of-type(1) {
      width: 100%;
      text-align: center;
      line-height: 33px;
      font-size: 16px;
    }
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
    .main-header-label {
      font-family: "Arial Negreta", "Arial Normal", "Arial";
      font-weight: 700;
      font-style: normal;
      color: #ffffff;
    }
  }
}
.ps-r {
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
.process-control-label {
  font-size: 14px;
  text-align: left;
}
</style>
