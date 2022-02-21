<template>
  <a-modal
    title="复制任务"
    :visible="visible"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    @cancel="$emit('update:visible', false)"
  >
    <a-form
      ref="formRef"
      :rules="rules"
      :model="formState"
      :label-col="{ span: 4 }"
    >
      <a-form-item label="原任务" name="subJobName">
        <a-input v-model:value="origin.subJobName" disabled />
      </a-form-item>
      <a-form-item label="任务名" name="jobName">
        <a-input v-model:value="formState.jobName" />
      </a-form-item>
      <!--<a-form-item label="业务标签" name="jobLabels">-->
      <!--<a-input v-model:value="formState.jobLabels" />-->
      <!--</a-form-item>-->
      <a-form-item label="执行引擎" name="engineType">
        <a-input v-model:value="origin.engineType" disabled />
      </a-form-item>
      <!--<a-form-item label="任务描述" name="jobDesc">-->
      <!--<a-input v-model:value="formState.jobDesc" />-->
      <!--</a-form-item>-->
    </a-form>
  </a-modal>
</template>

<script>
import { toRaw } from "vue";
import { message } from "ant-design-vue";
import { PlusOutlined } from "@ant-design/icons-vue";
export default {
  name: "JobManagementConfigModal",
  components: {
    PlusOutlined,
  },
  props: {
    // 是否可见
    visible: {
      type: Boolean,
      required: true,
    },
    origin: {
      type: Object,
      default: {},
    },
  },
  emits: ["finish", "cancel", "update:visible"],
  data() {
    return {
      // 是否加载中
      confirmLoading: false,
      // 表单数据
      formState: {
        jobName: "",
        jobLabels: "",
        jobDesc: "",
      },
      // 验证
      rules: {
        jobName: [{ required: true }],
      },
    };
  },
  watch: {},
  methods: {
    async handleOk() {
      await this.$refs.formRef.validate();
      const formatData = {
        ...this.origin,
        subJobName: this.formState.jobName
      };
      try {
        this.confirmLoading = true;
        message.success("复制成功");
      } catch (error) {}
      this.confirmLoading = false;
      this.$emit("update:visible", false);
      this.$emit("finish", formatData);
    },
  },
};
</script>

<style scoped lang="less">
.fr {
  float: right;
}
.fl {
  float: left;
}
.w40 {
  width: 40%;
}
.separator {
  margin: 5px;
}
</style>
