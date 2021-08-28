<template>
  <a-modal :title="$t(`dataSource.editModal.title.${mode}`)" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-form ref="formRef" :rules="rules" :model="formState" :label-col="{ span: 6 }">
      <a-form-item :label="$t(`dataSource.editModal.form.fields.dataSourceName.label`)" name="dataSourceName">
        <a-input v-model:value="formState.dataSourceName" :placeholder="$t(`dataSource.editModal.form.fields.dataSourceName.placeholder`)" />
      </a-form-item>
      <a-form-item label="数据源host" :name="['connectParams', 'host']">
        <a-input v-model:value="formState.connectParams.host" placeholder="数据源host" />
      </a-form-item>
      <a-form-item label="数据库名称" :name="['connectParams', 'dbName']">
        <a-input v-model:value="formState.connectParams.dbName" placeholder="数据库名称" />
      </a-form-item>
      <a-form-item label="用户名" :name="['connectParams', 'dbUser']">
        <a-input v-model:value="formState.connectParams.dbUser" placeholder="用户名" />
      </a-form-item>
      <a-form-item label="密码" :name="['connectParams', 'dbPassword']">
        <a-input v-model:value="formState.connectParams.dbPassword" placeholder="密码" />
      </a-form-item>
      <a-form-item :label="$t(`dataSource.editModal.form.fields.dataSourceDesc.label`)" name="dataSourceDesc">
        <a-textarea v-model:value="formState.dataSourceDesc" :placeholder="$t(`dataSource.editModal.form.fields.dataSourceDesc.placeholder`)" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { createDataSource } from "@/common/service";
import { toRaw } from "vue";
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
    // 创建的数据源类型
    type: {
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
      formState: {
        dataSourceName: "",
        dataSourceDesc: "",
        connectParams: {
          host: "",
          dbName: "",
          dbUser: "",
          dbPassword: "",
        },
      },
      // 验证
      rules: {
        dataSourceName: [{ required: true, min: 1, max: 24 }],
      },
    };
  },

  methods: {
    // modal完成
    handleOk(e) {
      // 在这里应该触发表单提交
      this.$refs.formRef.validate().then(async () => {
        console.log("values", this.formState.dataSourceName);
        await createDataSource({
          ...toRaw(this.formState),
          dataSourceTypeId: this.type,
          createSystem: "",
        });
        // 请求保存数据
        this.$emit("finish");
      });
    },
  },
};
</script>

<style scoped lang="less"></style>
