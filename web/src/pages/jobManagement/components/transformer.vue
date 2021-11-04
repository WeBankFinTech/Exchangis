<template>
  <div class="transformer-wrap" style="float: left">
    <!-- top -->
    <div class="tf-top">
      <span v-for="domain in dynamicValidateForm.domains" :key="domain.key">
        <span>校验：</span>
        {{ `${domain.optionVal} ${domain.value}` }}</span
      >
    </div>
    <!-- mid -->
    <div
      class="tf-mid"
      @click="showModal"
      :class="{ 'tf-mid-sync': !dynamicValidateForm.domains.length }"
    >
      <img src="../../../images/jobDetail/u6239.png" />
      <img
        src="../../../images/jobDetail/u6240.png"
        style="position: absolute; left: 72px; top: 6px"
      />
    </div>
    <!-- bottom -->
    <div class="tf-bottom">
      <span
        v-if="
          dynamicValidateForm.transf.value &&
          dynamicValidateForm.transf.startIndex &&
          dynamicValidateForm.transf.endIndex
        "
      >
        <span>转换：</span>
        {{
          `${dynamicValidateForm.transf.value}(${dynamicValidateForm.transf.startIndex},${dynamicValidateForm.transf.endIndex})`
        }}</span
      >
    </div>

    <!-- 弹窗 -->
    <a-modal
      v-model:visible="visible"
      title="映射方式"
      @ok="handleOk"
      okText="保存"
    >
      <div class="tf-modal">
        <!-- top -->
        <div class="tf-modal-top">
          <div class="tf-modal-title">
            <span>校验函数</span>
          </div>
          <div class="tf-modal-content">
            <a-form ref="formRef" :model="dynamicValidateForm">
              <a-form-item
                v-for="domain in dynamicValidateForm.domains"
                :key="domain.key"
              >
                <div style="display: flex">
                  <a-select
                    ref="select"
                    v-model:value="domain.optionVal"
                    style="width: 98px"
                    :options="checkOptions"
                  >
                  </a-select>
                  <a-input
                    v-model:value="domain.value"
                    style="width: 144px; margin-left: 24px"
                  />
                  <MinusCircleOutlined
                    style="
                      font-size: 20px;
                      margin-left: 16px;
                      line-height: 32px;
                    "
                    class="dynamic-delete-button"
                    @click="removeDomain(domain)"
                  />
                </div>
              </a-form-item>
              <a-form-item>
                <div style="display: flex; justify-content: center">
                  <a-button
                    type="dashed"
                    style="width: 220px"
                    @click="addDomain"
                  >
                    <PlusOutlined />
                    Add Check Funcion
                  </a-button>
                </div>
              </a-form-item>
            </a-form>
          </div>
        </div>
        <!-- bottom -->
        <div class="tf-modal-bottom">
          <div class="tf-modal-title">
            <span>转换函数</span>
          </div>
          <div class="tf-modal-content">
            <div>
              <a-select
                ref="select"
                v-model:value="dynamicValidateForm.transf.value"
                style="width: 120px"
                :options="transformFuncOptions"
              >
              </a-select>
            </div>
            <div class="tf-modal-content-l">
              <span>(</span>
              <a-input
                v-model:value="dynamicValidateForm.transf.startIndex"
                placeholder="StartIndex"
                style="width: 80px"
                size="small"
              />
              <span style="line-height: initial">.</span>
              <a-input
                v-model:value="dynamicValidateForm.transf.endIndex"
                placeholder="Length"
                style="width: 80px"
                size="small"
              />
              <span>)</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, toRaw, watch, computed } from "vue";
import {
  SyncOutlined,
  MinusCircleOutlined,
  PlusOutlined,
} from "@ant-design/icons-vue";
export default defineComponent({
  props: {
    tfData: Object,
  },
  emits: ["updateTransformer"],
  components: {
    SyncOutlined,
    MinusCircleOutlined,
    PlusOutlined,
  },
  setup(props, context) {
    let transformerMap = reactive({
      validator: props.tfData.validator || [],
      transformer: props.tfData.transformer || [],
      id: props.tfData.key,
    });

    const newProps = computed(() => JSON.parse(JSON.stringify(props.tfData)));
    watch(newProps, (val, oldVal) => {
      console.log("watch newProps in transformer", val, oldVal);
      const newVal = typeof val === "string" ? JSON.parse(val) : val;
      dynamicValidateForm.domains = transF(newVal.validator);
      dynamicValidateForm.transf = createTransformFunc(newVal.transformer);
    });

    const visible = ref(false);

    const showModal = () => {
      visible.value = true;
    };

    const handleOk = () => {
      visible.value = false;
      const { domains, transf } = dynamicValidateForm;
      const transformer = Object.create(null);
      const validator = [];
      domains.forEach((item) => {
        let str = item.optionVal + " " + item.value;
        validator.push(str);
      });
      const params = [];
      params.push(transf.startIndex);
      params.push(transf.endIndex);
      transformer.name = transf.value;
      transformer.params = params;

      context.emit("updateTransformer", {
        key: props.tfData.key,
        validator,
        transformer,
      });
    };

    // 校验函数生成
    const transF = (arr) => {
      if (!arr) return [];
      const domains = [];
      arr.forEach((item, idx) => {
        let domain = Object.create(null);
        domain.key = idx + "";
        domain.optionVal = item.split(" ")[0];
        domain.value = item.split(" ").slice(-1).pop();
        domains.push(domain);
      });
      return domains;
    };

    // 转换函数生成
    const createTransformFunc = (transformer) => {
      if (!transformer) return {};
      let value = transformer.name && transformer.name;
      let startIndex = transformer.params && transformer.params[0];
      let endIndex = transformer.params && transformer.params.slice(-1).pop();
      return {
        value,
        startIndex,
        endIndex,
      };
    };

    const checkOptions = ref([
      {
        value: "like",
        label: "like",
      },
      {
        value: "not like",
        label: "not like",
      },
      {
        value: ">",
        label: "大于",
      },
      {
        value: "==",
        label: "等于",
      },
      {
        value: "<",
        label: "小于",
      },
      {
        value: ">=",
        label: "大于等于",
      },
      {
        value: "<=",
        label: "小于等于",
      },
    ]);

    const transformFuncOptions = ref([
      {
        value: "ex_substr",
        label: "ex_substr",
      },
      {
        value: "ex_pad",
        label: "ex_pad",
      },
      {
        value: "ex_replace",
        label: "ex_replace",
      },
    ]);

    const formRef = ref();

    let dynamicValidateForm = reactive({
      domains: transF(transformerMap.validator),
      transf: createTransformFunc(transformerMap.transformer),
    });

    const removeDomain = (item) => {
      let index = dynamicValidateForm.domains.indexOf(item);

      if (index !== -1) {
        dynamicValidateForm.domains.splice(index, 1);
      }
    };

    const addDomain = () => {
      let key = dynamicValidateForm.domains.length + "";
      dynamicValidateForm.domains.push({
        value: "",
        optionVal: "like",
        key,
      });
    };

    return {
      visible,
      showModal,
      handleOk,
      transformFuncOptions,
      checkOptions,
      formRef,
      dynamicValidateForm,
      removeDomain,
      addDomain,
    };
  },
  // watch: {
  //   tfData: {
  //     handler: function (newVal) {
  //       console.log("watch props");
  //       this.props = newVal;
  //     },
  //     deep: true,
  //   },
  //   id: {
  //     handler: function (newVal) {
  //       console.log("watch props");
  //       this.props = newVal;
  //     },
  //     deep: true,
  //   },
  // },
});
</script>

<style lang="less" scoped>
.tf-mid {
  text-align: center;
  position: relative;
}

.tf-mid-sync {
  margin-top: 30px;
}
.tf-modal-content {
  display: flex;
  flex-direction: row;
}
.tf-modal-content-l {
  display: flex;
  flex-direction: row;
  margin-left: 20px;
  line-height: 32px;
  > span {
    margin: 0 10px;
  }
}
.tf-modal-content {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
.tf-modal-title {
  margin-bottom: 20px;
}
.tf-bottom {
  text-align: center;
  > span {
    font-size: 12px;
  }
}
.tf-top {
  text-align: center;
  > span {
    font-size: 12px;
  }
  :nth-of-type(2) {
    margin: 0;
  }
  :nth-of-type(1) {
    max-width: 0;
  }
}
</style>
