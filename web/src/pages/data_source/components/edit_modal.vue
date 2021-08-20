<template>
  <a-modal title="创建项目" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-form ref="formRef" :rules="rules" :model="formState" :label-col="{ span: 6 }" @submit="handleSubmit">
      <a-form-item label="数据源名称" name="name">
        <a-input v-model:value="formState.name" placeholder="请输入数据源名称" />
      </a-form-item>
      <a-form-item label="数据源Host" name="tags">
        <a-input v-model:value="formState.tags" placeholder="请输入数据源Host" />
      </a-form-item>
      <a-form-item label="数据库名称" name="tags">
        <a-input v-model:value="formState.tags" placeholder="请输入数据库名称" />
      </a-form-item>
      <a-form-item label="用户名" name="tags">
        <a-input v-model:value="formState.tags" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item label="密码" name="tags">
        <a-input v-model:value="formState.tags" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item label="可用集群" name="edit_users">
        <a-select v-model:value="formState.edit_users" placeholder="请选择可用集群">
          <a-select-option value="male"> male </a-select-option>
          <a-select-option value="female"> female </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="描述信息" name="describe">
        <a-textarea v-model:value="formState.describe" placeholder="请填写描述信息" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
export default {
  name: "ItemEditModal",
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
    // id如果是新增模式可以不传
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
      formState: { name: "", tags: [], describe: "", show_users: [], run_users: [], edit_users: [] },
      // 验证
      rules: {
        name: [{ required: true, min: 3, max: 5 }],
      },
    };
  },

  methods: {
    // modal完成
    handleOk(e) {
      // 在这里应该触发表单提交
      this.$refs.formRef.validate().then(() => {
        console.log("values", this.formState.name);
      });
    },
    // 表单提交
    handleSubmit(e) {
      // 请求保存数据
      // this.$emit("finish");
    },
  },
};
</script>

<style scoped lang="less"></style>
