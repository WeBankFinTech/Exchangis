<template>
  <a-modal title="创建项目" okText="确定" cancelText="取消" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-form ref="formRef" :rules="rules" :model="formState" :label-col="{ span: 4 }" @submit="handleSubmit">
      <a-form-item label="项目名" name="name">
        <a-input v-model:value="formState.name" placeholder="请输入项目名" />
      </a-form-item>
      <a-form-item label="业务标签" name="tags">
        <a-input v-model:value="formState.tags" placeholder="请输入业务标签" />
      </a-form-item>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="编辑权限" name="edit_users" :label-col="{ span: 8 }">
            <a-select v-model:value="formState.edit_users" placeholder="请选择用户">
              <a-select-option value="male"> male </a-select-option>
              <a-select-option value="female"> female </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="查看权限" name="show_users" :label-col="{ span: 8 }">
            <a-select v-model:value="formState.show_users" placeholder="请选择用户">
              <a-select-option value="male"> male </a-select-option>
              <a-select-option value="female"> female </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="执行权限" name="run_users" :label-col="{ span: 8 }">
            <a-select v-model:value="formState.run_users" placeholder="请选择用户">
              <a-select-option value="male"> male </a-select-option>
              <a-select-option value="female"> female </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item label="描述信息" name="describe">
        <a-textarea v-model:value="formState.describe" placeholder="请填写描述信息" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { Modal, Col, Row, Input, Form, FormItem, Select, SelectOption, Textarea } from "ant-design-vue";
export default {
  name: "ItemEditModal",
  components: {
    aCol: Col,
    aRow: Row,
    aModal: Modal,
    aForm: Form,
    aInput: Input,
    aFormItem: FormItem,
    aSelect: Select,
    aSelectOption: SelectOption,
    aTextarea: Textarea,
  },
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
