<template>
  <a-modal
    title="设置"
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
      <h2>任务配置</h2>
      <a-form-item label="执行用户" name="proxyUser">
        <a-input v-model:value="formState.proxyUser" />
      </a-form-item>
      <a-form-item label="执行节点" name="executeNode">
        <a-input v-model:value="formState.executeNode" />
      </a-form-item>
      <a-form-item label="同步方式" name="syncType">
        <a-radio-group v-model:value="formState.syncType" name="syncType">
          <a-radio :value="'FULL'"> 全量 </a-radio>
          <a-radio :value="'INCREMENTAL'"> 增量 </a-radio>
        </a-radio-group>
      </a-form-item>
      <h2>任务变量 <PlusOutlined class="fr" @click="createTask" /></h2>
      <div
        v-for="(item, index) in formState.jobParams"
        style="overflow: hidden"
        :key="index"
      >
        <a-form-item class="w40 fl" :label="index + 1" name="jobParamsKey">
          <a-input v-model:value="item.key" />
        </a-form-item>
        <span class="fl separator">=</span>
        <a-form-item class="w40 fl" name="jobParamsValue">
          <a-input v-model:value="item.value" />
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>

<script>
import { toRaw, ref, watch, reactive, watchEffect } from "vue";
import { message } from "ant-design-vue";
import { PlusOutlined } from "@ant-design/icons-vue";
import { createProject, getProjectById, updateProject } from "@/common/service";
import { cloneDeep } from "lodash-es";

// 抒写JS代码与组件低耦合
const rules = {
  proxyUser: [{ required: true }],
  executeNode: [{ required: true }],
  syncType: [{ required: true }],
};
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
    id: {
      type: String,
      default: "",
    },
    formData: {
      type: Object,
      default: () => {},
    },
  },
  emits: ["finish", "cancel", "update:visible"],
  setup(props, context) {
    let confirmLoading = ref(false);
    let formState = reactive({
      ...props.formData,
      jobParams: props.formData.jobParams || [],
    });
    const formRef = ref();

    watch(
      () => props.formData,
      (newVal, odlVal) => {
        const formData = newVal;
        if (!formData.jobParams) {
          formData.jobParams = [];
        }
        const keys = formData["jobParams"]
          ? Object.keys(formData["jobParams"])
          : [];
        const jobParams = [];
        for (let i = 0; i < keys.length; i++) {
          let key = keys[i];
          let value = formData["jobParams"][key];
          const o = Object.create(null);
          o.key = key;
          o.value = value;
          jobParams.push(o);
        }
        formData["jobParams"] = jobParams;
        formState["executeNode"] = formData["executeNode"] || "";
        formState["proxyUser"] = formData["proxyUser"] || "";
        formState["syncType"] = formData["syncType"] || "";
        formState["jobParams"] = formData["jobParams"] || "";
      }
    );

    const createTask = () => {
      formState.jobParams.push({ key: "", value: "" });
      console.log(formState, formState.jobParams);
    };

    const handleOk = async () => {
      await formRef.value.validate();
      const formatData = cloneDeep(formState);
      // try {
      //   if (this.mode === "create") {
      //     confirmLoading.value = true;
      //     await createProject(formatData);
      //     message.success("创建成功");
      //   }
      //   if (this.mode === "edit") {
      //     this.confirmLoading.value = true;
      //     await updateProject(formatData);
      //     message.success("修改成功");
      //   }
      // } catch(error) {}
      confirmLoading.value = false;
      context.emit("update:visible", false);
      context.emit("finish", formatData);
    };

    return {
      formRef,
      rules,
      confirmLoading,
      formState,
      createTask,
      handleOk,
    };
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
