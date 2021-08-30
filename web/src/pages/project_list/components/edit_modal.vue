<template>
  <a-modal :title="$t(`projectManage.editModal.title.${mode}`)" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-form ref="formRef" :rules="rules" :model="formState" :label-col="{ span: 4 }">
      <a-form-item :label="$t(`projectManage.editModal.form.fields.projectName.label`)" name="projectName">
        <a-input v-model:value="formState.projectName" :placeholder="$t(`projectManage.editModal.form.fields.projectName.placeholder`)" />
      </a-form-item>
      <a-form-item :label="$t(`projectManage.editModal.form.fields.tags.label`)" name="tags">
        <a-select mode="multiple" v-model:value="formState.tags" :placeholder="$t(`projectManage.editModal.form.fields.tags.placeholder`)">
          <a-select-option value="标签1">标签1</a-select-option>
          <a-select-option value="标签2">标签2</a-select-option>
        </a-select>
      </a-form-item>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item :label="$t(`projectManage.editModal.form.fields.editUsers.label`)" name="editUsers" :label-col="{ span: 8 }">
            <a-select mode="multiple" v-model:value="formState.editUsers" :placeholder="$t(`projectManage.editModal.form.fields.editUsers.placeholder`)">
              <a-select-option value="用户1">用户1</a-select-option>
              <a-select-option value="用户2">用户2</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item :label="$t(`projectManage.editModal.form.fields.viewUsers.label`)" name="viewUsers" :label-col="{ span: 8 }">
            <a-select mode="multiple" v-model:value="formState.viewUsers" :placeholder="$t(`projectManage.editModal.form.fields.viewUsers.placeholder`)">
              <a-select-option value="用户1">用户1</a-select-option>
              <a-select-option value="用户2">用户2</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item :label="$t(`projectManage.editModal.form.fields.execUsers.label`)" name="execUsers" :label-col="{ span: 8 }">
            <a-select mode="multiple" v-model:value="formState.execUsers" :placeholder="$t(`projectManage.editModal.form.fields.execUsers.placeholder`)">
              <a-select-option value="用户1">用户1</a-select-option>
              <a-select-option value="用户2">用户2</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item :label="$t(`projectManage.editModal.form.fields.description.label`)" name="description">
        <a-textarea v-model:value="formState.description" :placeholder="$t(`projectManage.editModal.form.fields.description.placeholder`)" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { toRaw } from "vue";
import { message } from "ant-design-vue";
import { createProject, getProjectById, updateProject } from "@/common/service";
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
  emits: ["finish", "cancel", "update:visible"],
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
  watch: {
    async id(newVlaue) {
      if (newVlaue && this.mode === "edit") {
        this.confirmLoading = true;
        const { item } = await getProjectById(newVlaue);
        this.confirmLoading = false;
        this.formState = {
          projectName: item.name,
          tags: item.tags.split(","),
          description: item.description,
          viewUsers: item.viewUsers.split(","),
          execUsers: item.execUsers.split(","),
          editUsers: item.editUsers.split(","),
        };
      }
    },
  },
  methods: {
    async handleOk() {
      await this.$refs.formRef.validate();
      const formatData = {
        id: this.id ? this.id : undefined,
        projectName: this.formState.projectName,
        description: this.formState.description,
        tags: this.formState.tags.join(),
        viewUsers: this.formState.viewUsers.join(),
        execUsers: this.formState.execUsers.join(),
        editUsers: this.formState.editUsers.join(),
      };
      if (this.mode === "create") {
        this.confirmLoading = true;
        await createProject(formatData);
        this.confirmLoading = false;
        message.success("创建成功");
      }
      if (this.mode === "edit") {
        this.confirmLoading = true;
        await updateProject(formatData);
        this.confirmLoading = false;
        message.success("修改成功");
      }
      this.$emit("update:visible", false);
      this.$emit("finish");
    },
  },
};
</script>

<style scoped lang="less"></style>
