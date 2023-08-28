<!--
 * @Description: 
 * @Author: sueRim
 * @Date: 2022-08-04 10:12:48
-->
<template>
  <a-modal :title="$t(`加密插件`)" :footer="null" :visible="visible" :confirm-loading="confirmLoading"
    :width="600" @cancel="$emit('update:visible', false)">
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="formState" :label-col="{ span: 6 }">
        <a-form-item label="原始字符串">
          <div class="inline-group">
            <a-input-password v-model:value="formState.originStr" allowClear placeholder="请输入" />
            <a-button type="primary" @click="handleEncrypt">加密</a-button>
          </div>
        </a-form-item>
        <a-form-item label="加密字符串">
           <div class="inline-group">
            <a-input v-model:value="formState.encryStr" disabled />
            <a-button type="primary" @click="onCopy">复制</a-button>
          </div>
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
      const { originStr } = this.formState;
      if (!originStr) {
        return message.warning('原始字符串不能为空');
      }
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

    onCopy() {
      let oInput = document.createElement('input')
      oInput.value = this.formState?.encryStr || '';
      document.body.appendChild(oInput)
      oInput.select() // 选择对象
      document.execCommand("Copy") // 执行浏览器复制命令
      message.success("复制成功");
      oInput.remove()
    }
  },
};
</script>

<style scoped lang="less">
.inline-group {
  :deep(.ant-input) {
    width: calc(100% - 80px);
    margin-right: 16px;
  }
  :deep(.ant-input-password) {
    width: calc(100% - 80px);
    margin-right: 16px;
    .ant-input {
      width: 100%;
      margin-right: 0px;
    }
  }
}
</style>
