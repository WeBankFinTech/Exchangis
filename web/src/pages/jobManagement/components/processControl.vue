<template>
  <div class="process-control-warp">
    <!-- left -->
    <div class="ps-l">
      <span>过程控制</span>
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
            v-for="item in settingParams"
            :key="item.field"
            :label="item.label"
            :name="item.label"
            :required="item.required"
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
import { defineComponent, ref, reactive, toRaw } from "vue";
import DyncRender from "./dyncRender.vue";
export default defineComponent({
  props: {
    psData: Array,
  },
  components: {
    DyncRender,
  },
  setup(props) {
    const formRef = ref();
    let settingParams = props.psData;
    const updateSettingParams = (info) => {
      const _settingParams = toRaw(settingParams).slice(0);
      _settingParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      settingParams = _settingParams;
      console.log("sourceParams", settingParams);
    };
    return {
      formRef,
      settingParams,
      updateSettingParams,
    };
  },
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
