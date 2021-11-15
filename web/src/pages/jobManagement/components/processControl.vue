<template>
  <div class="process-control-warp">
    <!-- left -->
    <div class="ps-l">
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
        <span class="main-header-label" @click="showInfo">过程控制</span>
      </div>
    </div>
    <!-- right -->
    <div class="ps-r">
      <div class="main-header">
        <div>
          <span class="main-header-label">速度控制</span>
        </div>
      </div>

      <div
        class="main-content"
        v-show="isFold"
        :class="{ 'text-danger': !settingData.psData.length }"
      >
        <a-form ref="formRef" layout="inline" :label-col="labelCol">
          <!-- 动态组件 -->
          <a-form-item
            v-for="item in settingData.psData"
            :key="item.field"
            :label="item.label"
            :name="item.label"
            :model="formState"
            :help="helpMsg[item.key]"
            :validate-status="helpStatus[item.key]"
            required
            class="process-control-label"
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
import cloneDeep from "lodash-es";
export default defineComponent({
  props: {
    psData: Array,
    engineType: String,
  },
  emits: ["updateProcessControl"],
  components: {
    DyncRender,
  },
  setup(props, context) {
    const formRef = ref();
    let settingData = reactive({
      psData: toRaw(props.psData),
    });
    const formState = reactive({});
    const helpMsg = reactive({});
    const helpStatus = reactive({});
    props.psData.forEach((item) => {
      formState[item.key] = "";
      helpMsg[item.key] = "";
      helpStatus[item.key] = "success";
    });
    const newProps = computed(() => JSON.parse(JSON.stringify(props.psData)));
    watch(newProps, (val, oldVal) => {
      settingData.psData = typeof val === "string" ? JSON.parse(val) : val;
    });
    const updateSettingParams = (info) => {
      formState[info.key] = info.value;
      const num_reg = /^\d+$/;
      if (!info.value) {
        helpMsg[info.key] = `请正确输入${info.label}`;
        helpStatus[info.key] = "error";
      } else {
        if (!num_reg.test(info.value)) {
          helpMsg[info.key] = `请正确输入${info.label}`;
          helpStatus[info.key] = "error";
        } else {
          helpMsg[info.key] = "";
          helpStatus[info.key] = "success";
        }
      }
      const _settingParams = toRaw(settingData.psData).slice(0);
      _settingParams.forEach((item) => {
        if (item.field === info.field) {
          return (item.value = info.value);
        }
      });
      settingData.psData = _settingParams;
      context.emit("updateProcessControl", settingData.psData);
    };
    let isFold = ref(true);
    const showInfo = () => {
      isFold.value = !isFold.value;
    };
    return {
      formRef,
      formState,
      helpMsg,
      helpStatus,
      settingData,
      updateSettingParams,
      isFold,
      showInfo,
      labelCol: {
        style: {
          width: "150px",
        },
      },
    };
  },
});
</script>

<style lang="less" scoped>
.process-control-warp {
  margin-top: 30px;
  width: 1215px;
  display: flex;
}
.ps-l {
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
    :nth-of-type(1) {
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
      position: absolute;
      left: 46px;
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

  .text-danger {
    padding: 0px;
    border: none;
  }
}
.process-control-label {
  font-size: 14px;
  text-align: right;
  width: 440px;
}
</style>
