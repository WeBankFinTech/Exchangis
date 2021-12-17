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
      <div class="cm-title">
        <span>任务配置</span>
      </div>
      <a-form-item label="执行用户" name="proxyUser">
        <a-input v-model:value="formState.proxyUser" :style="{ width: '366px'}"/>
      </a-form-item>
      <a-form-item label="执行节点" name="executeNode" >
        <a-input v-model:value="formState.executeNode" :style="{ width: '366px'}" />
      </a-form-item>
      <a-form-item label="同步方式" name="syncType">
        <a-radio-group v-model:value="formState.syncType" name="syncType">
          <a-radio :value="'FULL'"> 全量 </a-radio>
          <a-radio :value="'INCREMENTAL'"> 增量 </a-radio>
        </a-radio-group>
      </a-form-item>
      <div class="cm-title">
        <span>任务变量</span>
      </div>
      <div class="cm-button" @click="createTask">
        <PlusOutlined style="margin-right: 8px;font-size: 12px"/>
        <span>添加变量</span>
      </div>
      
      <div
        v-for="(item, index) in formState.jobParams"
        style="overflow: hidden;"
        :key="index"
      >
        <a-form-item class="w50 fl" :label="index + 1" name="jobParamsKey" labelAlign='left' :label-col='{ span: 3 }'>
          <a-input v-model:value="item.key" :style="{ width: '194px'}"/>
        </a-form-item>
        <span class="fl separator">=</span>
        <a-form-item class="w40 fl" name="jobParamsValue">
          <a-input v-model:value="item.value" :style="{ width: '194px'}"/>
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
const validateExecuteNode = async (rule, value) => {
  const ip_reg =
    /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
  if (value == "") {
    return Promise.resolve();
  }
  if (!ip_reg.test(value)) {
    return Promise.reject("请正确填写执行节点");
  } else {
    return Promise.resolve();
  }
};
const rules = {
  proxyUser: [
    { required: true, message: "请正确填写执行用户", trigger: "blur" },
  ],
  syncType: [
    { required: true, message: "请正确填写同步方式", trigger: "change" },
  ],
  executeNode: [{ validator: validateExecuteNode, trigger: "change" }],
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
      executeNode: props.formData.executeNode || "",
      jobParams: props.formData.jobParams || [],
      proxyUser: props.formData.proxyUser || "",
      syncType: props.formData.syncType || "FULL",
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
        formState["syncType"] = formData["syncType"] || "FULL";
        formState["jobParams"] = formData["jobParams"] || "";
      }
    );

    const createTask = () => {
      formState.jobParams.push({ key: "", value: "" });
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
.w50 {
  width: 48%;
}
.separator {
  margin: 5px;
}

.cm-title {
  font-family: PingFangSC-Medium;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 22px;
  font-weight: 500;
  height: 22px;
  margin-bottom: 16px;
  text-align: left;
}

.cm-button {
  height: 32px;
  width: 440px;
  line-height: 32px;
  background: #f8f9fc;
  border: 1px dashed #dee4ec;
  border-radius: 4px;
  font-family: PingFangSC-Regular;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  text-align: center;
  font-weight: 400;
  margin-bottom: 16px;
}
</style>
