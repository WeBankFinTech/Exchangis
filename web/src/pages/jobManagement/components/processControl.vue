<template>
  <div class="process-control-warp">
    <!-- left -->
    <!-- <div class="ps-l">
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
    </div> -->
    <!-- right -->
    <div class="ps-r">
      <div class="main-header" @click="showInfo">
        <span style="margin-right: 8px; color: rgba(0, 0, 0, 0.45)">
          <RightOutlined v-if="!isFold" />
          <DownOutlined v-else />
        </span>
        <span>过程控制</span>
      </div>
      <div
        class="main-content"
        v-show="isFold"
        :class="{ 'text-danger': !settingData.psData.length }"
      >
        <div class="main-content-t">
          <span>参数配置</span>
        </div>
        <div class="main-content-b">
          <a-form ref="formRef" :label-col="labelCol" :wrapper-col="wrapperCol">
            <!-- 动态组件 -->
            <a-row :gutter="24">
              <a-col
                :span="12"
                v-for="item in settingData.psData"
                :key="item.field"
              >
                <a-form-item
                  :label="item.label"
                  :name="value"
                  :model="formState"
                  :help="helpMsg[item.key]"
                  :validateStatus="helpStatus[item.key]"
                  :required="item.required"
                  class="process-control-label"
                >
                  <dync-render
                    v-bind:param="item"
                    @updateInfo="updateSettingParams"
                    :style='styleObject'
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { DownOutlined, RightOutlined } from "@ant-design/icons-vue";
import {
  defineComponent,
  ref,
  reactive,
  toRaw,
  watch,
  computed,
  defineAsyncComponent,
} from "vue";
import cloneDeep from "lodash-es";
export default defineComponent({
  props: {
    psData: Array,
    engineType: String,
  },
  emits: ["updateProcessControl"],
  components: {
    "dync-render": defineAsyncComponent(() => import("./dyncRender.vue")),
    DownOutlined,
    RightOutlined,
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
      if (info.required && !info.value) {
        helpMsg[info.key] = `请输入${info.label}`;
        helpStatus[info.key] = "error";
      } else if (info.value && info.validateType === "REGEX") {
        const num_reg = new RegExp(`${info.validateRange}`);
        if (!num_reg.test(info.value)) {
          helpMsg[info.key] = info.validateMsg;
          helpStatus[info.key] = "error";
        } else {
          helpMsg[info.key] = "";
          helpStatus[info.key] = "success";
        }
      } else {
        helpMsg[info.key] = "";
        helpStatus[info.key] = "success";
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
          width: "417px",
          'text-align': 'left',
        },
      },
      wrapperCol: {
        style: {
          'text-align': 'left',
        }
      },
      styleObject: {
        width: '400px'
      }
    };
  },
});
</script>

<style lang="less" scoped>
.process-control-warp {
  padding: 24px;
  display: flex;
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
    height: 20px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
  .main-content {
    min-width: 905px;
    max-width: 905px;
    border: 1px solid #dee4ec;
    margin: 24px;
    &-t {
      box-sizing: content-box;
      height: 39px;
      text-align: center;
      line-height: 39px;
      font-family: PingFangSC-Medium;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.65);
      font-weight: 500;
      background-color: #f8fafd;
      border-bottom: 1px solid #dee4ec;
    }
    &-b {
      padding: 0 16px;
      padding-top: 8px;
      padding-bottom: 18px;
    }
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
