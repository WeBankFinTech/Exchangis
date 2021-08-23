<template>
  <a-modal title="创建项目" okText="确定" cancelText="取消" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-form ref="formRef" :rules="rules" :model="formState" :label-col="{ span: 4 }">
      <a-form-item label="项目名" name="projectName">
        <a-input v-model:value="formState.projectName" placeholder="请输入项目名" />
      </a-form-item>
      <a-form-item label="业务标签" name="tags">
        <a-select mode="multiple" v-model:value="formState.tags" placeholder="请选择标签">
          <a-select-option value="标签1">标签1</a-select-option>
          <a-select-option value="标签2">标签2</a-select-option>
        </a-select>
      </a-form-item>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="编辑权限" name="editUsers" :label-col="{ span: 8 }">
            <a-select mode="multiple" v-model:value="formState.editUsers" placeholder="请选择用户">
              <a-select-option value="用户1">用户1</a-select-option>
              <a-select-option value="用户2">用户2</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="查看权限" name="viewUsers" :label-col="{ span: 8 }">
            <a-select mode="multiple" v-model:value="formState.viewUsers" placeholder="请选择用户">
              <a-select-option value="用户1">用户1</a-select-option>
              <a-select-option value="用户2">用户2</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="执行权限" name="execUsers" :label-col="{ span: 8 }">
            <a-select mode="multiple" v-model:value="formState.execUsers" placeholder="请选择用户">
              <a-select-option value="用户1">用户1</a-select-option>
              <a-select-option value="用户2">用户2</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item label="描述信息" name="description">
        <a-textarea v-model:value="formState.description" placeholder="请填写描述信息" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { toRaw } from "vue";
import { createProject } from "@/common/service";
export default {
  name: "ProjectEditModal",
  props: {
    // 是否可见
    visible: {
      type: Boolean,
      required: true,
    },
    // 模式
    mode: {
      type: String,
      required: true,
    },
    id: {
      type: String,
      default: "",
    },
  },
  emits: {
    // 操作完成
    finish: null,
    // 操作取消
    cancel: null,
    "update:visible": null,
  },
  data() {
    return {
      // 是否加载中
      confirmLoading: false,
      // 表单数据
      formState: { projectName: "", tags: [], description: "", viewUsers: [], execUsers: [], editUsers: [] },
      // 验证
      rules: {
        projectName: [{ required: true, min: 3, max: 5 }],
      },
    };
  },
  methods: {
    async handleOk(e) {
      await this.$refs.formRef.validate();
      this.confirmLoading = true;
      await createProject({
        ...toRaw(this.formState),
        tags: this.formState.tags.join(),
        viewUsers: this.formState.viewUsers.join(),
        execUsers: this.formState.execUsers.join(),
        editUsers: this.formState.editUsers.join(),
      });
      this.confirmLoading = false;
      this.$emit("update:visible", false);
      this.$emit("finish");
    },
  },
};
</script>

<style scoped lang="less"></style>
