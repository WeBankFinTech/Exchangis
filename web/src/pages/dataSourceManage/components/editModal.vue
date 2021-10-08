<template>
  <a-modal :title="$t(`dataSource.editModal.title.${mode}`)" :visible="visible" :confirm-loading="confirmLoading" @ok="handleOk" @cancel="$emit('update:visible', false)">
    <a-spin :spinning="confirmLoading">
      <a-form ref="formRef" :model="formState" :label-col="{ span: 6 }">
        <a-form-item :label="$t(`dataSource.editModal.form.fields.dataSourceName.label`)" name="dataSourceName">
          <a-input v-model:value="formState.dataSourceName" :placeholder="$t(`dataSource.editModal.form.fields.dataSourceName.placeholder`)" />
        </a-form-item>
        <a-form-item label="地址" :name="['connectParams', 'host']">
          <a-input v-model:value="formState.connectParams.host" placeholder="数据源host" />
        </a-form-item>
        <a-form-item label="端口" :name="['connectParams', 'port']">
          <a-input v-model:value="formState.connectParams.port" placeholder="端口" />
        </a-form-item>
        <a-form-item label="用户名" :name="['connectParams', 'username']">
          <a-input v-model:value="formState.connectParams.username" placeholder="用户名" />
        </a-form-item>
        <a-form-item label="密码" :name="['connectParams', 'password']">
          <a-input v-model:value="formState.connectParams.password" placeholder="密码" />
        </a-form-item>
        <a-form-item label="标签" :name="['connectParams', 'labels']">
          <a-select mode="multiple" v-model:value="formState.labels" placeholder="请选择标签">
            <a-select-option value="用户1">用户1</a-select-option>
            <a-select-option value="用户2">用户2</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="注释" :name="['connectParams', 'comment']">
          <a-input v-model:value="formState.connectParams.comment" placeholder="注释" />
        </a-form-item>
        <a-form-item :label="$t(`dataSource.editModal.form.fields.dataSourceDesc.label`)" name="dataSourceDesc">
          <a-textarea v-model:value="formState.dataSourceDesc" :placeholder="$t(`dataSource.editModal.form.fields.dataSourceDesc.placeholder`)" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { createDataSource, updateDataSource, getDataSourceById } from "@/common/service";
import { toRaw } from "vue";
import { message } from "ant-design-vue";
export default {
  name: "projectEditModal",
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
        // 数据源名称
        dataSourceName: "",
        // 数据源描述
        dataSourceDesc: "",
        // 连接参数
        connectParams: {
          host: "",
          port: "",
          username: "",
          password: "",
        },
        labels: [],
        comment: "",
      },
    };
  },
  watch: {
    id(val) {
      if (val) this.getDataSourceById();
    },
  },
  methods: {
    async getDataSourceById() {
      this.confirmLoading = true;
      let { info } = await getDataSourceById(this.id);
      this.confirmLoading = false;
      this.formState = {
        dataSourceName: info.dataSourceName,
        dataSourceDesc: info.dataSourceDesc,
        connectParams: info.connectParams,
        labels: info.labels.split(","),
        comment: info.comment || "",
      };
    },
    // modal完成
    async handleOk() {
      await this.$refs.formRef.validate();
      this.confirmLoading = true;
      try {
        if (this.mode === "create") {
          await createDataSource({
            ...toRaw(this.formState),
            labels: this.formState.labels.join(","),
            dataSourceTypeId: this.type,
            createSystem: "",
            createIdentify: "",
          });
          message.success("创建成功");
        }
        if (this.mode === "edit") {
          await updateDataSource(this.id, {
            ...toRaw(this.formState),
            labels: this.formState.labels.join(","),
            dataSourceTypeId: this.type,
            createSystem: "",
            createIdentify: "",
          });
          message.success("修改成功");
        }
      } catch (error) {}
      this.confirmLoading = false;
      this.$emit("update:visible", false);
      this.$emit("finish");
    },
  },
};
</script>

<style scoped lang="less"></style>
