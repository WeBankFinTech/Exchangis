<template>
  <div class="transformer-wrap">
    <!-- top -->
    <div class="tf-top">
      <span v-for="domain in dynamicValidateForm.domains" :key="domain.key">
        <a-tooltip>
          <template #title>校验：{{ `${domain.optionVal} ${domain.value}` }}</template>
          校验：{{ `${domain.optionVal} ${domain.value}` }}
        </a-tooltip>
      </span>
    </div>
    <!-- mid -->
    <div class="tf-mid" @click="showModal"
      :class="{ 'tf-mid-sync': !dynamicValidateForm.domains.length }">
      <svg class="icon icon-symbol" aria-hidden="true">
        <use xlink:href="#icon-lansejiantoudaikuang"></use>
      </svg>
      <!--<img src="../../../images/jobDetail/u6239.png" />-->
      <!--<img-->
      <!--src="../../../images/jobDetail/u6240.png"-->
      <!--style="position: absolute; left: 72px; top: 6px; cursor: pointer"-->
      <!--/>-->
    </div>
    <!-- bottom -->
    <div class="tf-bottom">
      <a-tooltip>
        <template #title>{{ trandformStr }}</template>
        {{ trandformStr }}
      </a-tooltip>
    </div>

    <!-- 弹窗 -->
    <a-modal v-model:visible="visible" title="映射方式" @ok="handleOk" okText="保存" @cancel="handleCancel">
      <div class="tf-modal">
        <!-- top -->
        <div class="tf-modal-top">
          <div class="tf-modal-title">
            <span>校验函数</span>
          </div>
          <div class="tf-modal-content">
            <a-form ref="formRef" :model="dynamicValidateForm">
              <a-form-item v-for="domain in dynamicValidateForm.domains" :key="domain.key">
                <div style="display: flex">
                  <a-select ref="select" v-model:value="domain.optionVal" style="width: 98px"
                    :options="checkOptions">
                  </a-select>
                  <a-input v-model:value="domain.value" style="width: 144px; margin-left: 24px" />
                  <MinusCircleOutlined style="
                      font-size: 20px;
                      margin-left: 16px;
                      line-height: 32px;
                    " class="dynamic-delete-button" @click="removeDomain(domain)" />
                </div>
              </a-form-item>
              <a-form-item v-if="!dynamicValidateForm.domains.length">
                <div style="display: flex; justify-content: center">
                  <a-button type="dashed" style="width: 220px" @click="addDomain">
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
              <a-select ref="select" v-model:value="dynamicValidateForm.transf.value"
                style="width: 120px" :options="transformFuncOptions"
                @select="changeDynamicValidateValue">
              </a-select>
            </div>
            <div class="tf-modal-content-l">
              <span>(</span>
              <a-input v-model:value="dynamicValidateForm.transf.param1" :placeholder="placeholder1"
                style="width: 130px" size="small" />
              <!--<span style="line-height: initial">.</span>-->
              <a-input v-model:value="dynamicValidateForm.transf.param2" :placeholder="placeholder2"
                style="width: 130px" size="small" />
              <!-- 编辑和新增判断param3的显示 -->
              <a-input
                v-if="placeholder3"
                v-model:value="dynamicValidateForm.transf.param3" :placeholder="placeholder3"
                style="width: 130px" size="small" />
              <span>)</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import {
  defineComponent,
  ref,
  reactive,
  toRaw,
  watch,
  computed,
  onMounted,
} from 'vue';
import { message } from 'ant-design-vue';
import {
  SyncOutlined,
  MinusCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue';

export default defineComponent({
  props: {
    tfData: Object,
    checkOptions: Array,
    transformFuncOptions: Array,
    transformEnable: Boolean
  },
  emits: ['updateTransformer'],
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
      deleteEnable: props.tfData.deleteEnable,
    });

    const newProps = computed(() => JSON.parse(JSON.stringify(props.tfData)));
    watch(newProps, (val, oldVal) => {
      const newVal = typeof val === 'string' ? JSON.parse(val) : val;
      dynamicValidateForm.domains = transF(newVal.validator); // 检验函数的键值对
      dynamicValidateForm.transf = createTransformFunc(newVal.transformer);
    });

    const visible = ref(false);

    const showModal = () => {
      if (props.transformEnable) {
        visible.value = true;
        changeDynamicValidateValue('new-open');
      }
    };

    // 点击确认
    const handleOk = () => {
      const { domains, transf } = dynamicValidateForm;
      const transformer = {};
      let validator = [];
      domains.forEach((item) => {
        validator = [item.optionVal, item.value];
      });

      if (dynamicValidateForm.transf.value) {
        if (!transf.param1 && placeholder1.value) {
          return message.error(`${placeholder1.value}为必填`);
        }
        if (!transf.param2  && placeholder2.value) {
          return message.error(`${placeholder2.value}为必填`);
        }
        if (!transf.param3 && placeholder3.value) {
          return message.error(`${placeholder3.value}为必填`);
        }
        const { param1, param2, param3 } = transf || {}
        const params = [ param1, param2, param3 ];
        transformer.name = transf.value;
        transformer.params = params.filter(v => !!v);
      }
      visible.value = false;
      context.emit('updateTransformer', {
        key: props.tfData.key,
        validator,
        transformer,
        deleteEnable: props.tfData.deleteEnable,
      });
    };

    // 取消
    const handleCancel = () => {
      dynamicValidateForm.domains = transF(newProps.value.validator);
      dynamicValidateForm.transf = createTransformFunc(newProps.value.transformer);
    }

    // 校验函数生成
    const transF = (arr) => {
      if (!arr) return [];
      const domains = [];
      let temp = arr.length > 0 ? [arr.join('$_$')] : arr
      temp.forEach((item, idx) => {
        let domain = Object.create(null);
        domain.key = idx + '';
        domain.optionVal = item.split('$_$')[0];
        domain.value = item.split('$_$').slice(-1).pop();
        domains.push(domain);
      });
      return domains;
    };

    // 转换函数生成
    const createTransformFunc = (transformer) => {
      if (!transformer) return {};
      let value = (transformer.name && transformer.name) || '';
      let param1 =
        transformer.params &&
        transformer.params.length &&
        transformer.params[0];
      let param2 =
        transformer.params &&
        transformer.params.length > 1 &&
        transformer.params[1];
      let param3 =
        transformer.params &&
        transformer.params.length > 2 &&
        transformer.params[2];
      return {
        value,
        param1,
        param2,
        param3,
      };
    };

    const formRef = ref();

    // 映射表单
    let dynamicValidateForm = reactive({
      domains: transF(transformerMap.validator),
      transf: createTransformFunc(transformerMap.transformer),
    });

    // 删除检验函数
    const removeDomain = (item) => {
      let index = dynamicValidateForm.domains.indexOf(item);

      if (index !== -1) {
        dynamicValidateForm.domains.splice(index, 1);
      }
    };

    // 添加检验函数
    const addDomain = () => {
      let key = dynamicValidateForm.domains.length + '';
      dynamicValidateForm.domains.push({
        value: '',
        optionVal: 'like',
        key,
      });
    };

    // 检验函数和转换函数的下拉选项
    const checkOptions = computed(() => props.checkOptions);
    const transformFuncOptions = computed(() => props.transformFuncOptions);

    // 转换函数的参数
    let placeholder1 = ref(''),
      placeholder2 = ref(''),
      placeholder3 = ref('');

    // 转换函数的change事件 open是否刚打开弹出窗 要判断有没有值
    const changeDynamicValidateValue = (open) => {
      let curVal = dynamicValidateForm.transf.value;
      const { paramNames = [] } = transformFuncOptions.value.find(
        (v) => v.value === curVal
      ) || {};
      let paramKeys = ['param1', 'param2', 'param3'];
      let placeholderKeys = [placeholder1, placeholder2, placeholder3];
      paramKeys.forEach((v, i) => {
        if (open !== 'new-open') {
          dynamicValidateForm.transf[v] = '';
        }
        placeholderKeys[i].value = paramNames[i] || '';
      });
    };

    const trandformStr = computed(() => {
      const { value, param1, param2, param3 } = dynamicValidateForm.transf || {}
      const params = [ param1, param2, param3 ].filter(v => !!v);
      return value && params.length ? `转换：${value}(${params.join(',')})` : ''
    })

    return {
      visible,
      showModal,
      handleOk,
      handleCancel,
      transformFuncOptions,
      checkOptions,
      formRef,
      dynamicValidateForm,
      removeDomain,
      addDomain,
      changeDynamicValidateValue,
      placeholder1,
      placeholder2,
      placeholder3,
      trandformStr
    };
  },
});
</script>

<style lang="less" scoped>
@import '../../../common/content.less';
.tf-mid {
  text-align: center;
  position: relative;
  height: 16px;
}

.tf-mid-sync {
  /*margin-top: 10px;*/
}
.tf-modal-content {
  display: flex;
  flex-direction: row;
}
.tf-modal-content-l {
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
  line-height: 22px;
  height: 22px;
  :deep(span) {
    font-size: 12px;
    display: inline-block;
    width: 100%;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
}
.tf-top {
  text-align: center;
  line-height: 27px;
  height: 27px;
  :deep(span) {
    font-size: 12px;
    display: inline-block;
    width: 100%;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
  :nth-of-type(2) {
    margin: 0;
  }
}
.icon-symbol {
  font-size: 50px;
  position: absolute;
  left: 72px;
  cursor: pointer;
  top: -16px;
}
.transformer-wrap {
  position: relative;
}
</style>
