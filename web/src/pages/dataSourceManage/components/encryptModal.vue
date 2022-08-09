<template>
  <a-modal :title="$t(`加密`)" :footer="null" :visible="visible" :confirm-loading="confirmLoading"
    :width="600" @cancel="$emit('update:visible', false)">
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="formState" :label-col="{ span: 6 }">
        <a-form-item label="原始字符串">
          <a-input-password v-model:value="formState.originStr" placeholder="请输入" />
        </a-form-item>
        <a-form-item class="inline-button">
          <a-button type="primary" @click="handleEncrypt">加密</a-button>
        </a-form-item>
        <a-form-item label="加密字符串">
          <a-input v-model:value="formState.encryStr" :readOnly="true"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { toRaw } from 'vue';
import { message } from 'ant-design-vue';
import { encryptFunc } from '@/common/service';
import func from 'vue-editor-bridge';

export default {
  name: 'encryptModal',
  props: {
    // 是否可见
    visible: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['update:visible'],
  data() {
    return {
      // 是否加载中
      confirmLoading: false,
      // 表单数据
      formState: {
        originStr: '',
        encryStr: '',
      },
    };
  },
  watch: {
    visible: function (cur, val) {
      if (!cur) {
        this.formState = {
          originStr: '',
          encryStr: '',
        };
      }
    },
  },
  methods: {
    // modal加密
    async handleEncrypt() {
      const { originStr } = this.formState
      if(!originStr) return
      this.confirmLoading = true;
      try {
        const res = await encryptFunc({ encryStr: originStr });
        this.formState.encryStr = res?.encryStr || '';
        message.success('加密成功');
      } catch (error) {
        console.log(error);
      }
      this.confirmLoading = false;
    },
  },
};
</script>

<style scoped lang="less">
.inline-button {
  :deep(.ant-form-item-control-input-content) {
    text-align: center;
  }
}
</style>
