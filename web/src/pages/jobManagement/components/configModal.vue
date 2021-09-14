<template>
  <a-modal title="设置" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-form ref="formRef" :rules="rules" :model="formState" :label-col="{ span: 4 }">
      <h2>任务配置</h2>
      <a-form-item label="执行用户" name="execUser">
        <a-input v-model:value="formState.execUser" />
      </a-form-item>
      <a-form-item label="执行用户" name="execPoint">
        <a-input v-model:value="formState.execPoint" />
      </a-form-item>
      <a-form-item label="同步方式" name="syncType">
        <a-radio-group v-model="formState.syncType" name="syncType">
          <a-radio :value="1">
            全量
          </a-radio>
          <a-radio :value="2">
            增量
          </a-radio>
        </a-radio-group>
      </a-form-item>
      <h2>任务变量 <PlusOutlined class="fr" @click="createTask"/></h2>
      <div v-for="(item, index) in formState.taskVariable" style="overflow: hidden">
        <a-form-item class="w40 fl" :label="index + 1" name="taskVariableKey" >
          <a-input v-model:value="item.key" />
        </a-form-item>
        <span class="fl separator">=</span>
        <a-form-item class="w40 fl" name="taskVariableValue">
          <a-input v-model:value="item.value" />
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>

<script>
import { toRaw } from "vue";
import { message } from "ant-design-vue";
import {
  PlusOutlined,
} from "@ant-design/icons-vue";
import { createProject, getProjectById, updateProject } from "@/common/service";
export default {
  name: "JobManagementConfigModal",
  components: {
    PlusOutlined
  },
  props: {
    // 是否可见
    visible: {
      type: Boolean,
      required: true,
    },
    id: {
      type: String,
      default: "",
    },
  },
  emits: ["finish", "cancel", "update:visible"],
  data() {
    return {
      // 是否加载中
      confirmLoading: false,
      // 表单数据
      formState: { execUser: '', execPoint: '', syncType: '', taskVariable: []},
      // 验证
      rules: {
        execUser: [{ required: true}],
        execPoint: [{ required: true}],
        syncType: [{ required: true}]
      },
    };
  },
  watch: {
  },
  methods: {
    async handleOk() {
      await this.$refs.formRef.validate();
      const formatData = {
        ...this.formState
      };
      try {
        if (this.mode === "create") {
          this.confirmLoading = true;
          await createProject(formatData);
          message.success("创建成功");
        }
        if (this.mode === "edit") {
          this.confirmLoading = true;
          await updateProject(formatData);
          message.success("修改成功");
        }
      } catch (error) {}
      this.confirmLoading = false;
      this.$emit("update:visible", false);
      this.$emit("finish");
    },
    createTask(){
      this.formState.taskVariable.push({key:'' , value:''})
    }
  }
};
</script>

<style scoped lang="less">
  .fr{
    float: right;
  }
  .fl {
    float: left;
  }
  .w40{
    width: 40%;
  }
  .separator {
    margin: 5px;
  }
</style>
